package com.salesiana.inventory_system.controller;

import com.salesiana.inventory_system.entity.Lote;
import com.salesiana.inventory_system.entity.Producto;
import com.salesiana.inventory_system.service.LoteService;
import com.salesiana.inventory_system.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/lotes")
public class LoteController {

    @Autowired
    private LoteService loteService;

    @Autowired
    private ProductoService productoService;

    /**
     * Listar todos los lotes con filtros opcionales
     */
    @GetMapping
    public String listarLotes(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer productoId,
            Model model) {
        try {
            System.out.println("=== CARGANDO LISTA DE LOTES ===");
            System.out.println("Filtro estado: " + (estado != null ? estado : "ninguno"));
            System.out.println("Filtro producto ID: " + (productoId != null ? productoId : "ninguno"));

            List<Lote> lotes;

            // Aplicar filtros
            if (productoId != null) {
                lotes = loteService.obtenerLotesPorProducto(productoId);
                System.out.println("🔍 Filtro: Lotes del producto ID = " + productoId);
            } else {
                lotes = loteService.obtenerTodosLotes();
                System.out.println("🔍 Cargando todos los lotes");
            }

            // Filtrar por estado si se especifica
            if (estado != null && !estado.isEmpty() && lotes != null) {
                final String estadoBuscar = estado;
                lotes = lotes.stream()
                    .filter(l -> l.getEstadoCalidad() != null && 
                                 l.getEstadoCalidad().name().equals(estadoBuscar))
                    .collect(Collectors.toList());
                System.out.println("🔍 Filtrado por estado: " + estado);
            }

            // Obtener estadísticas
            long lotesActivos = lotes != null ? 
                lotes.stream().filter(l -> l.getActivo() != null && l.getActivo()).count() : 0;
            long lotesPorVencer = loteService.obtenerLotesPorVencer(30).size();
            long lotesEnCuarentena = lotes != null ?
                lotes.stream().filter(l -> l.getEstadoCalidad() != null && 
                                          l.getEstadoCalidad().name().equals("EN_CUARENTENA")).count() : 0;

            model.addAttribute("lotes", lotes != null ? lotes : Collections.emptyList());
            model.addAttribute("lotesActivos", lotesActivos);
            model.addAttribute("lotesPorVencer", lotesPorVencer);
            model.addAttribute("lotesEnCuarentena", lotesEnCuarentena);
            model.addAttribute("estadoFilter", estado);
            model.addAttribute("productoIdFilter", productoId);
            model.addAttribute("productos", productoService.obtenerTodosProductos());

            System.out.println("✅ Lotes cargados exitosamente. Total: " + (lotes != null ? lotes.size() : 0));

            return "lotes/lista";
        } catch (Exception e) {
            System.err.println("❌ Error al cargar lotes: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar los lotes: " + e.getMessage());
            model.addAttribute("lotes", Collections.emptyList());
            model.addAttribute("lotesActivos", 0L);
            model.addAttribute("lotesPorVencer", 0L);
            model.addAttribute("lotesEnCuarentena", 0L);
            model.addAttribute("productos", Collections.emptyList());
            return "lotes/lista";
        }
    }

    /**
     * Mostrar formulario para nuevo lote
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        try {
            System.out.println("=== CARGANDO FORMULARIO NUEVO LOTE ===");
            model.addAttribute("lote", new Lote());
            model.addAttribute("productos", productoService.obtenerTodosProductos());
            System.out.println("✅ Formulario de creación de lote cargado exitosamente");
            return "lotes/form";
        } catch (Exception e) {
            System.err.println("❌ Error al cargar formulario de nuevo lote: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/lotes";
        }
    }

    /**
     * Mostrar formulario para editar lote existente
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== CARGANDO FORMULARIO EDITAR LOTE ID: " + id + " ===");
            Lote lote = loteService.obtenerLotePorId(id).orElse(null);
            
            if (lote == null) {
                System.out.println("❌ Lote no encontrado");
                redirectAttributes.addFlashAttribute("error", "Lote no encontrado");
                return "redirect:/lotes";
            }

            model.addAttribute("lote", lote);
            model.addAttribute("productos", productoService.obtenerTodosProductos());
            System.out.println("✅ Formulario de edición cargado exitosamente");
            return "lotes/form";
        } catch (Exception e) {
            System.err.println("❌ Error al cargar formulario de editar lote: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cargar el lote: " + e.getMessage());
            return "redirect:/lotes";
        }
    }

    /**
     * Guardar lote (nuevo o editado)
     */
    @PostMapping("/guardar")
    public String guardarLote(@ModelAttribute Lote lote, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== GUARDANDO LOTE ===");
            System.out.println("ID: " + (lote.getId() != null ? lote.getId() : "nuevo"));
            System.out.println("Número de lote: " + lote.getNumeroLote());
            System.out.println("Producto ID: " + (lote.getProducto() != null ? lote.getProducto().getId() : "null"));

            // Validaciones básicas
            if (lote.getNumeroLote() == null || lote.getNumeroLote().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El número de lote es requerido");
                return "redirect:/lotes/nuevo";
            }

            if (lote.getProducto() == null || lote.getProducto().getId() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un producto");
                return "redirect:/lotes/nuevo";
            }

            // Si no se especifica activo, establecer como true
            if (lote.getActivo() == null) {
                lote.setActivo(true);
            }

            // Guardar lote
            Lote loteGuardado = loteService.guardarLote(lote);

            if (loteGuardado != null) {
                String mensaje = lote.getId() != null ? 
                    "Lote actualizado correctamente" : 
                    "Lote registrado correctamente";
                redirectAttributes.addFlashAttribute("success", mensaje);
                System.out.println("✅ " + mensaje + " - ID: " + loteGuardado.getId());
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo guardar el lote");
                System.out.println("❌ Error: No se pudo guardar el lote");
            }

            return "redirect:/lotes";
        } catch (Exception e) {
            System.err.println("❌ Error al guardar lote: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al guardar el lote: " + e.getMessage());
            return "redirect:/lotes";
        }
    }

    /**
     * Eliminar lote (desactivar)
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarLote(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== ELIMINANDO LOTE ID: " + id + " ===");
            
            // El servicio eliminarLote retorna void
            loteService.eliminarLote(id);

            redirectAttributes.addFlashAttribute("success", "Lote eliminado correctamente");
            System.out.println("✅ Lote eliminado exitosamente");

        } catch (Exception e) {
            System.err.println("❌ Error al eliminar lote: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el lote: " + e.getMessage());
        }

        return "redirect:/lotes";
    }

    /**
     * Ver detalle de un lote específico
     */
    @GetMapping("/detalle/{id}")
    public String verDetalleLote(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== CARGANDO DETALLE DE LOTE ID: " + id + " ===");
            Lote lote = loteService.obtenerLotePorId(id).orElse(null);

            if (lote == null) {
                redirectAttributes.addFlashAttribute("error", "Lote no encontrado");
                return "redirect:/lotes";
            }

            model.addAttribute("lote", lote);
            System.out.println("✅ Detalle de lote cargado exitosamente");
            return "lotes/detalle";
        } catch (Exception e) {
            System.err.println("❌ Error al cargar detalle de lote: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cargar el detalle: " + e.getMessage());
            return "redirect:/lotes";
        }
    }
}