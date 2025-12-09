package com.salesiana.inventory_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ubicaciones_almacen")
@Data
public class UbicacionAlmacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoUbicacion tipo;

    @Column(name = "capacidad_maxima")
    private Integer capacidadMaxima;

    @Column(name = "capacidad_actual")
    private Integer capacidadActual = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_padre_id")
    private UbicacionAlmacen ubicacionPadre;

    @OneToMany(mappedBy = "ubicacionPadre", cascade = CascadeType.ALL)
    private List<UbicacionAlmacen> ubicacionesHijas;

    @Column(nullable = false)
    private Boolean activa = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public enum TipoUbicacion {
        ZONA, ESTANTERIA, RACK, PASILLO
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    // Método auxiliar para obtener ruta completa
    public String getRutaCompleta() {
        if (ubicacionPadre != null) {
            return ubicacionPadre.getRutaCompleta() + " > " + nombre;
        }
        return nombre;
    }

    // Verificar disponibilidad
    public boolean tieneCapacidadDisponible(int cantidad) {
        if (capacidadMaxima == null) return true;
        return (capacidadActual + cantidad) <= capacidadMaxima;
    }

    // CORRECCIÓN CRÍTICA: toString() seguro sin recursividad
    @Override
    public String toString() {
        return "UbicacionAlmacen{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tipo=" + tipo +
                ", capacidadMaxima=" + capacidadMaxima +
                ", capacidadActual=" + capacidadActual +
                ", activa=" + activa +
                '}';
    }
}