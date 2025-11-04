package entidades;

public class Tarea {
    private String titulo;
    private String descripcion;
    private double diasEstimados;
    private double diasReales;
    private boolean finalizada;
    private Empleado responsable;

    public Tarea(String titulo, String descripcion, double diasEstimados) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.diasEstimados = diasEstimados;
        this.diasReales = diasEstimados; // Inicialmente son iguales
        this.finalizada = false;
        this.responsable = null;
    }

    // Getters y setters
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public double getDiasEstimados() { return diasEstimados; }
    public double getDiasReales() { return diasReales; }
    public boolean isFinalizada() { return finalizada; }
    public Empleado getResponsable() { return responsable; }

    public void setResponsable(Empleado responsable) {
        this.responsable = responsable;
    }

    public void finalizar() {
        this.finalizada = true;
        if (this.responsable != null) {
            // El empleado queda liberado pero mantiene el historial
        }
    }

    public void registrarRetraso(double diasRetraso) {
        this.diasReales += diasRetraso;
        if (this.responsable != null) {
            this.responsable.incrementarRetrasos();
        }
    }

    public double getHorasTrabajo() {
        return diasReales * 8; // 8 horas por día
    }

    @Override
    public String toString() {
        return titulo; // Según el TP, toString solo debe devolver el título
    }
}
