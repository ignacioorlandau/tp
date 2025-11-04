package entidades;

public abstract class Empleado {
    private static int contadorLegajos = 1;
    protected Integer legajo;
    protected String nombre;
    protected int cantidadRetrasos;

    public Empleado(String nombre) {
        this.legajo = contadorLegajos++;
        this.nombre = nombre;
        this.cantidadRetrasos = 0;
    }

    // Métodos abstractos que las subclases deben implementar
    public abstract double calcularCosto(double horas);
    public abstract String getTipo();

    // Getters
    public Integer getLegajo() { return legajo; }
    public String getNombre() { return nombre; }
    public int getCantidadRetrasos() { return cantidadRetrasos; }

    public void incrementarRetrasos() {
        this.cantidadRetrasos++;
    }

    @Override
    public String toString() {
        return legajo + " - " + nombre + " (" + getTipo() + ")";
    }
}