/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesiana.inventory_system.service;

/**
 *
 * @author Andrei
 */

import com.salesiana.inventory_system.entity.TipoMovimiento;
import com.salesiana.inventory_system.repository.TipoMovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoMovimientoService {
    
    @Autowired
    private TipoMovimientoRepository tipoMovimientoRepository;
    
    public List<TipoMovimiento> obtenerTodosTiposMovimiento() {
        return tipoMovimientoRepository.findByActivoTrue();
    }
    
    public Optional<TipoMovimiento> obtenerTipoMovimientoPorCodigo(String codigo) {
        return tipoMovimientoRepository.findByCodigo(codigo);
    }
    
    public Optional<TipoMovimiento> obtenerTipoMovimientoPorId(Integer id) {
        return tipoMovimientoRepository.findById(id);
    }
    
    public TipoMovimiento guardarTipoMovimiento(TipoMovimiento tipoMovimiento) {
        return tipoMovimientoRepository.save(tipoMovimiento);
    }
    
    public void desactivarTipoMovimiento(Integer id) {
        tipoMovimientoRepository.findById(id).ifPresent(tipoMovimiento -> {
            tipoMovimiento.setActivo(false);
            tipoMovimientoRepository.save(tipoMovimiento);
        });
    }
}
