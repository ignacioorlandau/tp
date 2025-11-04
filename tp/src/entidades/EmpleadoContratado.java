package entidades;

public class EmpleadoContratado extends Empleado {
    private double valorHora;

    public EmpleadoContratado(String nombre, double valorHora) {
        super(nombre);
        this.valorHora = valorHora;
    }

    @Override
    public double calcularCosto(double horas) {
        return horas * valorHora;
    }

    @Override
    public String getTipo() {
        return "Contratado";
    }

    public double getValorHora() {
        return valorHora;
    }
}
