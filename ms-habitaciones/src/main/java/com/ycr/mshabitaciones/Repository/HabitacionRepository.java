package com.ycr.mshabitaciones.Repository;

import com.ycr.mshabitaciones.Entity.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {
    Optional<Habitacion> findByNumero(String numero);
    List<Habitacion> findByDisponible(Boolean disponible);
    List<Habitacion> findByTipo(String tipo);
    List<Habitacion> findByEstado(String estado);
    List<Habitacion> findByTipoAndDisponible(String tipo, Boolean disponible);
    List<Habitacion> findByCapacidadGreaterThanEqual(Integer capacidad);
    boolean existsByNumero(String numero);
}