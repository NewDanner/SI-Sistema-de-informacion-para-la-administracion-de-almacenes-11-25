/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesiana.inventory_system.entity;

/**
 *
 * @author Andrei
 */
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
@Data
public class Alerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_alerta_id", nullable = false)
    private TipoAlerta tipoAlerta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "nivel_prioridad")
    @Enumerated(EnumType.STRING)
    private NivelPrioridad nivelPrioridad;

    @Column(nullable = false)
    private Boolean leida = false;

    @Column(name = "fecha_alerta")
    private LocalDateTime fechaAlerta;

    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;

    public enum NivelPrioridad {
        baja, media, alta, critica
    }

    @PrePersist
    protected void onCreate() {
        fechaAlerta = LocalDateTime.now();
    }
}