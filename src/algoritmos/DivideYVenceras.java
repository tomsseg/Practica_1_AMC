package algoritmos;

import modelo.Punto;
import modelo.Solucion;
import utilidades.Distancias;


public class DivideYVenceras {
    public static Solucion resolver(Punto[] puntos){

        QuickSort.ordenar(puntos);

        return resolver(puntos,0, puntos.length-1);
    }


    private static Solucion resolver(Punto[] puntos, int inicio , int fin){
        Solucion sol = new Solucion(Double.POSITIVE_INFINITY);
         if (fin-inicio==1) {
            sol.mejorSol(puntos[inicio],puntos[fin], Distancias.euclidea(puntos[inicio],puntos[fin]));
             sol.incrementarDistanciasCalculadas();
            return sol;
         }else if (inicio == fin) {
             return sol;
         }
        int medio = inicio + (fin - inicio) / 2;
        Solucion sol1 = resolver(puntos,inicio, medio);
        Solucion sol2 = resolver(puntos,medio+1, fin);

        if(sol1.getDistancia()< sol2.getDistancia()){
             sol1.incrementarDistanciasCalculadas(sol2.getDistanciasCalculadas());
             sol=sol1;
        }else{
            sol2.incrementarDistanciasCalculadas(sol1.getDistanciasCalculadas());
            sol=sol2;
        }

        for (int i = medio; i >= inicio ; i--) {
            if(puntos[medio].getX()-puntos[i].getX()>=sol.getDistancia()){
                break;
            }
            for (int j = medio+1; j <= fin; j++) {
                if(puntos[j].getX()-puntos[i].getX()>=sol.getDistancia()){
                    break;
                }
                sol.mejorSol(puntos[i],puntos[j],Distancias.euclidea(puntos[i],puntos[j]));
                sol.incrementarDistanciasCalculadas();
            }
        }

        return sol;
    }


}
