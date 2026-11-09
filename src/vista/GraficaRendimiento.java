package vista;

import experimentos.ExperimentosRendimiento;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Grafica de lineas de los resultados medios del estudio experimental. */
public class GraficaRendimiento extends JPanel {

    public enum Metrica { TIEMPO, DISTANCIAS }

    private static final Color[] COLORES = {
            new Color(36, 99, 185),
            new Color(220, 86, 52),
            new Color(33, 145, 104),
            new Color(140, 86, 178)
    };

    private final Metrica metrica;
    private List<ExperimentosRendimiento.Resultado> resultados = List.of();

    public GraficaRendimiento(Metrica metrica) {
        this.metrica = metrica;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(820, 440));
    }

    public void setResultados(List<ExperimentosRendimiento.Resultado> resultados) {
        this.resultados = List.copyOf(resultados);
        repaint();
    }

    /** Exporta exactamente la grafica que se dibuja en el panel, pero con mayor resolucion. */
    public void guardarPNG(Path ruta) throws IOException {
        int ancho = Math.max(1100, getWidth());
        int alto = Math.max(650, getHeight());
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = imagen.createGraphics();
        try {
            dibujar(g2, ancho, alto);
        } finally {
            g2.dispose();
        }
        Path carpeta = ruta.getParent();
        if (carpeta != null) Files.createDirectories(carpeta);
        if (!ImageIO.write(imagen, "png", ruta.toFile())) {
            throw new IOException("No se ha podido codificar el archivo PNG");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            dibujar(g2, getWidth(), getHeight());
        } finally {
            g2.dispose();
        }
    }

    private void dibujar(Graphics2D g, int ancho, int alto) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, ancho, alto);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (resultados.isEmpty()) {
            g.setColor(Color.GRAY);
            g.drawString("La grafica aparecera al finalizar los experimentos.", 24, 35);
            return;
        }

        // LinkedHashMap mantiene la misma disposicion en la leyenda.
        Map<String, List<ExperimentosRendimiento.Resultado>> series = new LinkedHashMap<>();
        for (ExperimentosRendimiento.Resultado r : resultados) {
            series.computeIfAbsent(r.algoritmo(), k -> new ArrayList<>()).add(r);
        }
        for (List<ExperimentosRendimiento.Resultado> serie : series.values()) {
            serie.sort(Comparator.comparingInt(ExperimentosRendimiento.Resultado::tamano));
        }

        int izquierda = 90;
        int derecha = 34;
        int arriba = 75;
        int abajo = 100;
        int areaW = Math.max(1, ancho - izquierda - derecha);
        int areaH = Math.max(1, alto - arriba - abajo);

        int minN = resultados.stream().mapToInt(ExperimentosRendimiento.Resultado::tamano)
                .min().orElse(0);
        int maxN = resultados.stream().mapToInt(ExperimentosRendimiento.Resultado::tamano)
                .max().orElse(1);
        double maxValor = resultados.stream().mapToDouble(this::valor)
                .max().orElse(1);
        if (!(maxValor > 0) || !Double.isFinite(maxValor)) maxValor = 1;
        double limiteY = maxValor * 1.08;

        g.setColor(new Color(35, 42, 53));
        g.setFont(g.getFont().deriveFont(java.awt.Font.BOLD, 16f));
        String titulo = metrica == Metrica.TIEMPO
                ? "Tiempo medio por tamano" : "Distancias euclideas medias por tamano";
        g.drawString(titulo, izquierda, 30);

        g.setFont(g.getFont().deriveFont(java.awt.Font.PLAIN, 12f));
        for (int i = 0; i <= 5; i++) {
            int py = arriba + areaH - (int) Math.round(i * areaH / 5.0);
            double val = limiteY * i / 5.0;
            g.setColor(new Color(230, 233, 237));
            g.drawLine(izquierda, py, izquierda + areaW, py);
            g.setColor(new Color(70, 78, 88));
            String txt = formatearValor(val);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(txt, izquierda - 12 - fm.stringWidth(txt), py + 4);
        }
        g.setColor(new Color(75, 85, 95));
        g.drawLine(izquierda, arriba, izquierda, arriba + areaH);
        g.drawLine(izquierda, arriba + areaH, izquierda + areaW, arriba + areaH);

        // Etiquetas del eje X en cada tamano de muestra distinto.
        List<Integer> tamanos = resultados.stream()
                .map(ExperimentosRendimiento.Resultado::tamano)
                .distinct().sorted().toList();
        for (int n : tamanos) {
            int x = coordX(n, minN, maxN, izquierda, areaW);
            String etiqueta = Integer.toString(n);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(etiqueta, x - fm.stringWidth(etiqueta) / 2,
                    arriba + areaH + 20);
        }
        String ejeX = "Numero de puntos (N)";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(ejeX, izquierda + (areaW - fm.stringWidth(ejeX)) / 2,
                arriba + areaH + 43);
        g.drawString(metrica == Metrica.TIEMPO ? "Tiempo (ms)" : "Distancias calculadas",
                izquierda, 52);

        int indice = 0;
        for (Map.Entry<String, List<ExperimentosRendimiento.Resultado>> entry : series.entrySet()) {
            Color color = colorDe(entry.getKey(), indice++);
            g.setColor(color);
            g.setStroke(new BasicStroke(2.5f));
            int anteriorX = -1, anteriorY = -1;
            for (ExperimentosRendimiento.Resultado r : entry.getValue()) {
                int x = coordX(r.tamano(), minN, maxN, izquierda, areaW);
                int y = arriba + areaH -
                        (int) Math.round(valor(r) * areaH / limiteY);
                if (anteriorX >= 0) g.drawLine(anteriorX, anteriorY, x, y);
                g.fillOval(x - 4, y - 4, 8, 8);
                anteriorX = x;
                anteriorY = y;
            }
        }

        // Leyenda en la parte inferior, debajo del eje X.
        g.setStroke(new BasicStroke(1.8f));
        int xLeyenda = izquierda;
        int yLeyenda = alto - 20;
        indice = 0;
        for (String nombre : series.keySet()) {
            g.setColor(colorDe(nombre, indice++));
            g.drawLine(xLeyenda, yLeyenda - 4, xLeyenda + 19, yLeyenda - 4);
            g.fillOval(xLeyenda + 7, yLeyenda - 8, 8, 8);
            g.setColor(new Color(35, 42, 53));
            g.drawString(nombre, xLeyenda + 25, yLeyenda);
            xLeyenda += 35 + g.getFontMetrics().stringWidth(nombre);
        }
    }

    private double valor(ExperimentosRendimiento.Resultado r) {
        return metrica == Metrica.TIEMPO ? r.tiempoMedioMs() : r.distanciasMedias();
    }

    private static int coordX(int n, int minimo, int maximo, int inicio, int ancho) {
        if (minimo == maximo) return inicio + ancho / 2;
        return inicio + (int) Math.round((n - minimo) * ancho / (double) (maximo - minimo));
    }

    private static Color colorDe(String algoritmo, int indice) {
        for (int i = 0; i < ExperimentosRendimiento.NOMBRES.length; i++) {
            if (ExperimentosRendimiento.NOMBRES[i].equals(algoritmo)) {
                return COLORES[i % COLORES.length];
            }
        }
        return COLORES[indice % COLORES.length];
    }

    private String formatearValor(double valor) {
        if (metrica == Metrica.TIEMPO) {
            return String.format(Locale.US, "%.2f", valor);
        }
        if (valor >= 1_000_000) return String.format(Locale.US, "%.1fM", valor / 1_000_000);
        if (valor >= 10_000) return String.format(Locale.US, "%.0fk", valor / 1_000);
        return String.format(Locale.US, "%.0f", valor);
    }
}
