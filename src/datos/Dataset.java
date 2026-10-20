package datos;

import modelo.Punto;

public class Dataset {
    private String nombre;
    private Punto[] puntos;

    public Dataset(String nombre, Punto[] puntos) {
        this.nombre = nombre;
        this.puntos = puntos;
    }

    public String getNombre() {
        return nombre;
    }

    public Punto[] getPuntos() {
        return this.copiar();
    }

    public int getDimension(){
        return puntos.length;
    }

    public Punto[] copiar(){
        return puntos.clone();
    }

}
