package com.ycr.msreserva.feign;

import com.ycr.msreserva.dtos.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-cliente", path = "clientes", fallback = ClienteFeignFallback.class)
public interface ClienteFeign {

    @GetMapping("/{id}")
    ResponseEntity<ClienteDTO> obtenerClientePorId(@PathVariable("id") Long id);

    @GetMapping("/dni/{dni}")
    ResponseEntity<ClienteDTO> obtenerClientePorDni(@PathVariable("dni") String dni);
}