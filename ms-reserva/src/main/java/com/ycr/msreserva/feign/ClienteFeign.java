package com.ycr.msreserva.feign;

import com.ycr.msreserva.dtos.ClienteDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-cliente", path = "/clientes")
public interface ClienteFeign {

    @GetMapping("/{id}")
    @CircuitBreaker(name = "clientePorIdCB", fallbackMethod = "fallbackClientePorId")
    ResponseEntity<ClienteDTO> obtenerClientePorId(@PathVariable("id") Long id);
    default ResponseEntity<ClienteDTO> fallbackClientePorId(Long id, Exception e) {
        ClienteDTO dto = new ClienteDTO();
        dto.setIdCliente(0L);
        dto.setNombre("Desconocido");
        dto.setApellido("Desconocido");
        dto.setDni("00000000");
        dto.setCorreo("sin_correo@fallback.com");
        System.err.println("⚠️ CircuitBreaker: ms-cliente no disponible (ID: " + id + ")");
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/dni/{dni}")
    @CircuitBreaker(name = "clientePorDniCB", fallbackMethod = "fallbackClientePorDni")
    ResponseEntity<ClienteDTO> obtenerClientePorDni(@PathVariable("dni") String dni);
    default ResponseEntity<ClienteDTO> fallbackClientePorDni(String dni, Exception e) {
        ClienteDTO dto = new ClienteDTO();
        dto.setIdCliente(0L);
        dto.setNombre("Desconocido");
        dto.setApellido("Desconocido");
        dto.setDni(dni);
        dto.setCorreo("sin_correo@fallback.com");
        System.err.println("⚠️ CircuitBreaker: ms-cliente no disponible (DNI: " + dni + ")");
        return ResponseEntity.ok(dto);
    }
}
