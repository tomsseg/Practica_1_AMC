
package algoritmos;

import modelo.Punto;
import modelo.Solucion;
import utilidades.Distancias;

import java.util.ArrayList;
import java.util.IdentityHashMap;

public class DivideYVencerasMejorado {

    public static Solucion resolver(Punto[] puntos) {

        QuickSort.ordenar(puntos);

        return resolver(puntos, 0, puntos.length - 1);
    }

    private static Solucion resolver(Punto[] puntos, int inicio, int fin) {

        Solucion sol = new Solucion(Double.POSITIVE_INFINITY);

        if (fin - inicio == 1) {
            sol.mejorSol(
                    puntos[inicio],
                    puntos[fin],
                    Distancias.euclidea(puntos[inicio], puntos[fin])
            );
            sol.incrementarDistanciasCalculadas();
            return sol;

        } else if (inicio == fin) {
            return sol;
        }

        int medio = inicio + (fin - inicio) / 2;

        Solucion sol1 = resolver(puntos, inicio, medio);
        Solucion sol2 = resolver(puntos, medio + 1, fin);

        if (sol1.getDistancia() < sol2.getDistancia()) {
            sol1.incrementarDistanciasCalculadas(
                    sol2.getDistanciasCalculadas()
            );
            sol = sol1;
        } else {
            sol2.incrementarDistanciasCalculadas(
                    sol1.getDistanciasCalculadas()
            );
            sol = sol2;
        }

        // Construcción de la franja central
        ArrayList<Punto> puntosYArray = new ArrayList<>();

        // Registrar la mitad original de cada punto
        // false = izquierda, true = derecha
        IdentityHashMap<Punto, Boolean> mitades = new IdentityHashMap<>();

        for (int i = inicio; i <= fin; i++) {

            if (Math.abs(puntos[medio].getX() - puntos[i].getX()) < sol.getDistancia()) {

                puntosYArray.add(puntos[i]);

                mitades.put(puntos[i], i > medio);
            }
        }

        Punto[] puntosY = puntosYArray.toArray(new Punto[0]);


        QuickSort.ordenar(puntosY, true);


        for (int i = 0; i < puntosY.length - 1; i++) {

            int j = i + 1;

            while (j <= i + 11 &&
                    j < puntosY.length &&
                    puntosY[j].getY() - puntosY[i].getY() < sol.getDistancia()) {


                if (mitades.get(puntosY[i]) != mitades.get(puntosY[j])) {

                    sol.mejorSol(
                            puntosY[i],
                            puntosY[j],
                            Distancias.euclidea(puntosY[i], puntosY[j])
                    );

                    sol.incrementarDistanciasCalculadas();
                }

                j++;
            }
        }

        return sol;
    }
}
