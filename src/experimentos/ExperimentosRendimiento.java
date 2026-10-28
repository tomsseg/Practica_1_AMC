package experimentos;
import algoritmos.Exhaustivo;
import algoritmos.ExhaustivoPoda;
import algoritmos.DivideYVenceras;
import algoritmos.DivideYVencerasMejorado;
import modelo.Punto;
import modelo.Solucion;

import java.util.Random;

public class ExperimentosRendimiento {

    private static final int[] TAMANOS = {10, 100, 500, 1000, 5000};

    // Semilla fija para reproducir los experimentos
    private static final Random RANDOM = new Random(12345);

    public static void main(String[] args) {

        System.out.println("==============================================");
        System.out.println("   COMPARACION DE ALGORITMOS - PRACTICA AMC");
        System.out.println("==============================================");

        // Pruebas con puntos aleatorios en el plano
        for (int n : TAMANOS) {
            Punto[] puntos = generarDataset(n, false);
            compararAlgoritmos(puntos, "CASO GENERAL");
        }

        // Pruebas del peor caso para la poda por X
        // Todos los puntos tienen la misma coordenada X
        for (int n : TAMANOS) {
            Punto[] puntos = generarDataset(n, true);
            compararAlgoritmos(puntos, "MISMA COORDENADA X");
        }

        System.out.println("\nTODAS LAS PRUEBAS FINALIZADAS");
    }

    private static Punto[] generarDataset(int n, boolean mismaX) {

        Punto[] puntos = new Punto[n];

        for (int i = 0; i < n; i++) {

            double x;
            double y;

            if (mismaX) {
                x = 1.0;

                // Coordenadas Y distintas, sin puntos duplicados
                y = i * 0.73 + RANDOM.nextDouble() * 0.5;
            } else {
                x = RANDOM.nextDouble() * 1000;
                y = RANDOM.nextDouble() * 1000;
            }

            puntos[i] = new Punto(i, x, y);
        }

        return puntos;
    }

    private static void compararAlgoritmos(Punto[] puntos, String tipo) {

        System.out.println("\n------------------------------------------------------------");
        System.out.println(tipo + " | Dataset de " + puntos.length + " puntos");
        System.out.println("------------------------------------------------------------");

        String[] nombres = {
                "Exhaustivo",
                "ExhaustivoPoda",
                "DivideVenceras",
                "DyVMejorado"
        };

        Solucion[] soluciones = new Solucion[4];
        double[] tiempos = new double[4];

        for (int algoritmo = 0; algoritmo < 4; algoritmo++) {

            // Todos los algoritmos parten del mismo dataset original
            Punto[] copia = puntos.clone();

            long inicio = System.nanoTime();

            switch (algoritmo) {
                case 0:
                    soluciones[algoritmo] = Exhaustivo.resolver(copia);
                    break;

                case 1:
                    soluciones[algoritmo] = ExhaustivoPoda.resolver(copia);
                    break;

                case 2:
                    soluciones[algoritmo] = DivideYVenceras.resolver(copia);
                    break;

                case 3:
                    soluciones[algoritmo] = DivideYVencerasMejorado.resolver(copia);
                    break;
            }

            long fin = System.nanoTime();

            tiempos[algoritmo] = (fin - inicio) / 1_000_000.0;
        }

        System.out.printf(
                "%-20s %18s %20s %15s%n",
                "Algoritmo",
                "Distancia minima",
                "Distancias calculadas",
                "Tiempo (ms)"
        );

        System.out.println(
                "----------------------------------------------------------------------------"
        );

        double distanciaReferencia = soluciones[0].getDistancia();
        boolean todosCorrectos = true;

        for (int i = 0; i < 4; i++) {

            Solucion sol = soluciones[i];

            System.out.printf(
                    "%-20s %18.8f %20d %15.4f%n",
                    nombres[i],
                    sol.getDistancia(),
                    sol.getDistanciasCalculadas(),
                    tiempos[i]
            );

            // Comparar con la solución exhaustiva
            if (Math.abs(sol.getDistancia() - distanciaReferencia) > 1e-8) {
                todosCorrectos = false;
            }
        }

        System.out.println();

        if (todosCorrectos) {
            System.out.println("[OK] Los cuatro algoritmos coinciden.");
        } else {
            System.out.println("[ERROR] Los algoritmos NO coinciden.");

            for (int i = 0; i < 4; i++) {
                System.out.println(nombres[i] + ": " + soluciones[i]);
            }
        }

        System.out.println("Pareja de referencia: "
                + soluciones[0].getA()
                + " | "
                + soluciones[0].getB());
    }
}
