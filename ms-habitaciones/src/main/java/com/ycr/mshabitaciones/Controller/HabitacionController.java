package com.ycr.mshabitaciones.Controller;


import com.ycr.mshabitaciones.Entity.Habitacion;
import com.ycr.mshabitaciones.Service.HabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/habitaciones")
public class HabitacionController {

    @Autowired
    private HabitacionService habitacionService;

    // CREATE - Crear una nueva habitación
    @PostMapping
    public ResponseEntity<?> crearHabitacion(@RequestBody Habitacion habitacion) {
        try {
            Habitacion nuevaHabitacion = habitacionService.crearHabitacion(habitacion);
            return new ResponseEntity<>(nuevaHabitacion, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // READ - Obtener todas las habitaciones
    @GetMapping
    public ResponseEntity<List<Habitacion>> obtenerTodasLasHabitaciones() {
        List<Habitacion> habitaciones = habitacionService.obtenerTodasLasHabitaciones();
        return new ResponseEntity<>(habitaciones, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerHabitacionPorId(@PathVariable Long id) {
        return habitacionService.obtenerHabitacionPorId(id)
                .<ResponseEntity<?>>map(habitacion -> ResponseEntity.ok(habitacion))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Habitación no encontrada"));
    }


    @GetMapping("/numero/{numero}")
    public ResponseEntity<Habitacion> obtenerHabitacionPorNumero(@PathVariable String numero) {
        Optional<Habitacion> habitacionOpt = habitacionService.obtenerHabitacionPorNumero(numero);
        if (habitacionOpt.isPresent()) {
            return ResponseEntity.ok(habitacionOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // READ - Obtener habitaciones disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<Habitacion>> obtenerHabitacionesDisponibles() {
        List<Habitacion> habitaciones = habitacionService.obtenerHabitacionesDisponibles();
        return new ResponseEntity<>(habitaciones, HttpStatus.OK);
    }

    // READ - Obtener habitaciones por tipo
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Habitacion>> obtenerHabitacionesPorTipo(@PathVariable String tipo) {
        List<Habitacion> habitaciones = habitacionService.obtenerHabitacionesPorTipo(tipo);
        return new ResponseEntity<>(habitaciones, HttpStatus.OK);
    }

    // READ - Obtener habitaciones por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Habitacion>> obtenerHabitacionesPorEstado(@PathVariable String estado) {
        List<Habitacion> habitaciones = habitacionService.obtenerHabitacionesPorEstado(estado);
        return new ResponseEntity<>(habitaciones, HttpStatus.OK);
    }

    // READ - Obtener habitaciones por tipo y disponibilidad
    @GetMapping("/buscar")
    public ResponseEntity<List<Habitacion>> buscarHabitaciones(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Boolean disponible) {
        
        if (tipo != null && disponible != null) {
            List<Habitacion> habitaciones = habitacionService.obtenerHabitacionesPorTipoYDisponibilidad(tipo, disponible);
            return new ResponseEntity<>(habitaciones, HttpStatus.OK);
        }
        
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    // READ - Obtener habitaciones por capacidad
    @GetMapping("/capacidad/{capacidad}")
    public ResponseEntity<List<Habitacion>> obtenerHabitacionesPorCapacidad(@PathVariable Integer capacidad) {
        List<Habitacion> habitaciones = habitacionService.obtenerHabitacionesPorCapacidad(capacidad);
        return new ResponseEntity<>(habitaciones, HttpStatus.OK);
    }

    // UPDATE - Actualizar habitación
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarHabitacion(@PathVariable Long id, @RequestBody Habitacion habitacion) {
        try {
            Habitacion habitacionActualizada = habitacionService.actualizarHabitacion(id, habitacion);
            return new ResponseEntity<>(habitacionActualizada, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // UPDATE - Cambiar disponibilidad
    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<?> cambiarDisponibilidad(
            @PathVariable Long id,
            @RequestParam Boolean disponible) {
        try {
            Habitacion habitacion = habitacionService.cambiarDisponibilidad(id, disponible);
            return new ResponseEntity<>(habitacion, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // UPDATE - Cambiar estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        try {
            Habitacion habitacion = habitacionService.cambiarEstado(id, estado);
            return new ResponseEntity<>(habitacion, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // DELETE - Eliminar habitación
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarHabitacion(@PathVariable Long id) {
        try {
            habitacionService.eliminarHabitacion(id);
            return new ResponseEntity<>("Habitación eliminada exitosamente", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}