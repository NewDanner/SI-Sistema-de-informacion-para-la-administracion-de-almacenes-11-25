/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesiana.inventory_system.repository;

/**
 *
 * @author Andrei
 */

import com.salesiana.inventory_system.entity.ReporteGenerado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReporteGeneradoRepository extends JpaRepository<ReporteGenerado, Integer> {
    
    List<ReporteGenerado> findByUsuarioIdOrderByFechaGeneracionDesc(Integer usuarioId);
    
    List<ReporteGenerado> findByTipoReporteAndFechaGeneracionBetween(
        String tipoReporte, LocalDateTime inicio, LocalDateTime fin);
    
    @Query("SELECT r FROM ReporteGenerado r WHERE r.fechaGeneracion >= :fecha ORDER BY r.fechaGeneracion DESC")
    List<ReporteGenerado> findReportesRecientes(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT COUNT(r) FROM ReporteGenerado r WHERE r.fechaGeneracion >= :fecha")
    Long countReportesDesde(@Param("fecha") LocalDateTime fecha);
}