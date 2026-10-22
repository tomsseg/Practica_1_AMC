package modelo;

public class Punto {


    final private int id;
    final private double x, y;

    public Punto(int id, double x, double y){
        this.id=id; this.x=x; this.y=y;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Punto " + id +" {" +
                "x=" + x +
                " y=" + y +
                '}';
    }


}
