package com.ycr.msreserva.Repository;

import com.ycr.msreserva.Entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByIdCliente(Long idCliente);
    List<Reserva> findByIdHabitacion(Long idHabitacion);
    List<Reserva> findByEstado(String estado);

    // Verificar si hay solapamiento de fechas para una habitación
    @Query("SELECT r FROM Reserva r WHERE r.idHabitacion = :idHabitacion " +
           "AND r.estado IN ('CONFIRMADA', 'PENDIENTE', 'PENDIENTE_CONFIRMACION') " +
           "AND ((r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio))")
    List<Reserva> findReservasConflictivas(
        @Param("idHabitacion") Long idHabitacion,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );
    
    // Reservas activas de una habitación
    @Query("SELECT r FROM Reserva r WHERE r.idHabitacion = :idHabitacion " +
           "AND r.estado IN ('CONFIRMADA', 'PENDIENTE_CONFIRMACION') " +
           "AND r.fechaFin >= :fechaActual")
    List<Reserva> findReservasActivasPorHabitacion(
        @Param("idHabitacion") Long idHabitacion,
        @Param("fechaActual") LocalDate fechaActual
    );
    
    // Reservas por rango de fechas
    List<Reserva> findByFechaInicioBetween(LocalDate inicio, LocalDate fin);
    
    // Reservas que finalizan hoy (para check-out)
    @Query("SELECT r FROM Reserva r WHERE r.fechaFin = :fecha AND r.estado = 'CONFIRMADA'")
    List<Reserva> findReservasPorCheckOut(@Param("fecha") LocalDate fecha);
    
    // Reservas que inician hoy (para check-in)
    @Query("SELECT r FROM Reserva r WHERE r.fechaInicio = :fecha AND r.estado = 'CONFIRMADA'")
    List<Reserva> findReservasPorCheckIn(@Param("fecha") LocalDate fecha);
}