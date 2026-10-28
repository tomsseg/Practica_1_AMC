package experimentos;

import algoritmos.Exhaustivo;
import algoritmos.ExhaustivoPoda;
import algoritmos.DivideYVenceras;
import algoritmos.DivideYVencerasMejorado;

import modelo.Punto;
import modelo.Solucion;

import java.util.Random;

public class PruebasAlgoritmos {

    private static final double TOLERANCIA = 1e-8;

    private static int pruebasSuperadas = 0;
    private static int pruebasFallidas = 0;

    public static void main(String[] args) {

        System.out.println("==========================================");
        System.out.println("    PRUEBAS AUTOMATICAS - PRACTICA AMC");
        System.out.println("==========================================");

        // 1. Casos especiales
        System.out.println("\n========== CASOS ESPECIALES ==========");

        probar("Dos puntos", new Punto[]{
                new Punto(0, 0, 0),
                new Punto(1, 3, 4)
        });

        probar("Tres puntos", new Punto[]{
                new Punto(0, 0, 0),
                new Punto(1, 5, 5),
                new Punto(2, 1, 1)
        });

        probar("Coordenadas negativas", new Punto[]{
                new Punto(0, -10, -10),
                new Punto(1, -11, -11),
                new Punto(2, 20, 30),
                new Punto(3, -5, 8)
        });

        probar("Puntos repetidos", new Punto[]{
                new Punto(0, 3, 4),
                new Punto(1, 8, 9),
                new Punto(2, 3, 4),
                new Punto(3, 15, 20)
        });

        probar("Todos con misma X", new Punto[]{
                new Punto(0, 2, 10),
                new Punto(1, 2, 4),
                new Punto(2, 2, 8),
                new Punto(3, 2, 12),
                new Punto(4, 2, 5)
        });

        probar("Todos con misma Y", new Punto[]{
                new Punto(0, 1, 5),
                new Punto(1, 10, 5),
                new Punto(2, 4, 5),
                new Punto(3, 3, 5),
                new Punto(4, -2, 5)
        });

        probar("Todos los puntos coinciden", new Punto[]{
                new Punto(0, 7, 7),
                new Punto(1, 7, 7),
                new Punto(2, 7, 7),
                new Punto(3, 7, 7),
                new Punto(4, 7, 7)
        });

        probar("Puntos alineados diagonalmente", new Punto[]{
                new Punto(0, 0, 0),
                new Punto(1, 2, 2),
                new Punto(2, 4, 4),
                new Punto(3, 6, 6),
                new Punto(4, 8, 8),
                new Punto(5, 10, 10)
        });

        probar("Coordenadas grandes", new Punto[]{
                new Punto(0, 100000, 100000),
                new Punto(1, 100001, 100001),
                new Punto(2, -100000, -100000),
                new Punto(3, 500000, 500000)
        });

        // 2. Pruebas aleatorias
        System.out.println("\n========== PRUEBAS ALEATORIAS ==========");

        int[] tamanos = {10, 100, 500, 1000, 5000};

        int repeticiones = 10;

        Random random = new Random(12345);

        for (int n : tamanos) {

            System.out.println("\n--- Datasets de " + n + " puntos ---");

            for (int k = 1; k <= repeticiones; k++) {

                Punto[] puntos = generarAleatorios(n, random);

                probar("Aleatorio N=" + n + " #" + k, puntos);
            }
        }

        // 3. Pruebas aleatorias con coordenadas repetidas
        System.out.println(
                "\n========== COORDENADAS ENTERAS REPETIDAS =========="
        );

        for (int n : tamanos) {

            Punto[] puntos = generarEnteros(n, random);

            probar("Enteros repetidos N=" + n, puntos);
        }

        // 4. Pruebas verticales
        System.out.println("\n========== PUNTOS CON MISMA X ==========");

        for (int n : tamanos) {

            Punto[] puntos = generarVerticales(n, random);

            probar("Vertical N=" + n, puntos);
        }

        // 5. Resumen
        System.out.println("\n==========================================");
        System.out.println("          RESUMEN DE RESULTADOS");
        System.out.println("==========================================");

        System.out.println("Pruebas superadas: " + pruebasSuperadas);
        System.out.println("Pruebas fallidas:  " + pruebasFallidas);
        System.out.println("Total de pruebas: " +
                (pruebasSuperadas + pruebasFallidas));

        if (pruebasFallidas == 0) {
            System.out.println("\n[OK] TODAS LAS PRUEBAS SUPERADAS");
        } else {
            System.out.println("\n[ERROR] HAY PRUEBAS FALLIDAS");
        }
    }

