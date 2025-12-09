package com.salesiana.inventory_system.controller;

import com.salesiana.inventory_system.service.ProductoService;
import com.salesiana.inventory_system.service.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Collections;

@Controller
public class DashboardController {
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private MovimientoService movimientoService;
    
    @GetMapping("/")
    public String dashboard(Model model) {
        try {
            // Estadísticas de productos
            model.addAttribute("totalProductos", 
                productoService.contarProductosActivos());
            model.addAttribute("productosStockBajo", 
                productoService.obtenerProductosStockBajo().size());
            model.addAttribute("productosAgotados", 
                productoService.obtenerProductosAgotados().size());
            
            // Obtener últimos movimientos (últimos 10)
            var movimientos = movimientoService.obtenerUltimosMovimientos(10);
            
            model.addAttribute("ultimosMovimientos", movimientos != null ? 
                movimientos : Collections.emptyList());
                
        } catch (Exception e) {
            // En caso de error, establecer valores por defecto
            System.err.println("Error en dashboard: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("totalProductos", 0);
            model.addAttribute("productosStockBajo", 0);
            model.addAttribute("productosAgotados", 0);
            model.addAttribute("ultimosMovimientos", Collections.emptyList());
            model.addAttribute("error", "Error al cargar datos del dashboard");
        }
        return "dashboard";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}