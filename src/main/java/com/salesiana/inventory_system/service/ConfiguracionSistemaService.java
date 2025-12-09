/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesiana.inventory_system.service;

/**
 *
 * @author Andrei
 */
import com.salesiana.inventory_system.entity.ConfiguracionSistema;
import com.salesiana.inventory_system.repository.ConfiguracionSistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfiguracionSistemaService {
    
    @Autowired
    private ConfiguracionSistemaRepository configuracionSistemaRepository;
    
    public Optional<ConfiguracionSistema> obtenerConfiguracion(String clave) {
        return configuracionSistemaRepository.findByClave(clave);
    }
    
    public String obtenerValorConfiguracion(String clave, String valorPorDefecto) {
        return configuracionSistemaRepository.findByClave(clave)
                .map(ConfiguracionSistema::getValor)
                .orElse(valorPorDefecto);
    }
    
    public Integer obtenerValorConfiguracionEntero(String clave, Integer valorPorDefecto) {
        try {
            return configuracionSistemaRepository.findByClave(clave)
                    .map(config -> {
                        // Verificar el tipo de dato
                        if (config.getTipoDato() == ConfiguracionSistema.TipoDato.INTEGER) {
                            return Integer.parseInt(config.getValor());
                        }
                        return valorPorDefecto;
                    })
                    .orElse(valorPorDefecto);
        } catch (NumberFormatException e) {
            return valorPorDefecto;
        }
    }
    
    public Boolean obtenerValorConfiguracionBooleano(String clave, Boolean valorPorDefecto) {
        return configuracionSistemaRepository.findByClave(clave)
                .map(config -> {
                    // Verificar el tipo de dato
                    if (config.getTipoDato() == ConfiguracionSistema.TipoDato.BOOLEAN) {
                        return Boolean.parseBoolean(config.getValor());
                    }
                    return valorPorDefecto;
                })
                .orElse(valorPorDefecto);
    }
    
    public ConfiguracionSistema guardarConfiguracion(ConfiguracionSistema configuracion) {
        return configuracionSistemaRepository.save(configuracion);
    }
    
    public boolean existeConfiguracion(String clave) {
        return configuracionSistemaRepository.existsByClave(clave);
    }
}