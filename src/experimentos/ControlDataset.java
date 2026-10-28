package experimentos;

import datos.ArchivoTSP;
import datos.Dataset;
import datos.GeneradorDataset;
import modelo.Punto;

import java.io.IOException;

public class ControlDataset {

    public static void main(String[] args) {

        try {

            // 1. Generar 100 puntos aleatorios
            Dataset original = GeneradorDataset.generarAleatorios(
                    "dataset100", 100
            );

            // 2. Guardar en disco
            String ruta = "datasets/dataset100.tsp";

            ArchivoTSP.guardar(original, ruta);

            System.out.println("Archivo guardado: " + ruta);

            // 3. Leer el archivo que acabamos de guardar
            Dataset cargado = ArchivoTSP.leer(ruta);

            System.out.println("Nombre: " + cargado.getNombre());
            System.out.println("Dimension: " + cargado.getDimension());

            Punto[] originales = original.getPuntos();
            Punto[] recuperados = cargado.getPuntos();

            boolean correcto = true;

            for (int i = 0; i < originales.length; i++) {

                if (originales[i].getId() != recuperados[i].getId()
                        || Math.abs(originales[i].getX() - recuperados[i].getX()) > 1e-10
                        || Math.abs(originales[i].getY() - recuperados[i].getY()) > 1e-10) {

                    correcto = false;
                    System.out.println("Diferencia encontrada en el punto " + i);
                }
            }

            if (correcto) {
                System.out.println("[OK] Identificadores y coordenadas correctos");
            } else {
                System.out.println("[ERROR] Diferencias al guardar y leer");
            }
            // 4. Mostrar los primeros cinco puntos
            Punto[] puntos = cargado.getPuntos();

            System.out.println("\nPrimeros 5 puntos:");

            for (int i = 0; i < Math.min(5, puntos.length); i++) {
                System.out.println(puntos[i]);
            }

            // 5. Comprobar que se han cargado todos
            if (original.getDimension() == cargado.getDimension()) {
                System.out.println("\n[OK] Dimension correcta");
            } else {
                System.out.println("\n[ERROR] Dimension incorrecta");
            }

        } catch (IOException e) {

            System.out.println("Error de archivo: " + e.getMessage());

        }
    }

}
