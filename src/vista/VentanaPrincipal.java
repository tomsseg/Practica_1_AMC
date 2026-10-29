
package vista;

import algoritmos.Exhaustivo;
import algoritmos.ExhaustivoPoda;
import algoritmos.DivideYVenceras;
import algoritmos.DivideYVencerasMejorado;

import datos.ArchivoTSP;
import datos.Dataset;
import datos.GeneradorDataset;

import modelo.Punto;
import modelo.Solucion;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class VentanaPrincipal extends JFrame {

    private Dataset datasetActual;

    // Componentes gráficos
    private final PuntosCanvas canvas = new PuntosCanvas();

    private final JTable tabla = new JTable();
    private final JTextArea mensajes = new JTextArea(5, 40);
    private final JLabel etiquetaDataset =
            new JLabel("Dataset actual: ninguno");

    private final JButton btnAleatorio =
            new JButton("Generar aleatorio");

    private final JButton btnVertical =
            new JButton("Misma X");

    private final JButton btnCargar =
            new JButton("Cargar TSP");

    private final JButton btnComprobar =
            new JButton("Comprobar estrategias");

    private static final String CARPETA_DATOS = "datasets/";
    private static final String CARPETA_RESULTADOS = "resultados/";

    private static final String[] NOMBRES = {
            "Exhaustivo",
            "ExhaustivoPoda",
            "DivideVenceras",
            "DyVMejorado"
    };

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public VentanaPrincipal() {

        super("Practica AMC - Par de puntos mas cercano");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        crearBarraSuperior();
        crearZonaCentral();
        crearZonaInferior();
        configurarEventos();

        setSize(1250, 750);
        setMinimumSize(new Dimension(850, 500));
        setLocationRelativeTo(null);
    }

    // ==========================================
    // BARRA SUPERIOR
    // ==========================================

    private void crearBarraSuperior() {

        JPanel superior = new JPanel(new BorderLayout());

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        botones.add(btnAleatorio);
        botones.add(btnVertical);
        botones.add(btnCargar);
        botones.add(btnComprobar);

        superior.add(botones, BorderLayout.NORTH);

        etiquetaDataset.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );

        superior.add(etiquetaDataset, BorderLayout.SOUTH);

        add(superior, BorderLayout.NORTH);
    }

    // ==========================================
    // ZONA CENTRAL
    // ==========================================

    private void crearZonaCentral() {

        JSplitPane division = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT
        );

        // El 65 % del espacio inicial será para el dibujo
        division.setResizeWeight(0.65);

        canvas.setPreferredSize(new Dimension(750, 500));

        division.setLeftComponent(canvas);

        // Modelo de la tabla
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{
                        "Estrategia",
                        "Punto 1",
                        "Punto 2",
                        "Distancia",
                        "Calculadas",
                        "Tiempo (ms)"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla.setModel(modelo);
        tabla.setRowHeight(25);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(125);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(165);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(165);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(95);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(100);

        JScrollPane scrollTabla = new JScrollPane(tabla);

        division.setRightComponent(scrollTabla);

        add(division, BorderLayout.CENTER);
    }

    // ==========================================
    // ZONA INFERIOR
    // ==========================================

    private void crearZonaInferior() {

        mensajes.setEditable(false);
        mensajes.setLineWrap(true);
        mensajes.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(mensajes);

        add(scroll, BorderLayout.SOUTH);

        escribirMensaje("Aplicacion iniciada.");
    }

    // ==========================================
    // EVENTOS DE LOS BOTONES
    // ==========================================

    private void configurarEventos() {

        btnAleatorio.addActionListener(
                e -> generarDataset(false)
        );

        btnVertical.addActionListener(
                e -> generarDataset(true)
        );

        btnCargar.addActionListener(
                e -> cargarArchivo()
        );

        btnComprobar.addActionListener(
                e -> comprobarEstrategias()
        );
    }

    // ==========================================
    // GENERAR DATASET
    // ==========================================

    private void generarDataset(boolean vertical) {

        String entrada = JOptionPane.showInputDialog(
                this,
                "¿Cuantos puntos quieres generar?",
                "500"
        );

        if (entrada == null) {
            return;
        }

        try {

            int n = Integer.parseInt(entrada.trim());

            if (n < 2) {
                mostrarError("Debes generar al menos 2 puntos.");
                return;
            }

            String nombre = vertical
                    ? "vertical" + n
                    : "dataset" + n;

            String ruta = CARPETA_DATOS + nombre + ".tsp";

            File archivo = new File(ruta);

            if (archivo.exists()) {

                int respuesta = JOptionPane.showConfirmDialog(
                        this,
                        "El archivo " + ruta +
                                " ya existe.\n¿Quieres sobrescribirlo?",
                        "Confirmar",
                        JOptionPane.YES_NO_OPTION
                );

                if (respuesta != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            Dataset generado;

            if (vertical) {

                generado = GeneradorDataset.generarVertical(
                        nombre, n
                );

            } else {

                generado = GeneradorDataset.generarAleatorios(
                        nombre, n
                );
            }

            ArchivoTSP.guardar(generado, ruta);

            actualizarDataset(generado);

            escribirMensaje(
                    "Dataset generado: " + nombre +
                            " (" + n + " puntos)"
            );

            escribirMensaje("Archivo guardado: " + ruta);

        } catch (NumberFormatException ex) {

            mostrarError("Introduce un numero entero valido.");

        } catch (IOException | IllegalArgumentException ex) {

            mostrarError(ex.getMessage());
        }
    }

    // ==========================================
    // CARGAR ARCHIVO TSP
    // ==========================================

    private void cargarArchivo() {

        JFileChooser selector = new JFileChooser();

        selector.setDialogTitle("Seleccionar archivo TSP");

        selector.setFileFilter(
                new FileNameExtensionFilter(
                        "Archivos TSP (*.tsp)", "tsp"
                )
        );

        File carpeta = new File(CARPETA_DATOS);

        if (carpeta.exists()) {
            selector.setCurrentDirectory(carpeta);
        }

        int resultado = selector.showOpenDialog(this);

        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = selector.getSelectedFile();

        try {

            Dataset cargado = ArchivoTSP.leer(
                    archivo.getAbsolutePath()
            );

            actualizarDataset(cargado);

            escribirMensaje(
                    "Archivo cargado: " + archivo.getName()
            );

            escribirMensaje(
                    "Numero de puntos: " + cargado.getDimension()
            );

        } catch (IOException | IllegalArgumentException ex) {

            mostrarError(
                    "No se ha podido cargar el archivo: "
                            + ex.getMessage()
            );
        }
    }

    // ==========================================
    // ACTUALIZAR DATASET
    // ==========================================

    private void actualizarDataset(Dataset dataset) {

        datasetActual = dataset;

        etiquetaDataset.setText(
                "Dataset actual: " + dataset.getNombre() +
                        " | " + dataset.getDimension() + " puntos"
        );

        // Dibujar los puntos sin seleccionar ninguna pareja
        canvas.setData(dataset.copiar(), null);

        // Eliminar resultados del dataset anterior
        DefaultTableModel modelo =
                (DefaultTableModel) tabla.getModel();

        modelo.setRowCount(0);
    }

    // ==========================================
    // COMPROBAR ESTRATEGIAS
    // ==========================================

    private void comprobarEstrategias() {

        if (datasetActual == null) {

            mostrarError(
                    "Primero debes cargar o generar un dataset."
            );

            return;
        }

        // Capturamos el dataset que se va a procesar
        Dataset dataset = datasetActual;

        activarBotones(false);

        escribirMensaje(
                "Ejecutando estrategias sobre "
                        + dataset.getNombre() + "..."
        );

        // Ejecutar en segundo plano para no bloquear la ventana
        SwingWorker<ResultadoEjecucion[], Void> trabajador =
                new SwingWorker<>() {

                    @Override
                    protected ResultadoEjecucion[] doInBackground()
                            throws Exception {

                        ResultadoEjecucion[] resultados =
                                new ResultadoEjecucion[4];

                        for (int i = 0; i < 4; i++) {

                            Punto[] copia = dataset.copiar();

                            long inicio = System.nanoTime();

                            Solucion solucion =
                                    ejecutarAlgoritmo(i, copia);

                            long fin = System.nanoTime();

                            double tiempo =
                                    (fin - inicio) / 1_000_000.0;

                            resultados[i] = new ResultadoEjecucion(
                                    NOMBRES[i],
                                    solucion,
                                    tiempo
                            );

                            // Guardar el orden que deja el algoritmo
                            Dataset datasetResultado = new Dataset(
                                    NOMBRES[i], copia
                            );

                            ArchivoTSP.guardar(
                                    datasetResultado,
                                    CARPETA_RESULTADOS +
                                            NOMBRES[i] + ".tsp"
                            );
                        }

                        return resultados;
                    }

                    @Override
                    protected void done() {

                        activarBotones(true);

                        try {

                            ResultadoEjecucion[] resultados = get();

                            mostrarResultados(resultados);

                            // Dibujar la pareja encontrada
                            // por Divide y Venceras mejorado
                            canvas.setData(
                                    dataset.copiar(),
                                    resultados[3].solucion
                            );

                            escribirMensaje(
                                    "Estrategias calculadas correctamente."
                            );

                            escribirMensaje(
                                    "Archivos guardados en "
                                            + CARPETA_RESULTADOS
                            );

                        } catch (InterruptedException ex) {

                            Thread.currentThread().interrupt();

                            mostrarError("Calculo interrumpido.");

                        } catch (ExecutionException ex) {

                            Throwable causa = ex.getCause();

                            mostrarError(
                                    "Error al ejecutar estrategias: "
                                            + causa.getMessage()
                            );
                        }
                    }
                };

        trabajador.execute();
    }

    // ==========================================
    // EJECUTAR ALGORITMO
    // ==========================================

    private Solucion ejecutarAlgoritmo(
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
                        "Algoritmo desconocido"
                );
        }
    }

    // ==========================================
    // MOSTRAR RESULTADOS EN LA TABLA
    // ==========================================

    private void mostrarResultados(
            ResultadoEjecucion[] resultados) {

        DefaultTableModel modelo =
                (DefaultTableModel) tabla.getModel();

        modelo.setRowCount(0);

        double distanciaReferencia =
                resultados[0].solucion.getDistancia();

        boolean coinciden = true;

        for (ResultadoEjecucion resultado : resultados) {

            Solucion solucion = resultado.solucion;

            modelo.addRow(new Object[]{

                    resultado.nombre,

                    formatearPunto(solucion.getA()),

                    formatearPunto(solucion.getB()),

                    String.format(
                            Locale.US,
                            "%.8f",
                            solucion.getDistancia()
                    ),

                    solucion.getDistanciasCalculadas(),

                    String.format(
                            Locale.US,
                            "%.4f",
                            resultado.tiempo
                    )
            });

            if (Math.abs(
                    solucion.getDistancia() - distanciaReferencia
            ) > 1e-8) {

                coinciden = false;
            }
        }

        if (coinciden) {

            escribirMensaje(
                    "[OK] Las cuatro estrategias coinciden."
            );

        } else {

            escribirMensaje(
                    "[ERROR] Las estrategias no coinciden."
            );
        }
    }

    // ==========================================
    // METODOS AUXILIARES
    // ==========================================

    private String formatearPunto(Punto punto) {

        return String.format(
                Locale.US,
                "%d (%.3f, %.3f)",
                punto.getId(),
                punto.getX(),
                punto.getY()
        );
    }

    private void escribirMensaje(String texto) {

        mensajes.append(texto + "\n");

        mensajes.setCaretPosition(
                mensajes.getDocument().getLength()
        );
    }

    private void mostrarError(String texto) {

        JOptionPane.showMessageDialog(
                this,
                texto,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );

        escribirMensaje("[ERROR] " + texto);
    }

    private void activarBotones(boolean activar) {

        btnAleatorio.setEnabled(activar);
        btnVertical.setEnabled(activar);
        btnCargar.setEnabled(activar);
        btnComprobar.setEnabled(activar);
    }

    // ==========================================
    // CLASE AUXILIAR PARA GUARDAR RESULTADOS
    // ==========================================

    private static class ResultadoEjecucion {

        String nombre;
        Solucion solucion;
        double tiempo;

        ResultadoEjecucion(
                String nombre,
                Solucion solucion,
                double tiempo) {

            this.nombre = nombre;
            this.solucion = solucion;
            this.tiempo = tiempo;
        }
    }
}
