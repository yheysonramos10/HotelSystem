package com.ycr.msreserva.dtos;

import lombok.Data;

@Data
public class HabitacionDTO {
    private Long idHabitacion;
    private String numero;
    private String tipo;
    private Double precioPorNoche;
    private Boolean disponible;
    private String descripcion;
    private Integer capacidad;
    private String estado;
}
