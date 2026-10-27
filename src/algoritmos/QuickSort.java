package algoritmos;

import modelo.Punto;

public class QuickSort {

    static void ordenar(Punto[] puntos){
        ordenar(puntos,0,puntos.length-1);
    }

    static void ordenar(Punto[] puntos, boolean Y){
        ordenarY(puntos,0,puntos.length-1);
    }

    private static void ordenar(Punto[] puntos, int inicio, int fin){

        if(inicio>=fin) return;


        double pivote = puntos[inicio+(fin-inicio)/2].getX();

        int i=inicio,j=fin;

        while(i<=j) {
            while (puntos[i].getX() < pivote) {
                i++;
            }

            while (puntos[j].getX() > pivote) {
                j--;
            }

            if (i <= j) {
                intercambiar(puntos, i, j);
                i++;
                j--;
            }
        }

        if (inicio < j) {
            ordenar(puntos, inicio, j);
        }

        if (i < fin) {
            ordenar(puntos, i, fin);
        }


    }

    private static void ordenarY(Punto[] puntos, int inicio, int fin){

        if(inicio>=fin) return;


        double pivote = puntos[inicio+(fin-inicio)/2].getY();

        int i=inicio,j=fin;

        while(i<=j) {
            while (puntos[i].getY() < pivote) {
                i++;
            }

            while (puntos[j].getY() > pivote) {
                j--;
            }

            if (i <= j) {
                intercambiar(puntos, i, j);
                i++;
                j--;
            }
        }

        if (inicio < j) {
            ordenarY(puntos, inicio, j);
        }

        if (i < fin) {
            ordenarY(puntos, i, fin);
        }


    }

    private static void intercambiar (Punto[] puntos, int a, int b){
        Punto c = puntos[a];
        puntos[a]=puntos[b];
        puntos[b]=c;
    }
}
