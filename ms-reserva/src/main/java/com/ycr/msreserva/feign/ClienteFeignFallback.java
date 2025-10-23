package com.ycr.msreserva.feign;

import com.ycr.msreserva.dtos.ClienteDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ClienteFeignFallback implements ClienteFeign {

    @Override
    public ResponseEntity<ClienteDTO> obtenerClientePorId(Long id) {
        System.err.println("⚠️ Circuit Breaker activado: ms-cliente no disponible (ID: " + id + ")");
        throw new RuntimeException("Servicio de Clientes no disponible. Intente más tarde.");
    }

    @Override
    public ResponseEntity<ClienteDTO> obtenerClientePorDni(String dni) {
        System.err.println("⚠️ Circuit Breaker activado: ms-cliente no disponible (DNI: " + dni + ")");
        throw new RuntimeException("Servicio de Clientes no disponible. Intente más tarde.");
    }
}