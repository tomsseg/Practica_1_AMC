
package experimentos;

import algoritmos.Exhaustivo;
import algoritmos.ExhaustivoPoda;
import algoritmos.DivideYVenceras;
import algoritmos.DivideYVencerasMejorado;

import datos.ArchivoTSP;
import datos.Dataset;
import datos.GeneradorDataset;

import modelo.Punto;
import modelo.Solucion;

import java.io.BufferedWriter;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ExperimentosRendimiento {

    public static final int[] TAMANOS = {
            200, 500, 1500, 5000
    };

    public static final int REPETICIONES = 10;

    public static final String[] NOMBRES = {
            "Exhaustivo",
            "ExhaustivoPoda",
            "DivideVenceras",
            "DyVMejorado"
    };

    // Un resultado por tamaño y algoritmo
    public record Resultado(
            int tamano,
            String algoritmo,
            double tiempoMedioMs,
            double distanciasMedias
    ) {}

    // ============================================
    // COMPARAR TODAS LAS ESTRATEGIAS
    // ============================================

    public static List<Resultado> compararTodas(
            int[] tamanos,
            int repeticiones,
            boolean vertical) {

        return ejecutar(
                tamanos,
                repeticiones,
                vertical,
                new int[]{0, 1, 2, 3}
        );
    }

    // ============================================
    // COMPARAR DOS ESTRATEGIAS
    // ============================================

    public static List<Resultado> compararDos(
            int[] tamanos,
            int repeticiones,
            boolean vertical,
            int algoritmo1,
            int algoritmo2) {

        if (algoritmo1 == algoritmo2) {
            throw new IllegalArgumentException(
                    "Selecciona dos estrategias distintas"
            );
        }

        return ejecutar(
                tamanos,
                repeticiones,
                vertical,
                new int[]{algoritmo1, algoritmo2}
        );
    }

    // ============================================
    // ESTUDIO EXPERIMENTAL GENERAL
    // ============================================

    private static List<Resultado> ejecutar(
            int[] tamanos,
            int repeticiones,
            boolean vertical,
            int[] algoritmos) {

        validarParametros(
                tamanos,
                repeticiones,
                algoritmos
        );

        List<Resultado> resultados = new ArrayList<>();

        // Calentamiento previo de la JVM
        calentarJVM(algoritmos);

        for (int n : tamanos) {

            double[] tiempos = new double[algoritmos.length];
            long[] distancias = new long[algoritmos.length];

            for (int repeticion = 0;
                 repeticion < repeticiones;
                 repeticion++) {

                // Nuevo dataset para cada experimento
                Dataset dataset = vertical
                        ? GeneradorDataset.generarVertical(
                        "vertical" + n, n
                )
                        : GeneradorDataset.generarAleatorios(
                        "dataset" + n, n
                );

                double distanciaReferencia = Double.NaN;

                // Todos los algoritmos utilizan los mismos puntos
                for (int i = 0; i < algoritmos.length; i++) {

                    Punto[] copia = dataset.copiar();

                    long inicio = System.nanoTime();

                    Solucion solucion = ejecutarAlgoritmo(
                            algoritmos[i], copia
                    );

                    long fin = System.nanoTime();

                    tiempos[i] +=
                            (fin - inicio) / 1_000_000.0;

                    distancias[i] +=
                            solucion.getDistanciasCalculadas();

                    // Verificar que las estrategias coinciden
                    if (i == 0) {

                        distanciaReferencia =
                                solucion.getDistancia();

                    } else if (!Double.isFinite(
                            solucion.getDistancia()
                    ) || Math.abs(
                            solucion.getDistancia()
                                    - distanciaReferencia
                    ) > 1e-8) {

                        throw new IllegalStateException(
                                "Las estrategias no coinciden. " +
                                        "N=" + n +
                                        ", repeticion=" + (repeticion + 1) +
                                        ", algoritmo=" +
                                        NOMBRES[algoritmos[i]]
                        );
                    }
                }
            }

            // Obtener resultados medios
            for (int i = 0; i < algoritmos.length; i++) {

                resultados.add(new Resultado(
                        n,
                        NOMBRES[algoritmos[i]],
                        tiempos[i] / repeticiones,
                        distancias[i] / (double) repeticiones
                ));
            }

            System.out.println(
                    "[OK] Experimentos completados para N=" + n
            );
        }

        return resultados;
    }

    // ============================================
    // EJECUTAR UN ALGORITMO
    // ============================================

    private static Solucion ejecutarAlgoritmo(
            int algoritmo,
            Punto[] puntos) {

        return switch (algoritmo) {

            case 0 -> Exhaustivo.resolver(puntos);

            case 1 -> ExhaustivoPoda.resolver(puntos);

            case 2 -> DivideYVenceras.resolver(puntos);

            case 3 -> DivideYVencerasMejorado.resolver(puntos);

            default -> throw new IllegalArgumentException(
                    "Algoritmo desconocido: " + algoritmo
            );
        };
    }

    // ============================================
    // CALENTAR LA JVM
    // ============================================

    private static void calentarJVM(int[] algoritmos) {

        // Evitar medir únicamente las primeras ejecuciones,
        // en las que Java todavía puede estar compilando código.

        Dataset dataset =
                GeneradorDataset.generarAleatorios(
                        "calentamiento", 200
                );

        for (int repeticion = 0; repeticion < 3; repeticion++) {

            for (int algoritmo : algoritmos) {

                ejecutarAlgoritmo(
                        algoritmo,
                        dataset.copiar()
                );
            }
        }
    }

    // ============================================
    // MOSTRAR TABLA DE RESULTADOS
    // ============================================

    public static void mostrarResultados(
            List<Resultado> resultados) {

        System.out.println();

        System.out.printf(
                "%-8s %-20s %20s %20s%n",
                "N",
                "Algoritmo",
                "Tiempo medio (ms)",
                "Distancias medias"
        );

        System.out.println("-".repeat(75));

        for (Resultado r : resultados) {

            System.out.printf(
                    Locale.US,
                    "%-8d %-20s %20.4f %20.2f%n",
                    r.tamano(),
                    r.algoritmo(),
                    r.tiempoMedioMs(),
                    r.distanciasMedias()
            );
        }
    }

    // ============================================
    // EXPORTAR RESULTADOS A CSV
    // ============================================

    public static void guardarCSV(
            List<Resultado> resultados,
            String ruta) throws IOException {

        Path archivo = Path.of(ruta);

        Path carpeta = archivo.getParent();

        if (carpeta != null) {
            Files.createDirectories(carpeta);
        }

        try (BufferedWriter escritor =
                     Files.newBufferedWriter(
                             archivo,
                             StandardCharsets.UTF_8
                     )) {

            // Separador ; para facilitar la importación
            // en hojas de cálculo españolas.
            escritor.write(
                    "N;Algoritmo;TiempoMedioMs;DistanciasMedias"
            );

            escritor.newLine();

            for (Resultado r : resultados) {

                escritor.write(String.format(
                        Locale.US,
                        "%d;%s;%.6f;%.2f",
                        r.tamano(),
                        r.algoritmo(),
                        r.tiempoMedioMs(),
                        r.distanciasMedias()
                ));

                escritor.newLine();
            }
        }
    }

    // ============================================
    // VALIDAR LOS PARAMETROS
    // ============================================

    private static void validarParametros(
            int[] tamanos,
            int repeticiones,
            int[] algoritmos) {

        if (tamanos == null || tamanos.length == 0) {
            throw new IllegalArgumentException(
                    "Debes indicar al menos un tamaño"
            );
        }

        for (int n : tamanos) {

            if (n < 2) {
                throw new IllegalArgumentException(
                        "Los tamaños deben ser mayores o iguales a 2"
                );
            }
        }

        if (repeticiones < 1) {
            throw new IllegalArgumentException(
                    "Debe realizarse al menos una repeticion"
            );
        }

        if (algoritmos == null || algoritmos.length == 0) {
            throw new IllegalArgumentException(
                    "Debes seleccionar al menos una estrategia"
            );
        }

        Set<Integer> seleccionados = new HashSet<>();

        for (int algoritmo : algoritmos) {

            if (algoritmo < 0 || algoritmo >= NOMBRES.length) {
                throw new IllegalArgumentException(
                        "Identificador de estrategia incorrecto"
                );
            }

            if (!seleccionados.add(algoritmo)) {
                throw new IllegalArgumentException(
                        "No se puede repetir una estrategia"
                );
            }
        }
    }

    // ============================================
    // MAIN PARA PROBAR EL ESTUDIO
    // ============================================

    public static void main(String[] args) {

        try {

            System.out.println("ESTUDIO EXPERIMENTAL - CASO GENERAL");

            List<Resultado> general = compararTodas(
                    TAMANOS,
                    REPETICIONES,
                    false
            );

            mostrarResultados(general);

            guardarCSV(
                    general,
                    "experimentosCSV/caso_general.csv"
            );

            System.out.println("\nESTUDIO EXPERIMENTAL - MISMA X");

            List<Resultado> vertical = compararTodas(
                    TAMANOS,
                    REPETICIONES,
                    true
            );

            mostrarResultados(vertical);

            guardarCSV(
                    vertical,
                    "experimentosCSV/caso_vertical.csv"
            );

            System.out.println(
                    "\n[OK] CSV generados correctamente."
            );

        } catch (IOException | IllegalArgumentException |
                 IllegalStateException e) {

            System.err.println("[ERROR] " + e.getMessage());
        }
    }
}
