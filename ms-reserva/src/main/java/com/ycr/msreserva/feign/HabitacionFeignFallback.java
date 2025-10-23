package com.ycr.msreserva.feign;

import com.ycr.msreserva.dtos.HabitacionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class HabitacionFeignFallback implements HabitacionFeign {
    
    @Override
    public ResponseEntity<HabitacionDTO> obtenerHabitacionPorId(Long id) {
        System.err.println("⚠️ Circuit Breaker activado: ms-habitaciones no disponible (ID: " + id + ")");
        throw new RuntimeException("Servicio de Habitaciones no disponible. Intente más tarde.");
    }
    
    @Override
    public ResponseEntity<HabitacionDTO> obtenerHabitacionPorNumero(String numero) {
        System.err.println("⚠️ Circuit Breaker activado: ms-habitaciones no disponible (Número: " + numero + ")");
        throw new RuntimeException("Servicio de Habitaciones no disponible. Intente más tarde.");
    }
    
    @Override
    public ResponseEntity<HabitacionDTO> cambiarDisponibilidad(Long id, Boolean disponible) {
        System.err.println("⚠️ Circuit Breaker activado: No se pudo cambiar disponibilidad de habitación " + id);
        throw new RuntimeException("Servicio de Habitaciones no disponible. Intente más tarde.");
    }
}