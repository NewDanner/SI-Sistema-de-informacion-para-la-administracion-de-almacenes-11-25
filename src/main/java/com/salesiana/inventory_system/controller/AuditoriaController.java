package com.salesiana.inventory_system.controller;

import com.salesiana.inventory_system.service.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/auditoria")
public class AuditoriaController {
    
    @Autowired
    private AuditoriaService auditoriaService;
    
    /**
     * Muestra todas las auditorías
     * @param model
     * @return 
     */
    @GetMapping
    public String listarAuditorias(Model model) {
        try {
            model.addAttribute("auditorias", auditoriaService.obtenerAuditoriasRecientes());
            model.addAttribute("totalAuditorias", auditoriaService.contarAuditoriasHoy());
            return "auditoria/lista";
        } catch (Exception e) {
            System.err.println("Error al listar auditorías: " + e.getMessage());
            model.addAttribute("error", "Error al cargar auditorías");
            return "auditoria/lista";
        }
    }
    
    /**
     * Filtra auditorías por tabla
     * @param tabla
     * @param model
     * @return 
     */
    @GetMapping("/tabla/{tabla}")
    public String filtrarPorTabla(@PathVariable String tabla, Model model) {
        try {
            model.addAttribute("auditorias", auditoriaService.obtenerAuditoriasPorTabla(tabla));
            model.addAttribute("tablaFiltrada", tabla);
            return "auditoria/lista";
        } catch (Exception e) {
            System.err.println("Error al filtrar auditorías: " + e.getMessage());
            model.addAttribute("error", "Error al filtrar auditorías");
            return "auditoria/lista";
        }
    }
    
    /**
     * Filtra auditorías por rango de fechas
     * @param fechaInicio
     * @param fechaFin
     * @param model
     * @return 
     */
    @GetMapping("/buscar")
    public String buscarPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            Model model) {
        try {
            model.addAttribute("auditorias", 
                auditoriaService.obtenerAuditoriasPorRangoFechas(fechaInicio, fechaFin));
            return "auditoria/lista";
        } catch (Exception e) {
            System.err.println("Error en búsqueda de auditorías: " + e.getMessage());
            model.addAttribute("error", "Error en la búsqueda");
            return "auditoria/lista";
        }
    }
}