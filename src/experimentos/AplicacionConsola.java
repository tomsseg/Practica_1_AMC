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

import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

public class AplicacionConsola {

    private static final Scanner teclado = new Scanner(System.in);

    // Dataset cargado actualmente en memoria
    private static Dataset datasetActual = null;

    private static final String CARPETA_DATOS = "datasets/";
    private static final String CARPETA_RESULTADOS = "resultados/";

    private static final String[] NOMBRES_ALGORITMOS = {
            "Exhaustivo",
            "ExhaustivoPoda",
            "DivideVenceras",
            "DyVMejorado"
    };

    public static void main(String[] args) {

        int opcion;

        do {
            mostrarMenu();
            opcion = leerEntero("Selecciona una opcion: ");

            try {

                switch (opcion) {

                    case 1:
                        crearFicheroAleatorio();
                        break;

                    case 2:
                        cargarDataset();
                        break;

                    case 3:
                        comprobarEstrategias();
                        break;

                    case 4:
                        estudioExperimental();
                        break;

                    case 0:
                        System.out.println("Saliendo del programa...");
                        break;

                    default:
                        System.out.println("Opcion no valida.");
                }

            } catch (IOException e) {

                System.out.println(
                        "[ERROR DE ARCHIVO] " + e.getMessage()
                );

            } catch (IllegalArgumentException e) {

                System.out.println(
                        "[ERROR] " + e.getMessage()
                );
            }

        } while (opcion != 0);

        teclado.close();
    }

    // ==========================================
    // MOSTRAR MENU
    // ==========================================

    private static void mostrarMenu() {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("        PRACTICA AMC - PUNTOS CERCANOS");
        System.out.println("==========================================");

        if (datasetActual == null) {

            System.out.println("Dataset actual: NINGUNO");

        } else {

            System.out.println(
                    "Dataset actual: " + datasetActual.getNombre()
                            + " (" + datasetActual.getDimension()
                            + " puntos)"
            );
        }

        System.out.println();
        System.out.println("1. Crear fichero TSP aleatorio");
        System.out.println("2. Cargar dataset en memoria");
        System.out.println("3. Comprobar estrategias");
        System.out.println("4. Estudio experimental");
        System.out.println("0. Salir");
        System.out.println("==========================================");
    }

    // ==========================================
    // OPCION 1 - CREAR FICHERO ALEATORIO
    // ==========================================

    private static void crearFicheroAleatorio()
            throws IOException {

        System.out.println("\n--- CREAR FICHERO TSP ALEATORIO ---");

        int n = leerTamano();

        String nombre = "dataset" + n;
        String ruta = CARPETA_DATOS + nombre + ".tsp";

        Dataset dataset = GeneradorDataset.generarAleatorios(
                nombre, n
        );

        ArchivoTSP.guardar(dataset, ruta);

        System.out.println("\n[OK] Fichero creado: " + ruta);
        System.out.println("Numero de puntos: " + n);
        System.out.println(
                "Para utilizarlo, cargalo con la opcion 2."
        );
    }

    // ==========================================
    // OPCION 2 - CARGAR DATASET
    // ==========================================

    private static void cargarDataset()
            throws IOException {

        System.out.println("\n--- CARGAR DATASET ---");
        System.out.println("1. Cargar desde un fichero .tsp");
        System.out.println("2. Generar un dataset aleatorio");
        System.out.println("3. Generar un dataset vertical");
        System.out.println("0. Volver al menu");

        int opcion = leerEntero("Selecciona una opcion: ");

        switch (opcion) {

            case 1:
                cargarDesdeArchivo();
                break;

            case 2:
                generarYCargar(false);
                break;

            case 3:
                generarYCargar(true);
                break;

            case 0:
                return;

            default:
                System.out.println("Opcion no valida.");
        }
    }

    private static void cargarDesdeArchivo()
            throws IOException {

        System.out.print("Introduce la ruta del fichero .tsp: ");

        String ruta = teclado.nextLine().trim();

        if (ruta.isEmpty()) {
            System.out.println("No has indicado ninguna ruta.");
            return;
        }

        Dataset cargado = ArchivoTSP.leer(ruta);

        // Solo sustituimos el anterior si la lectura funciona
        datasetActual = cargado;

        System.out.println("\n[OK] Dataset cargado.");
        System.out.println("Nombre: " + datasetActual.getNombre());
        System.out.println(
                "Dimension: " + datasetActual.getDimension()
        );
    }

