package com.ycr.mshabitaciones.Service;


import com.ycr.mshabitaciones.Entity.Habitacion;
import com.ycr.mshabitaciones.Repository.HabitacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HabitacionService {

    @Autowired
    private HabitacionRepository habitacionRepository;

    // Crear habitación
    public Habitacion crearHabitacion(Habitacion habitacion) {
        if (habitacionRepository.existsByNumero(habitacion.getNumero())) {
            throw new RuntimeException("Ya existe una habitación con el número: " + habitacion.getNumero());
        }
        // Valores por defecto si no se proporcionan
        if (habitacion.getDisponible() == null) {
            habitacion.setDisponible(true);
        }
        if (habitacion.getEstado() == null) {
            habitacion.setEstado("ACTIVA");
        }
        return habitacionRepository.save(habitacion);
    }

    // Obtener todas las habitaciones
    public List<Habitacion> obtenerTodasLasHabitaciones() {
        return habitacionRepository.findAll();
    }

    // Obtener habitación por ID
    public Optional<Habitacion> obtenerHabitacionPorId(Long id) {
        return habitacionRepository.findById(id);
    }

    // Obtener habitación por número
    public Optional<Habitacion> obtenerHabitacionPorNumero(String numero) {
        return habitacionRepository.findByNumero(numero);
    }

    // Obtener habitaciones disponibles
    public List<Habitacion> obtenerHabitacionesDisponibles() {
        return habitacionRepository.findByDisponible(true);
    }

    // Obtener habitaciones por tipo
    public List<Habitacion> obtenerHabitacionesPorTipo(String tipo) {
        return habitacionRepository.findByTipo(tipo);
    }

    // Obtener habitaciones por estado
    public List<Habitacion> obtenerHabitacionesPorEstado(String estado) {
        return habitacionRepository.findByEstado(estado);
    }

    // Obtener habitaciones por tipo y disponibilidad
    public List<Habitacion> obtenerHabitacionesPorTipoYDisponibilidad(String tipo, Boolean disponible) {
        return habitacionRepository.findByTipoAndDisponible(tipo, disponible);
    }



    // Obtener habitaciones por capacidad mínima
    public List<Habitacion> obtenerHabitacionesPorCapacidad(Integer capacidad) {
        return habitacionRepository.findByCapacidadGreaterThanEqual(capacidad);
    }

    // Actualizar habitación
    public Habitacion actualizarHabitacion(Long id, Habitacion habitacionActualizada) {
        return habitacionRepository.findById(id)
                .map(habitacion -> {
                    // Verificar si el número cambió y si ya existe
                    if (!habitacion.getNumero().equals(habitacionActualizada.getNumero()) && 
                        habitacionRepository.existsByNumero(habitacionActualizada.getNumero())) {
                        throw new RuntimeException("El número de habitación ya está registrado");
                    }
                    
                    habitacion.setNumero(habitacionActualizada.getNumero());
                    habitacion.setTipo(habitacionActualizada.getTipo());
                    habitacion.setPrecioPorNoche(habitacionActualizada.getPrecioPorNoche());
                    habitacion.setDisponible(habitacionActualizada.getDisponible());
                    habitacion.setDescripcion(habitacionActualizada.getDescripcion());
                    habitacion.setCapacidad(habitacionActualizada.getCapacidad());
                    habitacion.setEstado(habitacionActualizada.getEstado());
                    return habitacionRepository.save(habitacion);
                })
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada con id: " + id));
    }

    // Cambiar disponibilidad
    public Habitacion cambiarDisponibilidad(Long id, Boolean disponible) {
        return habitacionRepository.findById(id)
                .map(habitacion -> {
                    habitacion.setDisponible(disponible);
                    return habitacionRepository.save(habitacion);
                })
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada con id: " + id));
    }

    // Cambiar estado
    public Habitacion cambiarEstado(Long id, String estado) {
        return habitacionRepository.findById(id)
                .map(habitacion -> {
                    habitacion.setEstado(estado);
                    // Si está en mantenimiento, marcar como no disponible
                    if ("MANTENIMIENTO".equals(estado) || "INACTIVA".equals(estado)) {
                        habitacion.setDisponible(false);
                    }
                    return habitacionRepository.save(habitacion);
                })
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada con id: " + id));
    }

    // Eliminar habitación
    public void eliminarHabitacion(Long id) {
        if (!habitacionRepository.existsById(id)) {
            throw new RuntimeException("Habitación no encontrada con id: " + id);
        }
        habitacionRepository.deleteById(id);
    }
}