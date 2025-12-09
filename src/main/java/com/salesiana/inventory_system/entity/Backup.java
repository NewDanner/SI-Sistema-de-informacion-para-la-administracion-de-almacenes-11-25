package com.salesiana.inventory_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "backups")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Backup {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true)
    private String nombreArchivo;
    
    @Column(nullable = false)
    private String rutaArchivo;
    
    @Column(nullable = false)
    private Long tamanoBytes;
    
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoBackup tipoBackup;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoBackup estado;
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(length = 1000)
    private String mensajeError;
    
    public enum TipoBackup {
        MANUAL,
        AUTOMATICO
    }
    
    public enum EstadoBackup {
        EXITOSO,
        FALLIDO,
        EN_PROCESO
    }
    
    // Método para obtener tamaño legible
    public String getTamanoLegible() {
        if (tamanoBytes == null) return "0 KB";
        
        double kb = tamanoBytes / 1024.0;
        double mb = kb / 1024.0;
        double gb = mb / 1024.0;
        
        if (gb >= 1) {
            return String.format("%.2f GB", gb);
        } else if (mb >= 1) {
            return String.format("%.2f MB", mb);
        } else {
            return String.format("%.2f KB", kb);
        }
    }
}