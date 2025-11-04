package entidades;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HomeSolution implements IHomeSolution {
    private List<Empleado> empleados;
    private List<Proyecto> proyectos;
    private int contadorEmpleados;
    private int contadorProyectos;

    public HomeSolution() {
        this.empleados = new ArrayList<>();
        this.proyectos = new ArrayList<>();
        this.contadorEmpleados = 1;
        this.contadorProyectos = 1;
    }

    // ============================================================
    // REGISTRO DE EMPLEADOS (SOBRECARGA - Requerimiento del TP)
    // ============================================================

    @Override
    public void registrarEmpleado(String nombre, double valor) throws IllegalArgumentException {
        if (nombre == null || nombre.trim().isEmpty() || valor < 0) {
            throw new IllegalArgumentException("Nombre vacío o valor negativo");
        }
        EmpleadoContratado nuevo = new EmpleadoContratado(nombre, valor);
        empleados.add(nuevo);
    }

    @Override
    public void registrarEmpleado(String nombre, double valor, String categoria) throws IllegalArgumentException {
        if (nombre == null || nombre.trim().isEmpty() || valor < 0 || categoria == null) {
            throw new IllegalArgumentException("Datos inválidos");
        }
        EmpleadoPlanta nuevo = new EmpleadoPlanta(nombre, valor, categoria);
        empleados.add(nuevo);
    }

    // ============================================================
    // REGISTRO DE PROYECTOS
    // ============================================================

    @Override
    public void registrarProyecto(String[] titulos, String[] descripcion, double[] dias,
                                 String domicilio, String[] cliente, String inicio, String fin)
            throws IllegalArgumentException {
        if (titulos == null || descripcion == null || dias == null || 
            domicilio == null || cliente == null || inicio == null || fin == null) {
            throw new IllegalArgumentException("Datos del proyecto incompletos");
        }

        Proyecto proyecto = new Proyecto(domicilio, cliente, inicio, fin);
        
        // Agregar tareas al proyecto (USANDO ITERADOR - Requerimiento del TP)
        for (int i = 0; i < titulos.length; i++) {
            Tarea tarea = new Tarea(titulos[i], descripcion[i], dias[i]);
            proyecto.agregarTarea(tarea);
        }
        
        proyectos.add(proyecto);
    }

    // ============================================================
    // ASIGNACIÓN DE EMPLEADOS A TAREAS
    // ============================================================

    @Override
    public void asignarResponsableEnTarea(Integer numero, String titulo) throws Exception {
        Proyecto proyecto = buscarProyecto(numero);
        if (proyecto == null) throw new Exception("Proyecto no encontrado");
        
        if (proyecto.getEstado().equals(Estado.finalizado)) {
            throw new Exception("No se puede asignar a proyecto finalizado");
        }

        Empleado empleadoDisponible = encontrarEmpleadoDisponible();
        if (empleadoDisponible == null) {
            // Proyecto queda pendiente (según requerimiento)
            throw new Exception("No hay empleados disponibles");
        }

        proyecto.asignarEmpleadoATarea(titulo, empleadoDisponible);
    }

    @Override
    public void asignarResponsableMenosRetraso(Integer numero, String titulo) throws Exception {
        Proyecto proyecto = buscarProyecto(numero);
        if (proyecto == null) throw new Exception("Proyecto no encontrado");
        
        if (proyecto.getEstado().equals(Estado.finalizado)) {
            throw new Exception("No se puede asignar a proyecto finalizado");
        }

        Empleado empleadoEficiente = encontrarEmpleadoMenosRetrasos();
        if (empleadoEficiente == null) {
            throw new Exception("No hay empleados disponibles");
        }

        proyecto.asignarEmpleadoATarea(titulo, empleadoEficiente);
    }

    // ============================================================
    // GESTIÓN DE TAREAS
    // ============================================================

    @Override
    public void registrarRetrasoEnTarea(Integer numero, String titulo, double cantidadDias) throws IllegalArgumentException {
        if (cantidadDias < 0) throw new IllegalArgumentException("Días de retraso no pueden ser negativos");
        
        Proyecto proyecto = buscarProyecto(numero);
        if (proyecto != null) {
            for (Tarea tarea : proyecto.getTareas()) {
                if (tarea.getTitulo().equals(titulo)) {
                    tarea.registrarRetraso(cantidadDias);
                    break;
                }
            }
        }
    }

    @Override
    public void agregarTareaEnProyecto(Integer numero, String titulo, String descripcion, double dias) throws IllegalArgumentException {
        if (titulo == null || dias < 0) throw new IllegalArgumentException("Datos inválidos");
        
        Proyecto proyecto = buscarProyecto(numero);
        if (proyecto != null && !proyecto.getEstado().equals(Estado.finalizado)) {
            Tarea nuevaTarea = new Tarea(titulo, descripcion, dias);
            proyecto.agregarTarea(nuevaTarea);
        } else {
            throw new IllegalArgumentException("Proyecto no encontrado o finalizado");
        }
    }

    @Override
    public void finalizarTarea(Integer numero, String titulo) throws Exception {
        Proyecto proyecto = buscarProyecto(numero);
        if (proyecto != null) {
            for (Tarea tarea : proyecto.getTareas()) {
                if (tarea.getTitulo().equals(titulo)) {
                    if (tarea.isFinalizada()) {
                        throw new Exception("La tarea ya estaba finalizada");
                    }
                    tarea.finalizar();
                    return;
                }
            }
            throw new Exception("Tarea no encontrada");
        }
        throw new Exception("Proyecto no encontrado");
    }

    @Override
    public void finalizarProyecto(Integer numero, String fin) throws IllegalArgumentException {
        Proyecto proyecto = buscarProyecto(numero);
        if (proyecto != null) {
            proyecto.finalizarProyecto(fin);
        } else {
            throw new IllegalArgumentException("Proyecto no encontrado");
        }
    }

    // ============================================================
    // REASIGNACIÓN DE EMPLEADOS
    // ============================================================

    @Override
    public void reasignarEmpleadoEnProyecto(Integer numero, Integer legajo, String titulo) throws Exception {
        Proyecto proyecto = buscarProyecto(numero);
        Empleado empleado = buscarEmpleado(legajo);
        
        if (proyecto == null || empleado == null) {
            throw new Exception("Proyecto o empleado no encontrado");
        }

        proyecto.asignarEmpleadoATarea(titulo, empleado);
    }

    @Override
    public void reasignarEmpleadoConMenosRetraso(Integer numero, String titulo) throws Exception {
        Proyecto proyecto = buscarProyecto(numero);
        if (proyecto == null) throw new Exception("Proyecto no encontrado");

        Empleado empleadoEficiente = encontrarEmpleadoMenosRetrasos();
        if (empleadoEficiente == null) {
            throw new Exception("No hay empleados disponibles");
        }

        proyecto.asignarEmpleadoATarea(titulo, empleadoEficiente);
    }

    // ============================================================
    // CONSULTAS Y REPORTES
    // ============================================================

    @Override
    public double costoProyecto(Integer numero) {
        Proyecto proyecto = buscarProyecto(numero);
        return proyecto != null ? proyecto.calcularCostoTotal() : 0.0;
    }

    @Override
    public List<Tupla<Integer, String>> proyectosFinalizados() {
        List<Tupla<Integer, String>> resultado = new ArrayList<>();
        // USANDO FOREACH - Requerimiento del TP
        for (Proyecto proyecto : proyectos) {
            if (proyecto.getEstado().equals(Estado.finalizado)) {
                resultado.add(new Tupla<>(proyecto.getNumero(), proyecto.getDomicilio()));
            }
        }
        return resultado;
    }

    @Override
    public List<Tupla<Integer, String>> proyectosPendientes() {
        List<Tupla<Integer, String>> resultado = new ArrayList<>();
        // USANDO ITERATOR - Requerimiento del TP
        Iterator<Proyecto> iterator = proyectos.iterator();
        while (iterator.hasNext()) {
            Proyecto proyecto = iterator.next();
            if (proyecto.getEstado().equals(Estado.pendiente)) {
                resultado.add(new Tupla<>(proyecto.getNumero(), proyecto.getDomicilio()));
            }
        }
        return resultado;
    }

    @Override
    public List<Tupla<Integer, String>> proyectosActivos() {
        List<Tupla<Integer, String>> resultado = new ArrayList<>();
        for (Proyecto proyecto : proyectos) {
            if (proyecto.getEstado().equals(Estado.activo)) {
                resultado.add(new Tupla<>(proyecto.getNumero(), proyecto.getDomicilio()));
            }
        }
        return resultado;
    }

    @Override
    public Object[] empleadosNoAsignados() {
        List<Empleado> noAsignados = new ArrayList<>();
        for (Empleado empleado : empleados) {
            if (!estaEmpleadoAsignado(empleado)) {
                noAsignados.add(empleado);
            }
        }
        return noAsignados.toArray();
    }

    @Override
    public boolean estaFinalizado(Integer numero) {
        Proyecto proyecto = buscarProyecto(numero);
        return proyecto != null && proyecto.getEstado().equals(Estado.finalizado);
    }

    @Override
    public int consultarCantidadRetrasosEmpleado(Integer legajo) {
        Empleado empleado = buscarEmpleado(legajo);
        return empleado != null ? empleado.getCantidadRetrasos() : 0;
    }

    @Override
    public List<Tupla<Integer, String>> empleadosAsignadosAProyecto(Integer numero) {
        List<Tupla<Integer, String>> resultado = new ArrayList<>();
        Proyecto proyecto = buscarProyecto(numero);
        if (proyecto != null) {
            // USANDO STRINGBUILDER - Requerimiento del TP
            StringBuilder sb = new StringBuilder();
            for (Tarea tarea : proyecto.getTareas()) {
                if (tarea.getResponsable() != null) {
                    Empleado emp = tarea.getResponsable();
                    resultado.add(new Tupla<>(emp.getLegajo(), emp.getNombre()));
                }
            }
        }
        return resultado;
    }

    // ============================================================
    // NUEVOS REQUERIMIENTOS
    // ============================================================

    @Override
    public Object[] tareasProyectoNoAsignadas(Integer numero) {
        List<String> noAsignadas = new ArrayList<>();
        Proyecto proyecto = buscarProyecto(numero);
        if (proyecto != null) {
            for (Tarea tarea : proyecto.getTareas()) {
                if (tarea.getResponsable() == null && !tarea.isFinalizada()) {
                    noAsignadas.add(tarea.getTitulo());
                }
            }
        }
        return noAsignadas.toArray();
    }

    @Override
    public Object[] tareasDeUnProyecto(Integer numero) {
        List<String> tareasProyecto = new ArrayList<>();
        Proyecto proyecto = buscarProyecto(numero);
        if (proyecto != null) {
            for (Tarea tarea : proyecto.getTareas()) {
                tareasProyecto.add(tarea.getTitulo());
            }
        }
        return tareasProyecto.toArray();
    }

    @Override
    public String consultarDomicilioProyecto(Integer numero) {
        Proyecto proyecto = buscarProyecto(numero);
        return proyecto != null ? proyecto.getDomicilio() : "";
    }

    @Override
    public boolean tieneRestrasos(String legajo) {
        try {
            Integer leg = Integer.parseInt(legajo);
            Empleado empleado = buscarEmpleado(leg);
            return empleado != null && empleado.getCantidadRetrasos() > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public List<Tupla<Integer, String>> empleados() {
        List<Tupla<Integer, String>> resultado = new ArrayList<>();
        // USANDO STRINGBUILDER para demostración
        StringBuilder sb = new StringBuilder();
        for (Empleado empleado : empleados) {
            resultado.add(new Tupla<>(empleado.getLegajo(), empleado.getNombre()));
            sb.append(empleado.getNombre()).append(", ");
        }
        return resultado;
    }

    @Override
    public String consultarProyecto(Integer numero) {
        Proyecto proyecto = buscarProyecto(numero);
        if (proyecto != null) {
            // USANDO STRINGBUILDER - Requerimiento del TP
            StringBuilder sb = new StringBuilder();
            sb.append("=== INFORMACIÓN DEL PROYECTO ===\n");
            sb.append(proyecto.toString());
            sb.append("\n\n=== TAREAS ===\n");
            
            for (Tarea tarea : proyecto.getTareas()) {
                sb.append("- ").append(tarea.getTitulo());
                if (tarea.isFinalizada()) {
                    sb.append(" (FINALIZADA)");
                }
                if (tarea.getResponsable() != null) {
                    sb.append(" - Asignada a: ").append(tarea.getResponsable().getNombre());
                }
                sb.append("\n");
            }
            return sb.toString();
        }
        return "Proyecto no encontrado";
    }

    // ============================================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ============================================================

    private Proyecto buscarProyecto(Integer numero) {
        for (Proyecto proyecto : proyectos) {
            if (proyecto.getNumero().equals(numero)) {
                return proyecto;
            }
        }
        return null;
    }

    private Empleado buscarEmpleado(Integer legajo) {
        for (Empleado empleado : empleados) {
            if (empleado.getLegajo().equals(legajo)) {
                return empleado;
            }
        }
        return null;
    }

    private Empleado encontrarEmpleadoDisponible() {
        for (Empleado empleado : empleados) {
            if (!estaEmpleadoAsignado(empleado)) {
                return empleado;
            }
        }
        return null;
    }

    private Empleado encontrarEmpleadoMenosRetrasos() {
        Empleado mejorEmpleado = null;
        int menorRetrasos = Integer.MAX_VALUE;
        
        for (Empleado empleado : empleados) {
            if (!estaEmpleadoAsignado(empleado) && empleado.getCantidadRetrasos() < menorRetrasos) {
                mejorEmpleado = empleado;
                menorRetrasos = empleado.getCantidadRetrasos();
            }
        }
        return mejorEmpleado;
    }

    private boolean estaEmpleadoAsignado(Empleado empleado) {
        for (Proyecto proyecto : proyectos) {
            for (Tarea tarea : proyecto.getTareas()) {
                if (tarea.getResponsable() == empleado && !tarea.isFinalizada()) {
                    return true;
                }
            }
        }
        return false;
    }
}