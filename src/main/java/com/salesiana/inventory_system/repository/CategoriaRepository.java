package com.salesiana.inventory_system.repository;

import com.salesiana.inventory_system.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    
    List<Categoria> findByActivaTrue();
    
    // ✅ NUEVO: Método para buscar por nombre
    Optional<Categoria> findByNombre(String nombre);
}