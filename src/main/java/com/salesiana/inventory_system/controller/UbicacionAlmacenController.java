package com.salesiana.inventory_system.controller;

import com.salesiana.inventory_system.entity.UbicacionAlmacen;
import com.salesiana.inventory_system.service.UbicacionAlmacenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Controller
@RequestMapping("/ubicaciones")
public class UbicacionAlmacenController {
    
    @Autowired
    private UbicacionAlmacenService ubicacionService;
    
    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("ubicaciones", ubicacionService.obtenerTodasUbicaciones());
            model.addAttribute("ubicacionesPrincipales", ubicacionService.obtenerUbicacionesPrincipales());
        } catch (Exception e) {
            System.err.println("❌ Error al listar ubicaciones: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar ubicaciones: " + e.getMessage());
            model.addAttribute("ubicaciones", Collections.emptyList());
            model.addAttribute("ubicacionesPrincipales", Collections.emptyList());
        }
        return "ubicaciones/lista";
    }
    
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        try {
            model.addAttribute("ubicacion", new UbicacionAlmacen());
            model.addAttribute("ubicacionesPadre", ubicacionService.obtenerTodasUbicaciones());
            model.addAttribute("tiposUbicacion", UbicacionAlmacen.TipoUbicacion.values());
        } catch (Exception e) {
            System.err.println("❌ Error al cargar formulario: " + e.getMessage());
            model.addAttribute("error", "Error al cargar formulario");
        }
        return "ubicaciones/form";
    }
    
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute UbicacionAlmacen ubicacion, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== GUARDANDO UBICACIÓN ===");
            System.out.println("Código: " + ubicacion.getCodigo());
            System.out.println("Nombre: " + ubicacion.getNombre());
            System.out.println("Tipo: " + ubicacion.getTipo());
            
            ubicacionService.guardar(ubicacion);
            redirectAttributes.addFlashAttribute("success", "Ubicación guardada correctamente");
            return "redirect:/ubicaciones";
        } catch (Exception e) {
            System.err.println("❌ Error al guardar ubicación: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al guardar ubicación: " + e.getMessage());
            return "redirect:/ubicaciones/nuevo";
        }
    }
    
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        try {
            ubicacionService.obtenerPorId(id).ifPresent(ubicacion -> {
                model.addAttribute("ubicacion", ubicacion);
                model.addAttribute("ubicacionesPadre", ubicacionService.obtenerTodasUbicaciones());
                model.addAttribute("tiposUbicacion", UbicacionAlmacen.TipoUbicacion.values());
            });
        } catch (Exception e) {
            System.err.println("❌ Error al editar: " + e.getMessage());
            model.addAttribute("error", "Error al cargar ubicación");
        }
        return "ubicaciones/form";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            ubicacionService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Ubicación eliminada correctamente");
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al eliminar ubicación");
        }
        return "redirect:/ubicaciones";
    }
}