    private static void generarYCargar(boolean vertical)
            throws IOException {

        int n = leerTamano();

        String nombre = "dataset" + n;
        String ruta = CARPETA_DATOS + nombre + ".tsp";

        Dataset generado;

        if (vertical) {

            nombre = "vertical" + n;
            ruta = CARPETA_DATOS + nombre + ".tsp";

            generado = GeneradorDataset.generarVertical(
                    nombre, n
            );

        } else {

            generado = GeneradorDataset.generarAleatorios(
                    nombre, n
            );
        }

        ArchivoTSP.guardar(generado, ruta);

        datasetActual = generado;

        System.out.println("\n[OK] Dataset generado y cargado.");
        System.out.println("Fichero: " + ruta);
        System.out.println(
                "Dimension: " + datasetActual.getDimension()
        );
    }

    // ==========================================
    // OPCION 3 - COMPROBAR ESTRATEGIAS
    // ==========================================

    private static void comprobarEstrategias()
            throws IOException {

        if (datasetActual == null) {

            System.out.println(
                    "\n[AVISO] Primero debes cargar un dataset."
            );

            return;
        }

        System.out.println("\n--- COMPROBAR ESTRATEGIAS ---");
        System.out.println("Dataset: " + datasetActual.getNombre());
        System.out.println(
                "Dimension: " + datasetActual.getDimension()
        );

        System.out.println();

        System.out.printf(
                "%-18s %-28s %-28s %14s %12s %14s%n",
                "Estrategia",
                "Punto 1",
                "Punto 2",
                "Distancia",
                "Calculadas",
                "Tiempo (ms)"
        );

        System.out.println("-".repeat(120));

        double referencia = Double.NaN;
        boolean coinciden = true;

        for (int i = 0; i < 4; i++) {

            // Cada algoritmo recibe una copia independiente
            Punto[] puntos = datasetActual.copiar();

            long inicio = System.nanoTime();

            Solucion sol = ejecutarAlgoritmo(i, puntos);

            long fin = System.nanoTime();

            double tiempo = (fin - inicio) / 1_000_000.0;

            System.out.printf(
                    Locale.US,
                    "%-18s %-28s %-28s %14.8f %12d %14.4f%n",
                    NOMBRES_ALGORITMOS[i],
                    formatearPunto(sol.getA()),
                    formatearPunto(sol.getB()),
                    sol.getDistancia(),
                    sol.getDistanciasCalculadas(),
                    tiempo
            );

            // Comprobar que los resultados coinciden
            if (i == 0) {

                referencia = sol.getDistancia();

            } else if (Math.abs(
                    sol.getDistancia() - referencia
            ) > 1e-8) {

                coinciden = false;
            }

            // Guardar el array DESPUES del algoritmo.
            // Exhaustivo conserva el orden original.
            // Los otros tres quedan ordenados por X.
            Dataset resultado = new Dataset(
                    NOMBRES_ALGORITMOS[i],
                    puntos
            );

            String ruta = CARPETA_RESULTADOS
                    + NOMBRES_ALGORITMOS[i] + ".tsp";

            ArchivoTSP.guardar(resultado, ruta);
        }

        System.out.println();

        if (coinciden) {

            System.out.println(
                    "[OK] Las cuatro estrategias coinciden."
            );

        } else {

            System.out.println(
                    "[ERROR] Las estrategias no coinciden."
            );
        }

        System.out.println(
                "Ficheros guardados en: " + CARPETA_RESULTADOS
        );
    }

    // ==========================================
    // EJECUTAR UN ALGORITMO
    // ==========================================

