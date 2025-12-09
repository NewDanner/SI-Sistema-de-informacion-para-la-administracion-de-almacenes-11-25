/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesiana.inventory_system.repository;

/**
 *
 * @author Andrei
 */

import com.salesiana.inventory_system.entity.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Integer> {
    
    List<Lote> findByProductoId(Integer productoId);
    
    Optional<Lote> findByNumeroLote(String numeroLote);
    
    List<Lote> findByFechaVencimientoBetween(LocalDate startDate, LocalDate endDate);
    
    List<Lote> findByFechaVencimientoBeforeAndCantidadActualGreaterThan(LocalDate date, Integer cantidad);
    
    @Query("SELECT l FROM Lote l WHERE l.producto.id = :productoId AND l.cantidadActual > 0 AND l.activo = true")
    List<Lote> findLotesDisponiblesPorProducto(@Param("productoId") Integer productoId);
    
    @Query("SELECT l FROM Lote l WHERE l.fechaVencimiento BETWEEN :startDate AND :endDate AND l.cantidadActual > 0")
    List<Lote> findLotesPorVencer(@Param("startDate") LocalDate startDate, 
                                  @Param("endDate") LocalDate endDate);
}