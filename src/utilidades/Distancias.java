package utilidades;

import modelo.Punto;

public class Distancias {
     public static double euclidea(Punto A, Punto B) {
         return Math.sqrt(Math.pow(A.getX()-B.getX(),2)+Math.pow(A.getY()-B.getY(),2));
     }
}
