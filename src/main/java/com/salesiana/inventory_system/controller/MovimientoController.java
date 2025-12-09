package com.salesiana.inventory_system.controller;

import com.salesiana.inventory_system.entity.Movimiento;
import com.salesiana.inventory_system.service.MovimientoService;
import com.salesiana.inventory_system.service.ProductoService;
import com.salesiana.inventory_system.service.TipoMovimientoService;
import com.salesiana.inventory_system.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/movimientos")
public class MovimientoController {
    
    @Autowired
    private MovimientoService movimientoService;
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private TipoMovimientoService tipoMovimientoService;
    
    @Autowired
    private ProveedorService proveedorService;
    
    @GetMapping
    public String listarMovimientos(Model model) {
        try {
            // 🔧 CORRECCIÓN: Siempre inicializar las listas
            List<Movimiento> movimientos = movimientoService.obtenerTodosMovimientos();
            
            model.addAttribute("movimientos", movimientos != null ? movimientos : Collections.emptyList());
            model.addAttribute("productos", productoService.obtenerTodosProductos());
            
            System.out.println("✅ Movimientos cargados: " + (movimientos != null ? movimientos.size() : 0));
            
        } catch (Exception e) {
            System.err.println("❌ Error al listar movimientos: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar movimientos: " + e.getMessage());
            model.addAttribute("movimientos", Collections.emptyList());
            model.addAttribute("productos", Collections.emptyList());
        }
        return "movimientos/lista";
    }
    
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(@RequestParam(required = false) Integer productoId, Model model) {
        try {
            // Crear nuevo movimiento
            Movimiento movimiento = new Movimiento();
            
            // Si viene productoId, pre-seleccionarlo
            if (productoId != null) {
                productoService.obtenerProductoPorId(productoId).ifPresent(movimiento::setProducto);
            }
            
            model.addAttribute("movimiento", movimiento);
            model.addAttribute("productos", productoService.obtenerTodosProductos());
            model.addAttribute("tiposMovimiento", tipoMovimientoService.obtenerTodosTiposMovimiento());
            model.addAttribute("proveedores", proveedorService.obtenerTodosProveedores());
            
            System.out.println("✅ Formulario de movimiento cargado");
            
        } catch (Exception e) {
            System.err.println("❌ Error al cargar formulario de movimiento: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
        }
        
        return "movimientos/form";
    }
    
    @PostMapping("/guardar")
    public String guardarMovimiento(@ModelAttribute Movimiento movimiento, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== GUARDANDO MOVIMIENTO ===");
            System.out.println("Tipo Movimiento ID: " + (movimiento.getTipoMovimiento() != null ? movimiento.getTipoMovimiento().getId() : "null"));
            System.out.println("Producto ID: " + (movimiento.getProducto() != null ? movimiento.getProducto().getId() : "null"));
            System.out.println("Cantidad: " + movimiento.getCantidad());
            
            // Validar datos básicos
            if (movimiento.getProducto() == null || movimiento.getProducto().getId() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un producto");
                return "redirect:/movimientos/nuevo";
            }
            
            if (movimiento.getTipoMovimiento() == null || movimiento.getTipoMovimiento().getId() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un tipo de movimiento");
                return "redirect:/movimientos/nuevo";
            }
            
            if (movimiento.getCantidad() == null || movimiento.getCantidad() <= 0) {
                redirectAttributes.addFlashAttribute("error", "La cantidad debe ser mayor a 0");
                return "redirect:/movimientos/nuevo";
            }
            
            // Registrar movimiento (el service asignará el usuario)
            movimientoService.registrarMovimiento(movimiento);
            
            System.out.println("✅ Movimiento guardado exitosamente");
            redirectAttributes.addFlashAttribute("success", "Movimiento registrado exitosamente");
            return "redirect:/movimientos";
            
        } catch (Exception e) {
            System.err.println("❌ Error al guardar movimiento: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al registrar movimiento: " + e.getMessage());
            return "redirect:/movimientos/nuevo";
        }
    }
    
    @GetMapping("/buscar")
    public String buscarMovimientos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer productoId,
            Model model) {
        
        try {
            LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
            LocalDateTime fin = fechaFin != null ? fechaFin.atTime(23, 59, 59) : null;
            
            List<Movimiento> movimientos;
            if (inicio != null && fin != null) {
                movimientos = movimientoService.obtenerMovimientosPorRangoFechas(inicio, fin);
                System.out.println("🔍 Búsqueda por fechas: " + movimientos.size() + " resultados");
            } else if (productoId != null) {
                movimientos = movimientoService.obtenerMovimientosPorProducto(productoId);
                System.out.println("🔍 Búsqueda por producto: " + movimientos.size() + " resultados");
            } else {
                movimientos = movimientoService.obtenerTodosMovimientos();
                System.out.println("🔍 Todos los movimientos: " + movimientos.size());
            }
            
            model.addAttribute("movimientos", movimientos != null ? movimientos : Collections.emptyList());
            model.addAttribute("productos", productoService.obtenerTodosProductos());
            
        } catch (Exception e) {
            System.err.println("❌ Error en búsqueda de movimientos: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error en la búsqueda: " + e.getMessage());
            model.addAttribute("movimientos", Collections.emptyList());
            model.addAttribute("productos", Collections.emptyList());
        }
        
        return "movimientos/lista";
    }
}