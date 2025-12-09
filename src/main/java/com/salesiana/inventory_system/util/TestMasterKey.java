package com.salesiana.inventory_system.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestMasterKey {
    
    public static void main(String[] args) {
        // Verificar que la clave maestra es correcta
        String masterKey = "ClaveMaestra2024!";
        System.out.println("Clave maestra configurada: " + masterKey);
        System.out.println("Longitud: " + masterKey.length());
        
        // Probar con diferentes variaciones
        String[] testKeys = {
            "ClaveMaestra2024!",
            "clavemaestra2024!",
            "ClaveMaestra2024",
            "ClaveMaestra2024! ",
            " ClaveMaestra2024!",
            "ClaveMaestra2024!"
        };
        
        for (String testKey : testKeys) {
            boolean matches = testKey.equals(masterKey);
            System.out.println("'" + testKey + "' -> " + (matches ? "CORRECTO" : "INCORRECTO"));
        }
    }
}
