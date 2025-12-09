package com.salesiana.inventory_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "transferencias_ubicacion")
@Data
public class TransferenciaUbicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private Lote lote;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_origen_id")
    private UbicacionAlmacen ubicacionOrigen;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_destino_id", nullable = false)
    private UbicacionAlmacen ubicacionDestino;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(length = 255)
    private String motivo;
    
    @Column(name = "documento_referencia", length = 100)
    private String documentoReferencia;
    
    @Column(name = "fecha_transferencia", nullable = false)
    private LocalDateTime fechaTransferencia;
    
    @PrePersist
    protected void onCreate() {
        if (fechaTransferencia == null) {
            fechaTransferencia = LocalDateTime.now();
        }
    }
}