package com.salesiana.inventory_system.service;

import com.salesiana.inventory_system.entity.Categoria;
import com.salesiana.inventory_system.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<Categoria> obtenerTodasCategorias() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Categoria> obtenerCategoriasActivas() {
        return categoriaRepository.findByActivaTrue();
    }

    @Transactional
    public Categoria guardarCategoria(Categoria categoria) {
        try {
            // ✅ VALIDACIÓN: Verificar si el nombre ya existe
            if (categoria.getId() == null) {
                // Es una categoría nueva, verificar que el nombre no exista
                Optional<Categoria> existente = categoriaRepository.findByNombre(categoria.getNombre());
                if (existente.isPresent()) {
                    throw new RuntimeException("Ya existe una categoría con el nombre: " + categoria.getNombre());
                }
            } else {
                // Es una edición, verificar que el nombre no esté usado por otra categoría
                Optional<Categoria> existente = categoriaRepository.findByNombre(categoria.getNombre());
                if (existente.isPresent() && !existente.get().getId().equals(categoria.getId())) {
                    throw new RuntimeException("Ya existe otra categoría con el nombre: " + categoria.getNombre());
                }
            }

            // Asignar fechas
            if (categoria.getId() == null) {
                categoria.setFechaCreacion(LocalDateTime.now());
            }
            categoria.setFechaActualizacion(LocalDateTime.now());

            // Asegurar que activa esté inicializado
            if (categoria.getActiva() == null) {
                categoria.setActiva(true);
            }

            return categoriaRepository.save(categoria);
            
        } catch (RuntimeException e) {
            // Re-lanzar RuntimeException para que el controller lo capture
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Error al guardar categoría: " + e.getMessage());
            throw new RuntimeException("Error al guardar la categoría: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> obtenerCategoriaPorId(Integer id) {
        return categoriaRepository.findById(id);
    }

    @Transactional
    public void eliminarCategoria(Integer id) {
        categoriaRepository.deleteById(id);
    }

    // ✅ NUEVO: Método específico para desactivar sin validaciones
    @Transactional
    public void desactivarCategoria(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        categoria.setActiva(false);
        categoria.setFechaActualizacion(LocalDateTime.now());
        // Guardar directamente sin pasar por validaciones
        categoriaRepository.save(categoria);
    }

    // ✅ NUEVO: Método específico para activar sin validaciones
    @Transactional
    public void activarCategoria(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        categoria.setActiva(true);
        categoria.setFechaActualizacion(LocalDateTime.now());
        // Guardar directamente sin pasar por validaciones
        categoriaRepository.save(categoria);
    }

    // ✅ NUEVO: Método para cambiar estado (toggle)
    @Transactional
    public void cambiarEstadoCategoria(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        categoria.setActiva(!categoria.getActiva());
        categoria.setFechaActualizacion(LocalDateTime.now());
        // Guardar directamente sin pasar por validaciones
        categoriaRepository.save(categoria);
    }
}