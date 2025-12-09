/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesiana.inventory_system.service;

/**
 *
 * @author Andrei
 */

import com.salesiana.inventory_system.entity.TipoAlerta;
import com.salesiana.inventory_system.repository.TipoAlertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoAlertaService {
    
    @Autowired
    private TipoAlertaRepository tipoAlertaRepository;
    
    public List<TipoAlerta> obtenerTodosTiposAlerta() {
        return tipoAlertaRepository.findByActivoTrue();
    }
    
    public Optional<TipoAlerta> obtenerTipoAlertaPorCodigo(String codigo) {
        return tipoAlertaRepository.findByCodigo(codigo);
    }
    
    public Optional<TipoAlerta> obtenerTipoAlertaPorId(Integer id) {
        return tipoAlertaRepository.findById(id);
    }
    
    public TipoAlerta guardarTipoAlerta(TipoAlerta tipoAlerta) {
        return tipoAlertaRepository.save(tipoAlerta);
    }
    
    public void desactivarTipoAlerta(Integer id) {
        tipoAlertaRepository.findById(id).ifPresent(tipoAlerta -> {
            tipoAlerta.setActivo(false);
            tipoAlertaRepository.save(tipoAlerta);
        });
    }
}