    private static Solucion ejecutarAlgoritmo(
            int algoritmo, Punto[] puntos) {

        switch (algoritmo) {

            case 0:
                return Exhaustivo.resolver(puntos);

            case 1:
                return ExhaustivoPoda.resolver(puntos);

            case 2:
                return DivideYVenceras.resolver(puntos);

            case 3:
                return DivideYVencerasMejorado.resolver(puntos);

            default:
                throw new IllegalArgumentException(
                        "Algoritmo no valido"
                );
        }
    }

    // ==========================================
    // OPCION 4 - ESTUDIO EXPERIMENTAL
    // ==========================================

    private static void estudioExperimental() {

        System.out.println("\n--- ESTUDIO EXPERIMENTAL ---");

        int[] tamanos = {200, 500, 1500, 5000};
        int repeticiones = 10;

        System.out.println(
                "Se realizaran " + repeticiones
                        + " experimentos por tamaño."
        );

        System.out.println(
                "Cada algoritmo recibira los mismos puntos."
        );

        for (int n : tamanos) {

            double[] tiemposAcumulados = new double[4];
            long[] calculosAcumulados = new long[4];

            boolean correcto = true;

            System.out.println(
                    "\nProcesando datasets de " + n + " puntos..."
            );

            for (int repeticion = 0;
                 repeticion < repeticiones;
                 repeticion++) {

                // Generamos un nuevo dataset para cada repeticion
                Dataset dataset =
                        GeneradorDataset.generarAleatorios(
                                "experimento" + n,
                                n
                        );

                double distanciaReferencia = Double.NaN;

                for (int algoritmo = 0;
                     algoritmo < 4;
                     algoritmo++) {

                    Punto[] copia = dataset.copiar();

                    long inicio = System.nanoTime();

                    Solucion sol = ejecutarAlgoritmo(
                            algoritmo, copia
                    );

                    long fin = System.nanoTime();

                    double tiempo =
                            (fin - inicio) / 1_000_000.0;

                    tiemposAcumulados[algoritmo] += tiempo;

                    calculosAcumulados[algoritmo] +=
                            sol.getDistanciasCalculadas();

                    if (algoritmo == 0) {

                        distanciaReferencia = sol.getDistancia();

                    } else if (Math.abs(
                            sol.getDistancia() - distanciaReferencia
                    ) > 1e-8) {

                        correcto = false;

                        System.out.println(
                                "[ERROR] No coinciden en repeticion "
                                        + (repeticion + 1)
                        );
                    }
                }
            }

            System.out.println(
                    "\nRESULTADOS PARA N = " + n
            );

            System.out.printf(
                    "%-20s %20s %20s%n",
                    "Algoritmo",
                    "Media calculadas",
                    "Tiempo medio (ms)"
            );

            System.out.println("-".repeat(65));

            for (int i = 0; i < 4; i++) {

                double mediaCalculos =
                        calculosAcumulados[i]
                                / (double) repeticiones;

                double mediaTiempo =
                        tiemposAcumulados[i]
                                / repeticiones;

                System.out.printf(
                        Locale.US,
                        "%-20s %20.2f %20.4f%n",
                        NOMBRES_ALGORITMOS[i],
                        mediaCalculos,
                        mediaTiempo
                );
            }

            if (correcto) {
                System.out.println(
                        "[OK] Todas las distancias coinciden."
                );
            }
        }

        System.out.println(
                "\n[OK] Estudio experimental finalizado."
        );
    }

    // ==========================================
    // METODOS AUXILIARES
    // ==========================================

    private static int leerEntero(String mensaje) {

        while (true) {

            System.out.print(mensaje);
            String entrada = teclado.nextLine().trim();

            try {

                return Integer.parseInt(entrada);

            } catch (NumberFormatException e) {

                System.out.println(
                        "Introduce un numero entero valido."
                );
            }
        }
    }

    private static int leerTamano() {

        while (true) {

            int n = leerEntero(
                    "Introduce el numero de puntos (minimo 2): "
            );

            if (n >= 2) {
                return n;
            }

            System.out.println(
                    "El tamaño debe ser igual o mayor que 2."
            );
        }
    }

    private static String formatearPunto(Punto punto) {

        return String.format(
                Locale.US,
                "%d (%.3f, %.3f)",
                punto.getId(),
                punto.getX(),
                punto.getY()
        );
    }
}
