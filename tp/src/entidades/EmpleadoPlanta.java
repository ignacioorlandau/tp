package entidades;

public class EmpleadoPlanta extends Empleado {
    private double valorDia;
    private String categoria;

    public EmpleadoPlanta(String nombre, double valorDia, String categoria) {
        super(nombre);
        this.valorDia = valorDia;
        this.categoria = categoria;
    }

    @Override
    public double calcularCosto(double horas) {
        // Empleados de planta cobran por día completo aunque trabajen medio día
        double diasTrabajados = Math.ceil(horas / 8.0); // Siempre redondea hacia arriba
        return diasTrabajados * valorDia;
    }

    @Override
    public String getTipo() {
        return "Planta - " + categoria;
    }

    public double getValorDia() {
        return valorDia;
    }

    public String getCategoria() {
        return categoria;
    }
}