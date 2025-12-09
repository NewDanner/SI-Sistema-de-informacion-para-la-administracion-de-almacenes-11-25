/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesiana.inventory_system.repository;

/**
 *
 * @author Andrei
 */

import com.salesiana.inventory_system.entity.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Integer> {
    
    List<Alerta> findByLeidaFalseOrderByFechaAlertaDesc();
    
    List<Alerta> findByProductoIdOrderByFechaAlertaDesc(Integer productoId);
    
    @Query("SELECT a FROM Alerta a WHERE a.fechaAlerta >= :fecha AND a.leida = false")
    List<Alerta> findAlertasRecientesNoLeidas(@Param("fecha") LocalDateTime fecha);
    
    boolean existsByProductoIdAndTipoAlertaIdAndFechaAlertaAfter(
        @Param("productoId") Integer productoId, 
        @Param("tipoAlertaId") Integer tipoAlertaId, 
        @Param("fecha") LocalDateTime fecha
    );
    
    boolean existsByLoteIdAndTipoAlertaIdAndFechaAlertaAfter(
        @Param("loteId") Integer loteId, 
        @Param("tipoAlertaId") Integer tipoAlertaId, 
        @Param("fecha") LocalDateTime fecha
    );
    
    @Query("SELECT COUNT(a) FROM Alerta a WHERE a.leida = false")
    Long countAlertasNoLeidas();
}