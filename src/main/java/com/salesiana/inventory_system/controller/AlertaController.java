/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesiana.inventory_system.controller;

/**
 *
 * @author Andrei
 */

import com.salesiana.inventory_system.service.AlertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/alertas")
public class AlertaController {
    
    @Autowired
    private AlertaService alertaService;
    
    @GetMapping
    public String listarAlertas(Model model) {
        model.addAttribute("alertas", alertaService.obtenerAlertasNoLeidas());
        model.addAttribute("totalAlertasNoLeidas", alertaService.obtenerAlertasNoLeidas().size());
        return "alertas/lista";
    }
    
    @GetMapping("/marcar-leida/{id}")
    public String marcarAlertaComoLeida(@PathVariable Integer id) {
        alertaService.marcarAlertaComoLeida(id);
        return "redirect:/alertas";
    }
}