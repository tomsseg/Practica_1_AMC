
package datos;

import modelo.Punto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ArchivoTSP {

    // ============================================
    // LEER UN ARCHIVO TSP
    // ============================================

    public static Dataset leer(String ruta) throws IOException {

        Path archivo = Path.of(ruta);

        String nombre = archivo.getFileName().toString();
        int dimension = -1;
        Punto[] puntos = null;
        int contador = 0;

        boolean seccionCoordenadas = false;
        Set<Integer> identificadores = new HashSet<>();

        try (BufferedReader lector = Files.newBufferedReader(
                archivo, StandardCharsets.UTF_8)) {

            String linea;

            while ((linea = lector.readLine()) != null) {

                linea = linea.trim();

                if (linea.isEmpty()) {
                    continue;
                }

                // Leer cabeceras, antes de las coordenadas
                if (!seccionCoordenadas) {

                    if (linea.equalsIgnoreCase("NODE_COORD_SECTION")) {

                        if (dimension < 2) {
                            throw new IOException(
                                    "DIMENSION debe indicar al menos 2 puntos"
                            );
                        }

                        puntos = new Punto[dimension];
                        seccionCoordenadas = true;
                        continue;
                    }

                    String nombreLeido = valorCabecera(linea, "NAME");

                    if (nombreLeido != null && !nombreLeido.isBlank()) {
                        nombre = nombreLeido;
                    }

                    String dimensionLeida =
                            valorCabecera(linea, "DIMENSION");

                    if (dimensionLeida != null) {

                        try {
                            dimension = Integer.parseInt(dimensionLeida);
                        } catch (NumberFormatException e) {
                            throw new IOException(
                                    "DIMENSION no es un numero entero valido",
                                    e
                            );
                        }
                    }

                    // Las demás cabeceras no son necesarias
                    // para nuestra práctica.
                    continue;
                }

                // Final del fichero
                if (linea.equalsIgnoreCase("EOF")) {
                    break;
                }

                // Ya tenemos todos los puntos indicados
                if (contador == dimension) {
                    break;
                }

                String[] datos = linea.split("\\s+");

                if (datos.length != 3) {
                    throw new IOException(
                            "Linea de coordenadas incorrecta: " + linea
                    );
                }

                try {

                    int id = Integer.parseInt(datos[0]);
                    double x = Double.parseDouble(datos[1]);
                    double y = Double.parseDouble(datos[2]);

                    if (!Double.isFinite(x) || !Double.isFinite(y)) {
                        throw new IOException(
                                "Coordenadas no finitas en el punto " + id
                        );
                    }

                    if (!identificadores.add(id)) {
                        throw new IOException(
                                "Identificador repetido: " + id
                        );
                    }

                    puntos[contador] = new Punto(id, x, y);
                    contador++;

                } catch (NumberFormatException e) {

                    throw new IOException(
                            "Valores numericos incorrectos: " + linea,
                            e
                    );
                }
            }
        }

        if (!seccionCoordenadas) {
            throw new IOException(
                    "No se ha encontrado NODE_COORD_SECTION"
            );
        }

        if (contador != dimension) {
            throw new IOException(
                    "Se esperaban " + dimension +
                            " puntos, pero se han leido " + contador
            );
        }

        return new Dataset(nombre, puntos);
    }


    // ============================================
    // GUARDAR UN DATASET EN FORMATO TSP
    // ============================================

    public static void guardar(Dataset dataset, String ruta)
            throws IOException {

        if (dataset == null) {
            throw new IllegalArgumentException(
                    "El dataset no puede ser null"
            );
        }

        Path archivo = Path.of(ruta);

        // Crear el directorio de destino si no existe
        Path carpeta = archivo.getParent();

        if (carpeta != null) {
            Files.createDirectories(carpeta);
        }

        Punto[] puntos = dataset.copiar();

        String nombre = dataset.getNombre();

        // Evitar escribir la extensión dos veces en NAME
        if (nombre.toLowerCase(Locale.ROOT).endsWith(".tsp")) {
            nombre = nombre.substring(0, nombre.length() - 4);
        }

        try (BufferedWriter escritor = Files.newBufferedWriter(
                archivo, StandardCharsets.UTF_8)) {

            escritor.write("NAME: " + nombre);
            escritor.newLine();

            escritor.write("TYPE: TSP");
            escritor.newLine();

            escritor.write("COMMENT: Dataset generado por la practica AMC");
            escritor.newLine();

            escritor.write("DIMENSION: " + puntos.length);
            escritor.newLine();

            escritor.write("EDGE_WEIGHT_TYPE: EUC_2D");
            escritor.newLine();

            escritor.write("NODE_COORD_SECTION");
            escritor.newLine();

            // Escribir identificador, X e Y
            for (Punto punto : puntos) {

                escritor.write(String.format(
                        Locale.US,
                        "%d %.10f %.10f",
                        punto.getId(),
                        punto.getX(),
                        punto.getY()
                ));

                escritor.newLine();
            }

            escritor.write("EOF");
            escritor.newLine();
        }
    }


    // ============================================
    // OBTENER EL VALOR DE UNA CABECERA
    // ============================================

    private static String valorCabecera(String linea, String clave) {

        String texto = linea.trim();

        if (!texto.regionMatches(
                true, 0, clave, 0, clave.length())) {
            return null;
        }

        if (texto.length() == clave.length()) {
            return "";
        }

        char siguiente = texto.charAt(clave.length());

        if (siguiente != ':' && !Character.isWhitespace(siguiente)) {
            return null;
        }

        String valor = texto.substring(clave.length()).trim();

        if (valor.startsWith(":")) {
            valor = valor.substring(1).trim();
        }

        return valor;
    }
}
