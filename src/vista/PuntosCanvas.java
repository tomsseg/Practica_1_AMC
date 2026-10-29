
package vista;

import modelo.Punto;
import modelo.Solucion;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class PuntosCanvas extends Canvas {

    private Punto[] puntos;
    private Solucion solucion;

    // Imagen auxiliar para evitar parpadeos
    private BufferedImage backBuffer;

    private static final int MARGEN = 35;

    public PuntosCanvas() {
        setBackground(Color.WHITE);
    }

    // Actualizar el dataset y la pareja seleccionada
    public void setData(Punto[] puntos, Solucion solucion) {
        this.puntos = puntos == null ? null : puntos.clone();
        this.solucion = solucion;
        repaint();
    }

    @Override
    public void paint(Graphics g) {

        int ancho = Math.max(1, getWidth());
        int alto = Math.max(1, getHeight());

        // Crear o actualizar el buffer cuando cambie el tamaño
        if (backBuffer == null
                || backBuffer.getWidth() != ancho
                || backBuffer.getHeight() != alto) {

            backBuffer = new BufferedImage(
                    ancho,
                    alto,
                    BufferedImage.TYPE_INT_ARGB
            );
        }

        Graphics2D g2 = backBuffer.createGraphics();

        try {

            // Limpiar el fondo
            g2.setColor(getBackground());
            g2.fillRect(0, 0, ancho, alto);

            // Suavizar líneas y círculos
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (puntos == null || puntos.length == 0) {
                g2.setColor(Color.GRAY);
                g2.drawString(
                        "Carga o genera un dataset para visualizarlo",
                        20, 30
                );
            } else {

                dibujarPuntos(g2, ancho, alto);
            }

        } finally {
            g2.dispose();
        }

        // Mostrar la imagen completa en pantalla
        g.drawImage(backBuffer, 0, 0, null);
    }

    private void dibujarPuntos(Graphics2D g2, int ancho, int alto) {

        // 1. Calcular los límites del dataset
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (Punto punto : puntos) {

            minX = Math.min(minX, punto.getX());
            maxX = Math.max(maxX, punto.getX());

            minY = Math.min(minY, punto.getY());
            maxY = Math.max(maxY, punto.getY());
        }

        // 2. Calcular una escala común para los dos ejes
        double rangoX = maxX - minX;
        double rangoY = maxY - minY;

        double espacioX = Math.max(1, ancho - 2.0 * MARGEN);
        double espacioY = Math.max(1, alto - 2.0 * MARGEN);

        double escalaX = rangoX == 0
                ? Double.POSITIVE_INFINITY
                : espacioX / rangoX;

        double escalaY = rangoY == 0
                ? Double.POSITIVE_INFINITY
                : espacioY / rangoY;

        double escala = Math.min(escalaX, escalaY);

        // Caso especial: todos los puntos coinciden
        if (!Double.isFinite(escala)) {
            escala = 1;
        }

        // 3. Centrar el conjunto de puntos
        double dibujoAncho = rangoX * escala;
        double dibujoAlto = rangoY * escala;

        double desplazamientoX = (ancho - dibujoAncho) / 2.0;
        double desplazamientoY = (alto - dibujoAlto) / 2.0;

        // 4. Dibujar todos los puntos
        g2.setColor(new Color(70, 70, 70));

        for (Punto punto : puntos) {

            int x = transformarX(
                    punto.getX(), minX, escala, desplazamientoX
            );

            int y = transformarY(
                    punto.getY(), minY, escala,
                    desplazamientoY, alto
            );

            g2.fillOval(x - 3, y - 3, 6, 6);
        }

        // 5. Destacar la pareja más cercana
        if (solucion != null
                && solucion.getA() != null
                && solucion.getB() != null) {

            Punto a = solucion.getA();
            Punto b = solucion.getB();

            int ax = transformarX(
                    a.getX(), minX, escala, desplazamientoX
            );

            int ay = transformarY(
                    a.getY(), minY, escala,
                    desplazamientoY, alto
            );

            int bx = transformarX(
                    b.getX(), minX, escala, desplazamientoX
            );

            int by = transformarY(
                    b.getY(), minY, escala,
                    desplazamientoY, alto
            );

            // Línea azul entre los dos puntos
            g2.setColor(new Color(0, 110, 255));
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawLine(ax, ay, bx, by);

            // Puntos seleccionados en rojo
            g2.setColor(new Color(220, 40, 40));
            g2.fillOval(ax - 5, ay - 5, 10, 10);
            g2.fillOval(bx - 5, by - 5, 10, 10);

            // Restaurar grosor de línea
            g2.setStroke(new BasicStroke(1f));
        }
    }

    // Convertir coordenada X del dataset a píxeles
    private int transformarX(
            double x,
            double minX,
            double escala,
            double desplazamientoX) {

        return (int) Math.round(
                desplazamientoX + (x - minX) * escala
        );
    }

    // Convertir coordenada Y del dataset a píxeles
    private int transformarY(
            double y,
            double minY,
            double escala,
            double desplazamientoY,
            int alto) {

        return (int) Math.round(
                alto - desplazamientoY - (y - minY) * escala
        );
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }
}
