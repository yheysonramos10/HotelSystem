package com.ycr.msreserva.feign;

import com.ycr.msreserva.dtos.HabitacionDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-habitacion", path = "/habitaciones")
public interface HabitacionFeign {

    @GetMapping("/{id}")
    @CircuitBreaker(name = "habitacionPorIdCB", fallbackMethod = "fallbackHabitacionPorId")
    ResponseEntity<HabitacionDTO> obtenerHabitacionPorId(@PathVariable("id") Long id);
    default ResponseEntity<HabitacionDTO> fallbackHabitacionPorId(Long id, Exception e) {
        HabitacionDTO dto = new HabitacionDTO();
        dto.setIdHabitacion(0L);
        dto.setNumero("Desconocido");
        dto.setTipo("Indefinido");
        dto.setDisponible(false);
        System.err.println("CircuitBreaker: ms-habitaciones no disponible (ID: " + id + ")");
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/numero/{numero}")
    @CircuitBreaker(name = "habitacionPorNumeroCB", fallbackMethod = "fallbackHabitacionPorNumero")
    ResponseEntity<HabitacionDTO> obtenerHabitacionPorNumero(@PathVariable("numero") String numero);
    default ResponseEntity<HabitacionDTO> fallbackHabitacionPorNumero(String numero, Exception e) {
        HabitacionDTO dto = new HabitacionDTO();
        dto.setIdHabitacion(0L);
        dto.setNumero("Desconocido");
        dto.setTipo("Indefinido");
        dto.setDisponible(false);
        System.err.println("CircuitBreaker: ms-habitaciones no disponible (Numero: "+ numero  + ")");
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}/disponibilidad")
    @CircuitBreaker(name = "cambiarDisponibilidadCB", fallbackMethod = "fallbackCambiarDisponibilidad")
    ResponseEntity<HabitacionDTO> cambiarDisponibilidad(
            @PathVariable("id") Long id,
            @RequestParam("disponible") Boolean disponible
    );
    default ResponseEntity<HabitacionDTO> fallbackCambiarDisponibilidad(Long id, Boolean disponible, Exception e) {
        HabitacionDTO dto = new HabitacionDTO();
        dto.setIdHabitacion(0L);
        dto.setNumero("Desconocido");
        dto.setTipo("Indefinido");
        dto.setDisponible(false);
        System.err.println("CircuitBreaker: ms-habitaciones no disponible (ID: "+ id + ", Disponible: " + disponible + ")");
        return ResponseEntity.ok(dto);
    }
}
