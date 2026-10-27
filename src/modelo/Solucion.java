package modelo;

public class Solucion {
    private Punto A, B;
    private double distancia;
    private int distanciasCalculadas;

    public Solucion(double distancia) {
        this.distancia = distancia;
        distanciasCalculadas=0;
    }

    public Solucion(Punto a, Punto b, double distancia) {
        A = a;
        B = b;
        this.distancia = distancia;
        distanciasCalculadas=0;
    }

    public Punto getB() {
        return B;
    }

    public Punto getA() {
        return A;
    }

    public double getDistancia() {
        return distancia;
    }

    public int getDistanciasCalculadas() {
        return distanciasCalculadas;
    }

    public void mejorSol(Punto a, Punto b, double distancia){
        if(this.distancia>distancia) {
            this.A = a;
            this.B = b;
            this.distancia = distancia;
        }
    }

    public void incrementarDistanciasCalculadas(){ distanciasCalculadas++; }

    public void incrementarDistanciasCalculadas(int incremento){ distanciasCalculadas+=incremento; }

    @Override
    public String toString() {
        return "Solucion{ \n" +
                " " + A +
                "\n " + B +
                "\n distancia = " + distancia +
                "\n distancias calculadas = " + distanciasCalculadas +
                "\n}";
    }
}
