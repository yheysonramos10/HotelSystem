package com.ycr.msreserva.dtos;

import lombok.Data;

@Data
public class ClienteDTO {
    private Long idCliente;
    private String nombre;
    private String apellido;
    private String dni;
    private String correo;
    private String telefono;
}
