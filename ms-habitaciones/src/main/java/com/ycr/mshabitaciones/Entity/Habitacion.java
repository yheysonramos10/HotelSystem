package com.ycr.mshabitaciones.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "habitaciones")
public class Habitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHabitacion;
    private String numero; // Ej: "305"
    private String tipo; // simple, doble, suite
    private Double precioPorNoche;
    private Boolean disponible; // true = libre, false = ocupada
    private String descripcion;
    private Integer capacidad; // Ej: 2 personas
    // Estado adicional opcional
    private String estado; // ACTIVA, MANTENIMIENTO, INACTIVA
}