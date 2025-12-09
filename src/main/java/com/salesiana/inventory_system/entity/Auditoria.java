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
@Table(name = "auditoria")
@Data
public class Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tabla_afectada", nullable = false, length = 50)
    private String tablaAfectada;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Operacion operacion;

    @Column(name = "registro_id")
    private Integer registroId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "datos_anteriores", columnDefinition = "JSON")
    private String datosAnteriores;

    @Column(name = "datos_nuevos", columnDefinition = "JSON")
    private String datosNuevos;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "fecha_operacion")
    private LocalDateTime fechaOperacion;

    public enum Operacion {
        INSERT, UPDATE, DELETE
    }

    @PrePersist
    protected void onCreate() {
        fechaOperacion = LocalDateTime.now();
    }
}