    // ==========================================
    // EJECUTAR Y COMPARAR LOS CUATRO ALGORITMOS
    // ==========================================

    private static void probar(String nombre, Punto[] puntos) {

        Solucion[] soluciones = new Solucion[4];

        String[] algoritmos = {
                "Exhaustivo",
                "ExhaustivoPoda",
                "DivideYVenceras",
                "DivideYVencerasMejorado"
        };

        try {

            // Clonamos para evitar que los algoritmos
            // modifiquen el orden original.
            soluciones[0] = Exhaustivo.resolver(puntos.clone());

            soluciones[1] = ExhaustivoPoda.resolver(puntos.clone());

            soluciones[2] = DivideYVenceras.resolver(puntos.clone());

            soluciones[3] = DivideYVencerasMejorado.resolver(puntos.clone());

            double distanciaCorrecta = soluciones[0].getDistancia();

            boolean correcto = true;

            for (int i = 0; i < soluciones.length; i++) {

                Solucion sol = soluciones[i];

                // La distancia debe coincidir con el exhaustivo.
                if (!Double.isFinite(sol.getDistancia()) ||
                        Math.abs(sol.getDistancia() - distanciaCorrecta)
                                > TOLERANCIA) {

                    correcto = false;
                }

                // La pareja devuelta tiene que ser válida.
                if (sol.getA() == null || sol.getB() == null ||
                        sol.getA() == sol.getB()) {

                    correcto = false;

                } else {

                    double distanciaPareja = Math.hypot(
                            sol.getA().getX() - sol.getB().getX(),
                            sol.getA().getY() - sol.getB().getY()
                    );

                    if (Math.abs(distanciaPareja - sol.getDistancia())
                            > TOLERANCIA) {
                        correcto = false;
                    }
                }

                // El contador no puede ser negativo.
                if (sol.getDistanciasCalculadas() < 0) {
                    correcto = false;
                }
            }

            if (correcto) {

                pruebasSuperadas++;

                System.out.println("[OK] " + nombre);

            } else {

                pruebasFallidas++;

                System.out.println("[ERROR] " + nombre);

                for (int i = 0; i < soluciones.length; i++) {

                    System.out.printf(
                            "  %-26s Distancia: %.10f | Calculos: %d%n",
                            algoritmos[i],
                            soluciones[i].getDistancia(),
                            soluciones[i].getDistanciasCalculadas()
                    );

                    System.out.println(
                            "     Pareja: " +
                                    soluciones[i].getA() + " | " +
                                    soluciones[i].getB()
                    );
                }

                imprimirPuntosSiPequeno(puntos);
            }

        } catch (Exception e) {

            pruebasFallidas++;

            System.out.println("[EXCEPCION] " + nombre);
            System.out.println("  " + e);

            imprimirPuntosSiPequeno(puntos);

            e.printStackTrace(System.out);
        }
    }

    // ==========================================
    // GENERADORES
    // ==========================================

    private static Punto[] generarAleatorios(int n, Random random) {

        Punto[] puntos = new Punto[n];

        for (int i = 0; i < n; i++) {

            double x = -1000 + random.nextDouble() * 2000;
            double y = -1000 + random.nextDouble() * 2000;

            puntos[i] = new Punto(i, x, y);
        }

        return puntos;
    }

    private static Punto[] generarEnteros(int n, Random random) {

        Punto[] puntos = new Punto[n];

        for (int i = 0; i < n; i++) {

            // Rango pequeño para provocar coordenadas repetidas
            double x = random.nextInt(21) - 10;
            double y = random.nextInt(21) - 10;

            puntos[i] = new Punto(i, x, y);
        }

        return puntos;
    }

    private static Punto[] generarVerticales(int n, Random random) {

        Punto[] puntos = new Punto[n];

        for (int i = 0; i < n; i++) {

            double x = 5.0;
            double y = i * 0.73 + random.nextDouble() * 0.5;

            puntos[i] = new Punto(i, x, y);
        }

        return puntos;
    }

    // ==========================================
    // MOSTRAR DATOS SI FALLA UNA PRUEBA PEQUEÑA
    // ==========================================

    private static void imprimirPuntosSiPequeno(Punto[] puntos) {

        if (puntos.length <= 20) {

            System.out.println("  Puntos del dataset:");

            for (Punto punto : puntos) {
                System.out.println("    " + punto);
            }
        }
    }
}
