package algoritmos;

import modelo.Punto;
import modelo.Solucion;
import utilidades.Distancias;

public class Exhaustivo {

    public static Solucion resolver(Punto[] puntos){
        Solucion sol= new Solucion(Double.POSITIVE_INFINITY);
        for (int i = 0; i < puntos.length; i++) {
            for (int j = i+1; j < puntos.length; j++) {
                sol.mejorSol(puntos[i],puntos[j],Distancias.euclidea(puntos[i],puntos[j]));
            }
        }



        return sol;
    }

}
