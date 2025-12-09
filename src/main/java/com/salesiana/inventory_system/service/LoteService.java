/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesiana.inventory_system.service;

/**
 *
 * @author Andrei
 */

import com.salesiana.inventory_system.entity.Lote;
import com.salesiana.inventory_system.repository.LoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoteService {
    
    @Autowired
    private LoteRepository loteRepository;
    
    public List<Lote> obtenerTodosLotes() {
        return loteRepository.findAll();
    }
    
    public Optional<Lote> obtenerLotePorId(Integer id) {
        return loteRepository.findById(id);
    }
    
    public List<Lote> obtenerLotesPorProducto(Integer productoId) {
        return loteRepository.findByProductoId(productoId);
    }
    
    public List<Lote> obtenerLotesPorVencer(Integer dias) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(dias);
        return loteRepository.findByFechaVencimientoBetween(hoy, fechaLimite);
    }
    
    public List<Lote> obtenerLotesVencidos() {
        LocalDate hoy = LocalDate.now();
        return loteRepository.findByFechaVencimientoBeforeAndCantidadActualGreaterThan(hoy, 0);
    }
    
    public Lote guardarLote(Lote lote) {
        return loteRepository.save(lote);
    }
    
    public void eliminarLote(Integer id) {
        loteRepository.findById(id).ifPresent(lote -> {
            lote.setActivo(false);
            loteRepository.save(lote);
        });
    }
    
    public List<Lote> obtenerLotesDisponiblesPorProducto(Integer productoId) {
        return loteRepository.findLotesDisponiblesPorProducto(productoId);
    }
}