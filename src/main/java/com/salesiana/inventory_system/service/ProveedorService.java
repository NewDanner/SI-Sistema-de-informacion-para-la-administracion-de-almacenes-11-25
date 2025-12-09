/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesiana.inventory_system.service;

/**
 *
 * @author Andrei
 */

import com.salesiana.inventory_system.entity.Proveedor;
import com.salesiana.inventory_system.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {
    
    @Autowired
    private ProveedorRepository proveedorRepository;
    
    public List<Proveedor> obtenerTodosProveedores() {
        return proveedorRepository.findByActivoTrue();
    }
    
    public Optional<Proveedor> obtenerProveedorPorId(Integer id) {
        return proveedorRepository.findById(id);
    }
    
    public Proveedor guardarProveedor(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }
    
    public void eliminarProveedor(Integer id) {
        proveedorRepository.findById(id).ifPresent(proveedor -> {
            proveedor.setActivo(false);
            proveedorRepository.save(proveedor);
        });
    }
    
    public List<Proveedor> buscarProveedores(String criterio) {
        return proveedorRepository.findByNombreContainingIgnoreCase(criterio);
    }
}
