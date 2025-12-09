package com.salesiana.inventory_system.config;

import com.salesiana.inventory_system.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolService rolService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== INICIALIZACIÓN DE DATOS ===");
        
        // Inicializar roles básicos si no existen
        rolService.inicializarRolesBasicos();
        System.out.println("✅ Roles inicializados");
        
        // ❌ ELIMINADO: Ya NO se crea usuario administrador automáticamente
        // Los usuarios deben crearse manualmente vía MySQL o mediante /secure-register
        
        System.out.println("✅ Datos inicializados correctamente");
        System.out.println("ℹ️  IMPORTANTE: Crea usuarios mediante MySQL o /secure-register");
    }
}