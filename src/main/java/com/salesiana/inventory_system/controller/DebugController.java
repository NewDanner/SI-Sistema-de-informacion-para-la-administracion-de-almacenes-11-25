/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesiana.inventory_system.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {
    
    @Value("${app.registration.master-key:ClaveMaestra2024!}")
    private String masterKey;
    
    @GetMapping("/debug/master-key")
    public String debugMasterKey(@RequestParam(required = false) String input) {
        StringBuilder result = new StringBuilder();
        result.append("Clave maestra configurada: '").append(masterKey).append("'\n");
        result.append("Longitud: ").append(masterKey.length()).append("\n");
        
        if (input != null) {
            boolean matches = masterKey.equals(input);
            result.append("Input recibido: '").append(input).append("'\n");
            result.append("Longitud input: ").append(input.length()).append("\n");
            result.append("¿Coincide? ").append(matches ? "SÍ" : "NO").append("\n");
            
            // Comparación carácter por carácter
            result.append("\nComparación detallada:\n");
            int maxLength = Math.max(masterKey.length(), input.length());
            for (int i = 0; i < maxLength; i++) {
                char masterChar = i < masterKey.length() ? masterKey.charAt(i) : ' ';
                char inputChar = i < input.length() ? input.charAt(i) : ' ';
                boolean charMatches = masterChar == inputChar;
                result.append(String.format("Pos %2d: '%c' vs '%c' -> %s\n", 
                    i, masterChar, inputChar, charMatches ? "✓" : "✗"));
            }
        }
        
        return result.toString();
    }
}
