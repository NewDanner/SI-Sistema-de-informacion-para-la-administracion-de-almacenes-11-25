package com.salesiana.inventory_system.service;

import com.salesiana.inventory_system.entity.UbicacionAlmacen;
import com.salesiana.inventory_system.repository.UbicacionAlmacenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UbicacionAlmacenService {
    
    @Autowired
    private UbicacionAlmacenRepository ubicacionRepository;
    
    public List<UbicacionAlmacen> obtenerTodasUbicaciones() {
        return ubicacionRepository.findByActivaTrue();
    }
    
    public List<UbicacionAlmacen> obtenerUbicacionesPrincipales() {
        return ubicacionRepository.findUbicacionesPrincipales();
    }
    
    public List<UbicacionAlmacen> obtenerUbicacionesConCapacidad() {
        return ubicacionRepository.findUbicacionesConCapacidad();
    }
    
    public Optional<UbicacionAlmacen> obtenerPorId(Integer id) {
        return ubicacionRepository.findById(id);
    }
    
    public Optional<UbicacionAlmacen> obtenerPorCodigo(String codigo) {
        return ubicacionRepository.findByCodigo(codigo);
    }
    
    public UbicacionAlmacen guardar(UbicacionAlmacen ubicacion) {
        return ubicacionRepository.save(ubicacion);
    }
    
    public void eliminar(Integer id) {
        ubicacionRepository.findById(id).ifPresent(ubicacion -> {
            ubicacion.setActiva(false);
            ubicacionRepository.save(ubicacion);
        });
    }
    
    public boolean verificarCapacidadDisponible(Integer ubicacionId, int cantidad) {
        return ubicacionRepository.findById(ubicacionId)
            .map(u -> u.tieneCapacidadDisponible(cantidad))
            .orElse(false);
    }
}