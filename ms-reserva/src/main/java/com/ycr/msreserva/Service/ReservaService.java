package com.ycr.msreserva.Service;

import com.ycr.msreserva.Entity.Reserva;
import com.ycr.msreserva.Repository.ReservaRepository;
import com.ycr.msreserva.dtos.ClienteDTO;
import com.ycr.msreserva.dtos.HabitacionDTO;
import com.ycr.msreserva.feign.ClienteFeign;
import com.ycr.msreserva.feign.HabitacionFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    @Autowired
    private ClienteFeign clienteFeign;
    @Autowired
    private HabitacionFeign habitacionFeign;

    // Crear reserva
    public Reserva crearReserva(Reserva reserva) {
        // 🗓 Validar fechas
        if (reserva.getFechaInicio().isAfter(reserva.getFechaFin())) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        if (reserva.getFechaInicio().isBefore(LocalDate.now())) {
            throw new RuntimeException("La fecha de inicio no puede ser anterior a hoy");
        }

        //  Verificar que el cliente exista
        ResponseEntity<ClienteDTO> clienteResponse = clienteFeign.obtenerClientePorId(reserva.getIdCliente());
        if (!clienteResponse.getStatusCode().is2xxSuccessful() || clienteResponse.getBody() == null) {
            throw new RuntimeException("Cliente no encontrado con ID: " + reserva.getIdCliente());
        }

        //  Verificar que la habitación exista
        ResponseEntity<HabitacionDTO> habitacionResponse = habitacionFeign.obtenerHabitacionPorId(reserva.getIdHabitacion());
        if (!habitacionResponse.getStatusCode().is2xxSuccessful() || habitacionResponse.getBody() == null) {
            throw new RuntimeException("Habitación no encontrada con ID: " + reserva.getIdHabitacion());
        }

        HabitacionDTO habitacion = habitacionResponse.getBody();

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
        if (dias <= 0) dias = 1; // Mínimo 1 día
        reserva.setMontoTotal(habitacion.getPrecioPorNoche() * dias);

        // ⚙ Establecer valores por defecto
        if (reserva.getEstado() == null) {
            reserva.setEstado("PENDIENTE");
        }
        reserva.setFechaCreacion(LocalDateTime.now());

        //  Guardar reserva
        Reserva reservaGuardada = reservaRepository.save(reserva);

        // Actualizar disponibilidad de la habitación
        try {
            habitacionFeign.cambiarDisponibilidad(habitacion.getIdHabitacion(), false);
        } catch (Exception e) {
            System.err.println("No se pudo actualizar la disponibilidad de la habitación (posible fallback activado)");
        }

        return reservaGuardada;
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

                    //  No permitir actualizar una reserva cancelada
                    if ("CANCELADA".equalsIgnoreCase(reserva.getEstado())) {
                        throw new RuntimeException("No se puede actualizar una reserva cancelada");
                    }

                    // Validar cliente
                    ResponseEntity<ClienteDTO> clienteResponse = clienteFeign.obtenerClientePorId(reservaActualizada.getIdCliente());
                    if (!clienteResponse.getStatusCode().is2xxSuccessful() || clienteResponse.getBody() == null) {
                        throw new RuntimeException("Cliente no encontrado con ID: " + reservaActualizada.getIdCliente());
                    }

                    // Validar habitación
                    ResponseEntity<HabitacionDTO> habitacionResponse = habitacionFeign.obtenerHabitacionPorId(reservaActualizada.getIdHabitacion());
                    if (!habitacionResponse.getStatusCode().is2xxSuccessful() || habitacionResponse.getBody() == null) {
                        throw new RuntimeException("Habitación no encontrada con ID: " + reservaActualizada.getIdHabitacion());
                    }

                    HabitacionDTO habitacion = habitacionResponse.getBody();

                    // Verificar si cambian las fechas o la habitación
                    boolean cambiosDeDisponibilidad =
                            !reserva.getIdHabitacion().equals(reservaActualizada.getIdHabitacion()) ||
                                    !reserva.getFechaInicio().equals(reservaActualizada.getFechaInicio()) ||
                                    !reserva.getFechaFin().equals(reservaActualizada.getFechaFin());

                    if (cambiosDeDisponibilidad) {
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

                        // Recalcular monto total
                        long dias = ChronoUnit.DAYS.between(
                                reservaActualizada.getFechaInicio(),
                                reservaActualizada.getFechaFin()
                        );
                        if (dias <= 0) dias = 1;

                        reservaActualizada.setMontoTotal(habitacion.getPrecioPorNoche() * dias);
                    }

                    // Actualizar campos
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
    //* Cambiar estado de reserva
    public Reserva cambiarEstado(Long id, String nuevoEstado) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    String estadoAnterior = reserva.getEstado();

                    // 🔒 Validar transición de estado
                    if ("CANCELADA".equals(estadoAnterior)) {
                        throw new RuntimeException("No se puede cambiar el estado de una reserva cancelada");
                    }

                    reserva.setEstado(nuevoEstado);

                    // 🔄 Cambiar disponibilidad en ms-habitacion según el nuevo estado
                    Long idHabitacion = reserva.getIdHabitacion();

                    try {
                        if ("CANCELADA".equals(nuevoEstado)) {
                            habitacionFeign.cambiarDisponibilidad(idHabitacion, true);
                            System.out.println("✔ Habitación liberada (disponible nuevamente)");
                        } else if ("CONFIRMADA".equals(nuevoEstado)) {
                            habitacionFeign.cambiarDisponibilidad(idHabitacion, false);
                            System.out.println("✔ Habitación marcada como ocupada");
                        }
                    } catch (Exception e) {
                        System.err.println(" Error al actualizar disponibilidad de habitación vía Feign: " + e.getMessage());
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