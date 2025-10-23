package com.ycr.msreserva.Service;

import com.ycr.msreserva.Entity.Reserva;
import com.ycr.msreserva.Repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;



    // Crear reserva
    public Reserva crearReserva(Reserva reserva) {
        // Validar fechas
        if (reserva.getFechaInicio().isAfter(reserva.getFechaFin())) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        if (reserva.getFechaInicio().isBefore(LocalDate.now())) {
            throw new RuntimeException("La fecha de inicio no puede ser anterior a hoy");
        }

        // Verificar que la habitación existe
        Habitacion habitacion = habitacionRepository.findById(reserva.getIdHabitacion())
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        // Verificar disponibilidad (no hay reservas conflictivas)
        List<Reserva> reservasConflictivas = reservaRepository.findReservasConflictivas(
                reserva.getIdHabitacion(),
                reserva.getFechaInicio(),
                reserva.getFechaFin()
        );

        if (!reservasConflictivas.isEmpty()) {
            throw new RuntimeException("La habitación no está disponible en las fechas seleccionadas");
        }

        // Calcular monto total
        long dias = ChronoUnit.DAYS.between(reserva.getFechaInicio(), reserva.getFechaFin());
        if (dias == 0) dias = 1; // Mínimo 1 día
        reserva.setMontoTotal(habitacion.getPrecioPorNoche() * dias);

        // Establecer valores por defecto
        if (reserva.getEstado() == null) {
            reserva.setEstado("PENDIENTE");
        }
        reserva.setFechaCreacion(LocalDateTime.now());

        return reservaRepository.save(reserva);
    }

    // Obtener todas las reservas
    public List<Reserva> obtenerTodasLasReservas() {
        return reservaRepository.findAll();
    }

    // Obtener reserva por ID
    public Optional<Reserva> obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id);
    }

    // Obtener reservas por cliente
    public List<Reserva> obtenerReservasPorCliente(Long idCliente) {
        return reservaRepository.findByIdCliente(idCliente);
    }

    // Obtener reservas por habitación
    public List<Reserva> obtenerReservasPorHabitacion(Long idHabitacion) {
        return reservaRepository.findByIdHabitacion(idHabitacion);
    }

    // Obtener reservas por estado
    public List<Reserva> obtenerReservasPorEstado(String estado) {
        return reservaRepository.findByEstado(estado);
    }

    // Obtener reservas activas de una habitación
    public List<Reserva> obtenerReservasActivas(Long idHabitacion) {
        return reservaRepository.findReservasActivasPorHabitacion(idHabitacion, LocalDate.now());
    }

    // Obtener reservas por rango de fechas
    public List<Reserva> obtenerReservasPorRangoFechas(LocalDate inicio, LocalDate fin) {
        return reservaRepository.findByFechaInicioBetween(inicio, fin);
    }

    // Obtener reservas para check-out hoy
    public List<Reserva> obtenerReservasCheckOutHoy() {
        return reservaRepository.findReservasPorCheckOut(LocalDate.now());
    }

    // Obtener reservas para check-in hoy
    public List<Reserva> obtenerReservasCheckInHoy() {
        return reservaRepository.findReservasPorCheckIn(LocalDate.now());
    }

    // Verificar disponibilidad de habitación
    public boolean verificarDisponibilidad(Long idHabitacion, LocalDate fechaInicio, LocalDate fechaFin) {
        List<Reserva> reservasConflictivas = reservaRepository.findReservasConflictivas(
                idHabitacion, fechaInicio, fechaFin
        );
        return reservasConflictivas.isEmpty();
    }

    // Actualizar reserva
    public Reserva actualizarReserva(Long id, Reserva reservaActualizada) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    // No permitir actualizar si está cancelada
                    if ("CANCELADA".equals(reserva.getEstado())) {
                        throw new RuntimeException("No se puede actualizar una reserva cancelada");
                    }

                    // Si se cambian las fechas o habitación, verificar disponibilidad
                    if (!reserva.getIdHabitacion().equals(reservaActualizada.getIdHabitacion()) ||
                        !reserva.getFechaInicio().equals(reservaActualizada.getFechaInicio()) ||
                        !reserva.getFechaFin().equals(reservaActualizada.getFechaFin())) {
                        
                        List<Reserva> reservasConflictivas = reservaRepository.findReservasConflictivas(
                                reservaActualizada.getIdHabitacion(),
                                reservaActualizada.getFechaInicio(),
                                reservaActualizada.getFechaFin()
                        );
                        
                        // Excluir la reserva actual de los conflictos
                        reservasConflictivas.removeIf(r -> r.getIdReserva().equals(id));
                        
                        if (!reservasConflictivas.isEmpty()) {
                            throw new RuntimeException("La habitación no está disponible en las nuevas fechas");
                        }

                        // Recalcular monto
                        Habitacion habitacion = habitacionRepository.findById(reservaActualizada.getIdHabitacion())
                                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));
                        
                        long dias = ChronoUnit.DAYS.between(
                                reservaActualizada.getFechaInicio(),
                                reservaActualizada.getFechaFin()
                        );
                        if (dias == 0) dias = 1;
                        reservaActualizada.setMontoTotal(habitacion.getPrecioPorNoche() * dias);
                    }

                    reserva.setIdCliente(reservaActualizada.getIdCliente());
                    reserva.setIdHabitacion(reservaActualizada.getIdHabitacion());
                    reserva.setFechaInicio(reservaActualizada.getFechaInicio());
                    reserva.setFechaFin(reservaActualizada.getFechaFin());
                    reserva.setMontoTotal(reservaActualizada.getMontoTotal());
                    reserva.setEstado(reservaActualizada.getEstado());
                    
                    return reservaRepository.save(reserva);
                })
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + id));
    }

    // Cambiar estado de reserva
    public Reserva cambiarEstado(Long id, String nuevoEstado) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    String estadoAnterior = reserva.getEstado();
                    
                    // Validar transiciones de estado
                    if ("CANCELADA".equals(estadoAnterior)) {
                        throw new RuntimeException("No se puede cambiar el estado de una reserva cancelada");
                    }

                    reserva.setEstado(nuevoEstado);
                    
                    // Si se cancela, actualizar disponibilidad de habitación
                    if ("CANCELADA".equals(nuevoEstado)) {
                        habitacionRepository.findById(reserva.getIdHabitacion())
                                .ifPresent(habitacion -> {
                                    habitacion.setDisponible(true);
                                    habitacionRepository.save(habitacion);
                                });
                    }
                    
                    // Si se confirma, marcar habitación como ocupada
                    if ("CONFIRMADA".equals(nuevoEstado)) {
                        habitacionRepository.findById(reserva.getIdHabitacion())
                                .ifPresent(habitacion -> {
                                    habitacion.setDisponible(false);
                                    habitacionRepository.save(habitacion);
                                });
                    }
                    
                    return reservaRepository.save(reserva);
                })
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + id));
    }

    // Cancelar reserva
    public Reserva cancelarReserva(Long id) {
        return cambiarEstado(id, "CANCELADA");
    }

    // Confirmar reserva
    public Reserva confirmarReserva(Long id) {
        return cambiarEstado(id, "CONFIRMADA");
    }

    // Eliminar reserva
    public void eliminarReserva(Long id) {
        if (!reservaRepository.existsById(id)) {
            throw new RuntimeException("Reserva no encontrada con id: " + id);
        }
        reservaRepository.deleteById(id);
    }
}