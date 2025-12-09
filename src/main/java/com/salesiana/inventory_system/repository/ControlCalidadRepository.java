package com.salesiana.inventory_system.repository;

import com.salesiana.inventory_system.entity.ControlCalidad;
import com.salesiana.inventory_system.entity.EstadoCalidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ControlCalidadRepository extends JpaRepository<ControlCalidad, Integer> {
    Optional<ControlCalidad> findByLoteId(Integer loteId);
    List<ControlCalidad> findByEstadoCalidad(EstadoCalidad estadoCalidad);
    List<ControlCalidad> findByEstadoCalidadOrderByFechaCreacionDesc(EstadoCalidad estadoCalidad);
    List<ControlCalidad> findByFechaInspeccionBetween(LocalDateTime inicio, LocalDateTime fin);
    
    @Query("SELECT c FROM ControlCalidad c WHERE c.estadoCalidad = 'PENDIENTE' ORDER BY c.fechaCreacion ASC")
    List<ControlCalidad> findPendientesDeInspeccion();
    
    @Query("SELECT c FROM ControlCalidad c WHERE c.estadoCalidad = 'EN_CUARENTENA' ORDER BY c.fechaCreacion ASC")
    List<ControlCalidad> findEnCuarentena();
    
    Long countByEstadoCalidad(EstadoCalidad estadoCalidad);
}