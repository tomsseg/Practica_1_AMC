import algoritmos.Exhaustivo;
import algoritmos.ExhaustivoPoda;
import modelo.Punto;
import modelo.Solucion;

public class Main {
    public static void main(String [] args){
        Punto[] puntos={new Punto(0,12,46),
                        new Punto(1,31,30),
                        new Punto(2,25,15),
                        new Punto(3,45,23)};

        System.out.println("COMPARACION DE ALGORITMOS");
        System.out.println("______________________________");

        for (int i = 0; i < puntos.length; i++) {
            System.out.println(puntos[i]);
        }


        Solucion s = Exhaustivo.resolver(puntos);

        System.out.println("______________________________");
        System.out.println("EXHAUSTIVO");
        System.out.println();
        for (int i = 0; i < puntos.length; i++) {
            System.out.println(puntos[i]);
        }
        System.out.println();
        System.out.println(s);

        puntos= new Punto[]{new Punto(0, 12, 46),
                new Punto(1, 31, 30),
                new Punto(2, 25, 15),
                new Punto(3, 45, 23)};


        System.out.println("______________________________");
        System.out.println("EXHAUSTIVO CON PODA");
        System.out.println();
        s = ExhaustivoPoda.resolver(puntos);

        for (int i = 0; i < puntos.length; i++) {
            System.out.println(puntos[i]);
        }
        System.out.println();
        System.out.println(s);

    }

}
