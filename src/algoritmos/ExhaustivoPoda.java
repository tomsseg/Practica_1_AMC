package algoritmos;

import modelo.Punto;
import modelo.Solucion;
import utilidades.Distancias;

public class ExhaustivoPoda {
    public static Solucion resolver(Punto[] puntos){
        QuickSort.ordenar(puntos);
        Solucion sol= new Solucion(Double.POSITIVE_INFINITY);

        for (int i = 0; i < puntos.length; i++) {
            int j=i+1;
            while(j< puntos.length&&(puntos[j].getX()-puntos[i].getX())<sol.getDistancia()){
                sol.mejorSol(puntos[i],puntos[j], Distancias.euclidea(puntos[i],puntos[j]));
                sol.incrementarDistanciasCalculadas();
                j++;
            }
        }


        return sol;
    }
}
