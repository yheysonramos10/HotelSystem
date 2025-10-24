package com.ycr.msreserva.Controller;


import com.ycr.msreserva.Entity.Reserva;
import com.ycr.msreserva.Service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    // CREATE - Crear una nueva reserva
    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestBody Reserva reserva) {
        try {
            Reserva nuevaReserva = reservaService.crearReserva(reserva);
            return new ResponseEntity<>(nuevaReserva, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // READ - Obtener todas las reservas
    @GetMapping
    public ResponseEntity<List<Reserva>> obtenerTodasLasReservas() {
        List<Reserva> reservas = reservaService.obtenerTodasLasReservas();
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    // READ - Obtener reserva por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerReservaPorId(@PathVariable Long id) {
        return reservaService.obtenerReservaPorId(id)
                .<ResponseEntity<?>>map(reserva -> ResponseEntity.ok(reserva))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Reserva no encontrada"));
    }


    // READ - Obtener reservas por cliente
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Reserva>> obtenerReservasPorCliente(@PathVariable Long idCliente) {
        List<Reserva> reservas = reservaService.obtenerReservasPorCliente(idCliente);
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    // READ - Obtener reservas por habitación
    @GetMapping("/habitacion/{idHabitacion}")
    public ResponseEntity<List<Reserva>> obtenerReservasPorHabitacion(@PathVariable Long idHabitacion) {
        List<Reserva> reservas = reservaService.obtenerReservasPorHabitacion(idHabitacion);
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    // READ - Obtener reservas por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Reserva>> obtenerReservasPorEstado(@PathVariable String estado) {
        List<Reserva> reservas = reservaService.obtenerReservasPorEstado(estado);
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    // READ - Obtener reservas activas de una habitación
    @GetMapping("/habitacion/{idHabitacion}/activas")
    public ResponseEntity<List<Reserva>> obtenerReservasActivas(@PathVariable Long idHabitacion) {
        List<Reserva> reservas = reservaService.obtenerReservasActivas(idHabitacion);
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    // READ - Obtener reservas por rango de fechas
    @GetMapping("/fechas")
    public ResponseEntity<List<Reserva>> obtenerReservasPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        List<Reserva> reservas = reservaService.obtenerReservasPorRangoFechas(inicio, fin);
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    // READ - Obtener reservas para check-out hoy
    @GetMapping("/checkout/hoy")
    public ResponseEntity<List<Reserva>> obtenerReservasCheckOutHoy() {
        List<Reserva> reservas = reservaService.obtenerReservasCheckOutHoy();
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    // READ - Obtener reservas para check-in hoy
    @GetMapping("/checkin/hoy")
    public ResponseEntity<List<Reserva>> obtenerReservasCheckInHoy() {
        List<Reserva> reservas = reservaService.obtenerReservasCheckInHoy();
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    // READ - Verificar disponibilidad
    @GetMapping("/disponibilidad")
    public ResponseEntity<?> verificarDisponibilidad(
            @RequestParam Long idHabitacion,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        boolean disponible = reservaService.verificarDisponibilidad(idHabitacion, fechaInicio, fechaFin);
        return new ResponseEntity<>(disponible, HttpStatus.OK);
    }

    // UPDATE - Actualizar reserva
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarReserva(@PathVariable Long id, @RequestBody Reserva reserva) {
        try {
            Reserva reservaActualizada = reservaService.actualizarReserva(id, reserva);
            return new ResponseEntity<>(reservaActualizada, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // UPDATE - Cambiar estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        try {
            Reserva reserva = reservaService.cambiarEstado(id, estado);
            return new ResponseEntity<>(reserva, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // UPDATE - Confirmar reserva
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarReserva(@PathVariable Long id) {
        try {
            Reserva reserva = reservaService.confirmarReserva(id);
            return new ResponseEntity<>(reserva, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // UPDATE - Cancelar reserva
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarReserva(@PathVariable Long id) {
        try {
            Reserva reserva = reservaService.cancelarReserva(id);
            return new ResponseEntity<>(reserva, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE - Eliminar reserva
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarReserva(@PathVariable Long id) {
        try {
            reservaService.eliminarReserva(id);
            return new ResponseEntity<>("Reserva eliminada exitosamente", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}