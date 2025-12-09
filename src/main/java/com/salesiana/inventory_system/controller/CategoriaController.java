package com.salesiana.inventory_system.controller;

import com.salesiana.inventory_system.entity.Categoria;
import com.salesiana.inventory_system.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    /**
     * Listar todas las categorías
     */
    @GetMapping
    public String listarCategorias(Model model) {
        try {
            System.out.println("=== CARGANDO LISTA DE CATEGORÍAS ===");
            
            List<Categoria> categorias = categoriaService.obtenerTodasCategorias();
            
            // Obtener estadísticas
            long categoriasActivas = categorias != null ? 
                categorias.stream().filter(c -> c.getActiva() != null && c.getActiva()).count() : 0;
            long categoriasInactivas = categorias != null ? 
                categorias.stream().filter(c -> c.getActiva() == null || !c.getActiva()).count() : 0;

            model.addAttribute("categorias", categorias != null ? categorias : Collections.emptyList());
            model.addAttribute("categoriasActivas", categoriasActivas);
            model.addAttribute("categoriasInactivas", categoriasInactivas);
            model.addAttribute("totalCategorias", categorias != null ? categorias.size() : 0);

            System.out.println("✅ Categorías cargadas exitosamente. Total: " + (categorias != null ? categorias.size() : 0));
            System.out.println("📊 Estadísticas: Activas=" + categoriasActivas + ", Inactivas=" + categoriasInactivas);

            return "categorias/lista";
        } catch (Exception e) {
            System.err.println("❌ Error al cargar categorías: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar las categorías: " + e.getMessage());
            model.addAttribute("categorias", Collections.emptyList());
            model.addAttribute("categoriasActivas", 0L);
            model.addAttribute("categoriasInactivas", 0L);
            model.addAttribute("totalCategorias", 0L);
            return "categorias/lista";
        }
    }

    /**
     * Mostrar formulario para nueva categoría
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        try {
            System.out.println("=== CARGANDO FORMULARIO NUEVA CATEGORÍA ===");
            
            if (!model.containsAttribute("categoria")) {
                model.addAttribute("categoria", new Categoria());
            }
            
            System.out.println("✅ Formulario de creación de categoría cargado exitosamente");
            return "categorias/form";
        } catch (Exception e) {
            System.err.println("❌ Error al cargar formulario de nueva categoría: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/categorias";
        }
    }

    /**
     * Mostrar formulario para editar categoría existente
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== CARGANDO FORMULARIO EDITAR CATEGORÍA ID: " + id + " ===");
            
            if (!model.containsAttribute("categoria")) {
                Categoria categoria = categoriaService.obtenerCategoriaPorId(id).orElse(null);
                
                if (categoria == null) {
                    System.out.println("❌ Categoría no encontrada");
                    redirectAttributes.addFlashAttribute("error", "Categoría no encontrada");
                    return "redirect:/categorias";
                }
                
                model.addAttribute("categoria", categoria);
            }

            System.out.println("✅ Formulario de edición cargado exitosamente");
            return "categorias/form";
        } catch (Exception e) {
            System.err.println("❌ Error al cargar formulario de editar categoría: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cargar la categoría: " + e.getMessage());
            return "redirect:/categorias";
        }
    }

    /**
     * Guardar categoría (nueva o editada)
     */
    @PostMapping("/guardar")
    public String guardarCategoria(@ModelAttribute Categoria categoria, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== GUARDANDO CATEGORÍA ===");
            System.out.println("ID: " + (categoria.getId() != null ? categoria.getId() : "nueva"));
            System.out.println("Nombre: " + categoria.getNombre());

            // Validaciones básicas
            if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El nombre de la categoría es requerido");
                redirectAttributes.addFlashAttribute("categoria", categoria);
                System.out.println("❌ Error: Nombre de categoría vacío");
                
                if (categoria.getId() != null) {
                    return "redirect:/categorias/editar/" + categoria.getId();
                } else {
                    return "redirect:/categorias/nuevo";
                }
            }

            // Si no se especifica activa, establecer como true
            if (categoria.getActiva() == null) {
                categoria.setActiva(true);
            }

            // Guardar categoría
            Categoria categoriaGuardada = categoriaService.guardarCategoria(categoria);

            if (categoriaGuardada != null) {
                String mensaje = categoria.getId() != null ? 
                    "Categoría actualizada correctamente" : 
                    "Categoría registrada correctamente";
                redirectAttributes.addFlashAttribute("success", mensaje);
                System.out.println("✅ " + mensaje + " - ID: " + categoriaGuardada.getId());
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo guardar la categoría");
                System.out.println("❌ Error: No se pudo guardar la categoría");
            }

            return "redirect:/categorias";
            
        } catch (RuntimeException e) {
            System.err.println("❌ Error al guardar categoría: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("categoria", categoria);
            
            if (categoria.getId() != null) {
                return "redirect:/categorias/editar/" + categoria.getId();
            } else {
                return "redirect:/categorias/nuevo";
            }
        } catch (Exception e) {
            System.err.println("❌ Error inesperado al guardar categoría: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error inesperado al guardar la categoría: " + e.getMessage());
            return "redirect:/categorias";
        }
    }

    /**
     * ✅ CORREGIDO: Eliminar categoría (desactivar) usando método específico
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== ELIMINANDO CATEGORÍA ID: " + id + " ===");
            
            // Usar el método específico de desactivación
            categoriaService.desactivarCategoria(id);

            redirectAttributes.addFlashAttribute("success", "Categoría desactivada correctamente");
            System.out.println("✅ Categoría desactivada exitosamente");

        } catch (Exception e) {
            System.err.println("❌ Error al eliminar categoría: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la categoría: " + e.getMessage());
        }

        return "redirect:/categorias";
    }

    /**
     * ✅ CORREGIDO: Activar/Desactivar categoría usando método específico
     */
    @GetMapping("/toggle-estado/{id}")
    public String toggleEstadoCategoria(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== CAMBIANDO ESTADO DE CATEGORÍA ID: " + id + " ===");
            
            // Obtener el estado actual para el mensaje
            Categoria categoria = categoriaService.obtenerCategoriaPorId(id).orElse(null);
            
            if (categoria == null) {
                redirectAttributes.addFlashAttribute("error", "Categoría no encontrada");
                return "redirect:/categorias";
            }

            boolean estadoAnterior = categoria.getActiva();

            // Usar el método específico de cambio de estado
            categoriaService.cambiarEstadoCategoria(id);

            String mensaje = !estadoAnterior ? 
                "Categoría activada correctamente" : 
                "Categoría desactivada correctamente";
            redirectAttributes.addFlashAttribute("success", mensaje);
            System.out.println("✅ " + mensaje);

        } catch (Exception e) {
            System.err.println("❌ Error al cambiar estado de categoría: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cambiar el estado: " + e.getMessage());
        }

        return "redirect:/categorias";
    }
}