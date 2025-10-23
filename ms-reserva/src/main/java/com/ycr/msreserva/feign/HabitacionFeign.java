package com.ycr.msreserva.feign;

import com.ycr.msreserva.dtos.HabitacionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-habitaciones", path = "/habitaciones", fallback = HabitacionFeignFallback.class)
public interface HabitacionFeign {

    @GetMapping("/{id}")
    ResponseEntity<HabitacionDTO> obtenerHabitacionPorId(@PathVariable("id") Long id);

    @GetMapping("/numero/{numero}")
    ResponseEntity<HabitacionDTO> obtenerHabitacionPorNumero(@PathVariable("numero") String numero);

    @PatchMapping("/{id}/disponibilidad")
    ResponseEntity<HabitacionDTO> cambiarDisponibilidad(
            @PathVariable("id") Long id,
            @RequestParam("disponible") Boolean disponible
    );
}