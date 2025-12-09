package com.salesiana.inventory_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "control_calidad")
@Data
public class ControlCalidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;
    
    @Column(name = "estado_calidad", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoCalidad estadoCalidad = EstadoCalidad.PENDIENTE;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_inspector_id")
    private Usuario usuarioInspector;
    
    @Column(name = "fecha_inspeccion")
    private LocalDateTime fechaInspeccion;
    
    @Column(name = "fecha_liberacion")
    private LocalDateTime fechaLiberacion;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;
    
    @Column(name = "cumple_especificaciones")
    private Boolean cumpleEspecificaciones;
    
    @Column(name = "temperatura_recepcion", precision = 5, scale = 2)
    private BigDecimal temperaturaRecepcion;
    
    @Column(name = "lote_proveedor", length = 100)
    private String loteProveedor;
    
    @Column(name = "certificado_calidad", length = 255)
    private String certificadoCalidad;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}