package com.salesiana.inventory_system.entity;

public enum EstadoCalidad {
    PENDIENTE("Pendiente de Inspección"),
    EN_REVISION("En Revisión"),
    LIBERADO("Liberado para Venta"),
    RECHAZADO("Rechazado"),
    EN_CUARENTENA("En Cuarentena");
    
    private final String descripcion;
    
    EstadoCalidad(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}