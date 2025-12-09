/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesiana.inventory_system.repository;

/**
 *
 * @author Andrei
 */

import com.salesiana.inventory_system.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {
    
    List<Movimiento> findByProductoIdOrderByFechaMovimientoDesc(Integer productoId);
    
    List<Movimiento> findByTipoMovimientoId(Integer tipoMovimientoId);
    
    @Query("SELECT m FROM Movimiento m WHERE m.fechaMovimiento BETWEEN :startDate AND :endDate ORDER BY m.fechaMovimiento DESC")
    List<Movimiento> findMovimientosPorRangoFechas(@Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT m FROM Movimiento m WHERE m.producto.id = :productoId AND m.fechaMovimiento BETWEEN :startDate AND :endDate")
    List<Movimiento> findMovimientosProductoPorFecha(@Param("productoId") Integer productoId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);
}