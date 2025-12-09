package com.salesiana.inventory_system.controller;

import com.salesiana.inventory_system.entity.UbicacionAlmacen;
import com.salesiana.inventory_system.service.MapaAlmacenService;
import com.salesiana.inventory_system.service.UbicacionAlmacenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mapa")
public class MapaAlmacenController {

    @Autowired
    private MapaAlmacenService mapaAlmacenService;
    
    @Autowired
    private UbicacionAlmacenService ubicacionService;

    @GetMapping
    public String mostrarMapaAlmacen(Model model) {
        // Obtener todas las ubicaciones principales y sus hijos
        List<UbicacionAlmacen> ubicacionesPrincipales = ubicacionService.obtenerUbicacionesPrincipales();
        model.addAttribute("ubicaciones", ubicacionesPrincipales);
        
        // Obtener productos por ubicación
        Map<Integer, List<Object>> productosPorUbicacion = mapaAlmacenService.obtenerProductosPorUbicacion();
        model.addAttribute("productosPorUbicacion", productosPorUbicacion);
        
        return "mapa/almacen";
    }
}