package datos;

import modelo.Punto;

import java.util.Random;

public class GeneradorDataset {


    public static Dataset generarVertical(String nombre, int tamano) {

        if(tamano<2) throw new IllegalArgumentException("El tamaño debe ser igual o mayor que 2");

        Punto[] puntos=new Punto[tamano];
        Random random= new Random();

        for (int i = 0; i < tamano; i++) {

            double x = 5.0;
            double y = i * 0.73 + random.nextDouble() * 0.5;

            puntos[i] = new Punto(i, x, y);
        }

        return new Dataset(nombre, puntos);
    }

    public static Dataset generarAleatorios(String nombre, int tamano) {

        if(tamano<2) throw new IllegalArgumentException("El tamaño debe ser igual o mayor que 2");


        Punto[] puntos = new Punto[tamano];
        Random random = new Random();

        for (int i = 0; i < tamano; i++) {

            double x = -1000 + random.nextDouble() * 2000;
            double y = -1000 + random.nextDouble() * 2000;

            puntos[i] = new Punto(i, x, y);
        }

        return new Dataset(nombre, puntos);
    }

}
