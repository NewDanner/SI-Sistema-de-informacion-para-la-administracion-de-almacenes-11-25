package com.salesiana.inventory_system.controller;

import com.salesiana.inventory_system.entity.Backup;
import com.salesiana.inventory_system.service.BackupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/backups")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class BackupController {
    
    @Autowired
    private BackupService backupService;
    
    /**
     * Lista todos los backups
     */
    @GetMapping
    public String listar(Model model) {
        try {
            List<Backup> backups = backupService.listarTodos();
            BackupService.BackupStats stats = backupService.obtenerEstadisticas();
            
            model.addAttribute("backups", backups);
            model.addAttribute("stats", stats);
            model.addAttribute("titulo", "Gestión de Backups");
            
            return "backup/lista";
        } catch (Exception e) {
            log.error("Error al listar backups: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al cargar la lista de backups");
            return "error";
        }
    }
    
    /**
     * Crear backup manual
     */
    @PostMapping("/crear")
    public String crearBackupManual(RedirectAttributes redirectAttributes) {
        try {
            Backup backup = backupService.crearBackupManual();
            
            if (backup.getEstado() == Backup.EstadoBackup.EXITOSO) {
                redirectAttributes.addFlashAttribute("success", 
                    "✅ Backup creado exitosamente: " + backup.getNombreArchivo());
            } else {
                redirectAttributes.addFlashAttribute("error", 
                    "❌ Error al crear backup: " + backup.getMensajeError());
            }
            
        } catch (Exception e) {
            log.error("Error al crear backup manual: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", 
                "❌ Error inesperado al crear backup: " + e.getMessage());
        }
        
        return "redirect:/backups";
    }
    
    /**
     * Descargar un backup
     */
    @GetMapping("/descargar/{id}")
    public ResponseEntity<Resource> descargar(@PathVariable Integer id) {
        try {
            Optional<Backup> backupOpt = backupService.obtenerPorId(id);
            
            if (backupOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Backup backup = backupOpt.get();
            File archivo = new File(backup.getRutaArchivo());
            
            if (!archivo.exists()) {
                log.error("Archivo de backup no encontrado: {}", backup.getRutaArchivo());
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(archivo);
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + backup.getNombreArchivo() + "\"")
                .body(resource);
                
        } catch (Exception e) {
            log.error("Error al descargar backup: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Eliminar un backup
     */
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            boolean eliminado = backupService.eliminar(id);
            
            if (eliminado) {
                redirectAttributes.addFlashAttribute("success", "✅ Backup eliminado correctamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "❌ No se pudo eliminar el backup");
            }
            
        } catch (Exception e) {
            log.error("Error al eliminar backup: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "❌ Error al eliminar backup: " + e.getMessage());
        }
        
        return "redirect:/backups";
    }
}