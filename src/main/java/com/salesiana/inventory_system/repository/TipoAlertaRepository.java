/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesiana.inventory_system.repository;

/**
 *
 * @author Andrei
 */
import com.salesiana.inventory_system.entity.TipoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface TipoAlertaRepository extends JpaRepository<TipoAlerta, Integer> {
    
    Optional<TipoAlerta> findByCodigo(String codigo);
    
    List<TipoAlerta> findByActivoTrue();
}