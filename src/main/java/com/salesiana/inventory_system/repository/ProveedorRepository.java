/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesiana.inventory_system.repository;

/**
 *
 * @author Andrei
 */

import com.salesiana.inventory_system.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    
    Optional<Proveedor> findByCodigo(String codigo);
    
    Optional<Proveedor> findByRucNit(String rucNit);
    
    List<Proveedor> findByActivoTrue();
    
    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);
}