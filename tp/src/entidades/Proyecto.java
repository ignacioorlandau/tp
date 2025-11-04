package entidades;

import java.util.ArrayList;
import java.util.List;

public class Proyecto {
    private static int contadorProyectos = 1;
    private Integer numero;
    private String domicilio;
    private String[] cliente; // [nombre, email, telefono]
    private String fechaInicio;
    private String fechaFinEstimada;
    private String fechaFinReal;
    private String estado;
    private List<Tarea> tareas;
    private List<Empleado> historialEmpleados;

    public Proyecto(String domicilio, String[] cliente, String fechaInicio, String fechaFinEstimada) {
        this.numero = contadorProyectos++;
        this.domicilio = domicilio;
        this.cliente = cliente;
        this.fechaInicio = fechaInicio;
        this.fechaFinEstimada = fechaFinEstimada;
        this.fechaFinReal = fechaFinEstimada; // Inicialmente igual
        this.estado = Estado.pendiente;
        this.tareas = new ArrayList<>();
        this.historialEmpleados = new ArrayList<>();
    }

    // Getters
    public Integer getNumero() { return numero; }
    public String getDomicilio() { return domicilio; }
    public String getEstado() { return estado; }
    public List<Tarea> getTareas() { return tareas; }
    public String getFechaFinReal() { return fechaFinReal; }

    public void agregarTarea(Tarea tarea) {
        tareas.add(tarea);
        // Recalcular fechas al agregar tarea
        recalcularFechas();
    }

    public void asignarEmpleadoATarea(String tituloTarea, Empleado empleado) {
        for (Tarea tarea : tareas) {
            if (tarea.getTitulo().equals(tituloTarea) && !tarea.isFinalizada()) {
                if (tarea.getResponsable() != null) {
                    // Reasignación - el empleado anterior queda en el historial
                    historialEmpleados.add(tarea.getResponsable());
                }
                tarea.setResponsable(empleado);
                if (estado.equals(Estado.pendiente)) {
                    estado = Estado.activo;
                }
                break;
            }
        }
    }

    public void finalizarProyecto(String fechaFinReal) {
        this.fechaFinReal = fechaFinReal;
        this.estado = Estado.finalizado;
        // Liberar empleados pero mantener historial
        for (Tarea tarea : tareas) {
            if (!tarea.isFinalizada()) {
                tarea.finalizar();
            }
        }
    }

    private void recalcularFechas() {
        // Lógica simplificada para recalcular fechas
        // En una implementación real, aquí iría la lógica de fechas
    }

    public boolean todasTareasAsignadas() {
        for (Tarea tarea : tareas) {
            if (!tarea.isFinalizada() && tarea.getResponsable() == null) {
                return false;
            }
        }
        return true;
    }

    public boolean todasTareasFinalizadas() {
        for (Tarea tarea : tareas) {
            if (!tarea.isFinalizada()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Proyecto #").append(numero)
          .append("\nDomicilio: ").append(domicilio)
          .append("\nCliente: ").append(cliente[0])
          .append("\nEstado: ").append(estado)
          .append("\nFecha Inicio: ").append(fechaInicio)
          .append("\nFecha Fin Real: ").append(fechaFinReal)
          .append("\nTareas: ").append(tareas.size())
          .append("\nCosto Total: $").append(calcularCostoTotal());
        return sb.toString();
    }

    public double calcularCostoTotal() {
        double costo = 0;
        for (Tarea tarea : tareas) {
            if (tarea.getResponsable() != null) {
                costo += tarea.getResponsable().calcularCosto(tarea.getHorasTrabajo());
            }
        }
        
        // Aplicar recargos/descuentos según retrasos
        boolean hayRetrasos = false;
        for (Tarea tarea : tareas) {
            if (tarea.getDiasReales() > tarea.getDiasEstimados()) {
                hayRetrasos = true;
                break;
            }
        }

        if (hayRetrasos) {
            costo *= 1.25; // 25% de recargo por retrasos
        } else {
            costo *= 1.35; // 35% de recargo normal
        }

        return costo;
    }
}