package datos;

import modelo.Punto;

import java.util.Random;

public class Dataset {
    private String nombre;
    private Punto[] puntos;

    public Dataset(String nombre, Punto[] puntos){
        if (puntos == null||puntos.length<=1) {
            throw new IllegalArgumentException(
                    "El array de puntos debe tener al menos 2 puntos"
            );
        }

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del dataset no puede estar vacío"
            );
        }

        for (int i = 0; i < puntos.length; i++) {
            if (puntos[i] == null) {
                throw new IllegalArgumentException(
                        "No puede haber ningun punto a null"
                );
            }
        }
        this.nombre = nombre;
        this.puntos = puntos.clone();
    }

    public Dataset(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del dataset no puede estar vacío"
            );
        }

        this.nombre = nombre;
        this.puntos = null;
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
