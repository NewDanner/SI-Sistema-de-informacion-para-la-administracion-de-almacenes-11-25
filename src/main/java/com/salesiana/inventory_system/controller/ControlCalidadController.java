package com.salesiana.inventory_system.controller;

import com.salesiana.inventory_system.entity.ControlCalidad;
import com.salesiana.inventory_system.entity.EstadoCalidad;
import com.salesiana.inventory_system.entity.UbicacionAlmacen;
import com.salesiana.inventory_system.service.ControlCalidadService;
import com.salesiana.inventory_system.service.UbicacionAlmacenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/control-calidad")
public class ControlCalidadController {

    @Autowired
    private ControlCalidadService controlCalidadService;
    
    @Autowired
    private UbicacionAlmacenService ubicacionAlmacenService;

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("pendientes", controlCalidadService.obtenerPendientesDeInspeccion());
            model.addAttribute("enCuarentena", controlCalidadService.obtenerEnCuarentena());
            model.addAttribute("totalPendientes", controlCalidadService.contarPorEstado(EstadoCalidad.PENDIENTE));
            model.addAttribute("totalEnCuarentena", controlCalidadService.contarPorEstado(EstadoCalidad.EN_CUARENTENA));
            model.addAttribute("totalEnRevision", controlCalidadService.contarPorEstado(EstadoCalidad.EN_REVISION));
            
            // Obtener ubicaciones disponibles para mover productos
            model.addAttribute("ubicacionesDisponibles", ubicacionAlmacenService.obtenerUbicacionesConCapacidad());
        } catch (Exception e) {
            System.err.println("❌ Error al listar control de calidad: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar control de calidad: " + e.getMessage());
            model.addAttribute("pendientes", Collections.emptyList());
            model.addAttribute("enCuarentena", Collections.emptyList());
            model.addAttribute("totalPendientes", 0L);
            model.addAttribute("totalEnCuarentena", 0L);
            model.addAttribute("totalEnRevision", 0L);
        }
        return "control-calidad/lista";
    }
    
    @GetMapping("/cuarentena")
    public String mostrarCuarentena(Model model) {
        try {
            model.addAttribute("lotesEnCuarentena", controlCalidadService.obtenerEnCuarentena());
            model.addAttribute("totalEnCuarentena", controlCalidadService.contarPorEstado(EstadoCalidad.EN_CUARENTENA));
            model.addAttribute("totalPendientes", controlCalidadService.contarPorEstado(EstadoCalidad.PENDIENTE));
            model.addAttribute("ubicacionesDisponibles", ubicacionAlmacenService.obtenerUbicacionesConCapacidad());
        } catch (Exception e) {
            System.err.println("❌ Error al cargar vista de cuarentena: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar los datos de cuarentena");
        }
        return "control-calidad/cuarentena";
    }

    @PostMapping("/iniciar-inspeccion/{id}")
    public String iniciarInspeccion(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            controlCalidadService.iniciarInspeccion(id);
            redirectAttributes.addFlashAttribute("success", "Inspección iniciada correctamente");
        } catch (Exception e) {
            System.err.println("❌ Error al iniciar inspección: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al iniciar inspección: " + e.getMessage());
        }
        return "redirect:/control-calidad";
    }

    @PostMapping("/liberar/{id}")
    public String liberar(
            @PathVariable Integer id, 
            @RequestParam(required = false, defaultValue = "") String observaciones,
            @RequestParam(required = false) Integer ubicacionDestinoId,
            RedirectAttributes redirectAttributes) {
        try {
            // Validar que se seleccionó una ubicación de destino
            if (ubicacionDestinoId == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar una ubicación de destino para liberar el lote");
                return "redirect:/control-calidad/cuarentena";
            }
            
            // Obtener la ubicación de destino
            Optional<UbicacionAlmacen> ubicacionDestinoOpt = ubicacionAlmacenService.obtenerPorId(ubicacionDestinoId);
            if (!ubicacionDestinoOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "La ubicación de destino seleccionada no existe");
                return "redirect:/control-calidad/cuarentena";
            }
            
            // Realizar la liberación
            controlCalidadService.liberarLote(id, observaciones, ubicacionDestinoOpt.get());
            redirectAttributes.addFlashAttribute("success", "Lote liberado exitosamente y movido a la ubicación seleccionada");
        } catch (Exception e) {
            System.err.println("❌ Error al liberar lote: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al liberar lote: " + e.getMessage());
        }
        return "redirect:/control-calidad/cuarentena";
    }

    @PostMapping("/rechazar/{id}")
    public String rechazar(
            @PathVariable Integer id, 
            @RequestParam(required = false, defaultValue = "") String motivoRechazo,
            @RequestParam(required = false) String accion,
            RedirectAttributes redirectAttributes) {
        try {
            // Validar datos de rechazo
            if (motivoRechazo == null || motivoRechazo.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debe especificar el motivo del rechazo");
                return "redirect:/control-calidad/cuarentena";
            }
            
            if (accion == null || accion.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar una acción para el lote rechazado");
                return "redirect:/control-calidad/cuarentena";
            }
            
            // Realizar el rechazo
            controlCalidadService.rechazarLote(id, motivoRechazo, accion);
            redirectAttributes.addFlashAttribute("success", "Lote rechazado y procesado según la acción seleccionada: " + accion);
        } catch (Exception e) {
            System.err.println("❌ Error al rechazar lote: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al rechazar lote: " + e.getMessage());
        }
        return "redirect:/control-calidad/cuarentena";
    }

    @PostMapping("/cuarentena/{id}")
    public String cuarentena(
            @PathVariable Integer id, 
            @RequestParam(required = false, defaultValue = "") String observaciones,
            RedirectAttributes redirectAttributes) {
        try {
            controlCalidadService.ponerEnCuarentena(id, observaciones);
            redirectAttributes.addFlashAttribute("success", "Lote puesto en cuarentena");
        } catch (Exception e) {
            System.err.println("❌ Error al poner en cuarentena: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/control-calidad";
    }
    
    @GetMapping("/informe")
    public String generarInforme(Model model) {
        try {
            // Obtener estadísticas de control de calidad
            model.addAttribute("totalPendientes", controlCalidadService.contarPorEstado(EstadoCalidad.PENDIENTE));
            model.addAttribute("totalEnRevision", controlCalidadService.contarPorEstado(EstadoCalidad.EN_REVISION));
            model.addAttribute("totalEnCuarentena", controlCalidadService.contarPorEstado(EstadoCalidad.EN_CUARENTENA));
            model.addAttribute("totalLiberados", controlCalidadService.contarPorEstado(EstadoCalidad.LIBERADO));
            model.addAttribute("totalRechazados", controlCalidadService.contarPorEstado(EstadoCalidad.RECHAZADO));
            
            // Últimos controles de calidad
            model.addAttribute("ultimosControles", controlCalidadService.obtenerUltimosControles(10));
            
            // Productos con más problemas de calidad
            model.addAttribute("productosProblemas", controlCalidadService.obtenerProductosConMasProblemas(5));
            
            // Porcentaje de aceptación
            long total = controlCalidadService.contarTotalControles();
            long liberados = controlCalidadService.contarPorEstado(EstadoCalidad.LIBERADO);
            double porcentajeAceptacion = total > 0 ? (liberados * 100.0 / total) : 0;
            model.addAttribute("porcentajeAceptacion", String.format("%.2f", porcentajeAceptacion));
            
        } catch (Exception e) {
            System.err.println("❌ Error al generar informe de control de calidad: " + e.getMessage());
            model.addAttribute("error", "Error al generar informe: " + e.getMessage());
        }
        return "control-calidad/informe";
    }
}