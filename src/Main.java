
import algoritmos.Exhaustivo;
import algoritmos.ExhaustivoPoda;
import algoritmos.DivideYVenceras;
import modelo.Punto;
import modelo.Solucion;

public class Main {

    public static void main(String[] args) {

        // Dataset original
        Punto[] puntos = {
                new Punto(0, 12, 46),
                new Punto(1, 31, 30),
                new Punto(2, 25, 15),
                new Punto(3, 45, 23),
                new Punto(4, 4, 4)
        };

        System.out.println("COMPARACION DE ALGORITMOS");
        System.out.println("______________________________");
        System.out.println("DATASET ORIGINAL:");

        mostrarPuntos(puntos);

        // Cada algoritmo recibe su propia copia
        Punto[] copiaExhaustivo = puntos.clone();
        Punto[] copiaPoda = puntos.clone();
        Punto[] copiaDyV = puntos.clone();

        // EXHAUSTIVO
        long inicio = System.nanoTime();
        Solucion solExhaustivo = Exhaustivo.resolver(copiaExhaustivo);
        long fin = System.nanoTime();

        mostrarResultado("EXHAUSTIVO", solExhaustivo,
                (fin - inicio) / 1_000_000.0);

        // EXHAUSTIVO CON PODA
        inicio = System.nanoTime();
        Solucion solPoda = ExhaustivoPoda.resolver(copiaPoda);
        fin = System.nanoTime();

        mostrarResultado("EXHAUSTIVO CON PODA", solPoda,
                (fin - inicio) / 1_000_000.0);

        // DIVIDE Y VENCERAS
        inicio = System.nanoTime();
        Solucion solDyV = DivideYVenceras.resolver(copiaDyV);
        fin = System.nanoTime();

        mostrarResultado("DIVIDE Y VENCERAS", solDyV,
                (fin - inicio) / 1_000_000.0);

        // Verificar que los tres algoritmos coinciden
        boolean coinciden =
                Double.compare(solExhaustivo.getDistancia(),
                        solPoda.getDistancia()) == 0
                        &&
                        Double.compare(solExhaustivo.getDistancia(),
                                solDyV.getDistancia()) == 0;

        System.out.println("\n______________________________");
        System.out.println("COMPROBACION FINAL");

        if (coinciden) {
            System.out.println("Los tres algoritmos coinciden.");
        } else {
            System.out.println("ERROR: Los algoritmos dan resultados distintos.");
        }
    }

    private static void mostrarPuntos(Punto[] puntos) {
        for (Punto punto : puntos) {
            System.out.println(punto);
        }
    }

    private static void mostrarResultado(
            String nombre, Solucion solucion, double tiempoMs) {

        System.out.println("\n______________________________");
        System.out.println(nombre);

        System.out.println("Punto A: " + solucion.getA());
        System.out.println("Punto B: " + solucion.getB());

        System.out.printf("Distancia minima: %.8f%n",
                solucion.getDistancia());

        System.out.println("Distancias calculadas: "
                + solucion.getDistanciasCalculadas());

        System.out.printf("Tiempo de ejecucion: %.4f ms%n",
                tiempoMs);
    }
}
