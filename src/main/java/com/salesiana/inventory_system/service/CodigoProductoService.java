package com.salesiana.inventory_system.service;

import com.salesiana.inventory_system.entity.Producto;
import com.salesiana.inventory_system.entity.UbicacionAlmacen;
import com.salesiana.inventory_system.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodigoProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public String generarCodigoUnico(Producto producto, UbicacionAlmacen ubicacion) {
        // Obtener la ruta completa de la ubicación
        String rutaUbicacion = obtenerRutaUbicacion(ubicacion);
        
        // Obtener las primeras 3 letras de la categoría
        String categoriaCode = producto.getCategoria().getNombre().replaceAll("\\s+", "")
                .substring(0, Math.min(3, producto.getCategoria().getNombre().replaceAll("\\s+", "").length())).toUpperCase();
        
        // Obtener las primeras 3 letras del nombre del producto
        String nombreCode = producto.getNombre().replaceAll("\\s+", "")
                .substring(0, Math.min(3, producto.getNombre().replaceAll("\\s+", "").length())).toUpperCase();
        
        // Formato: RUTA_UBI-CAT-NOM-SEQ
        String codigoBase = rutaUbicacion + "-" + categoriaCode + "-" + nombreCode;
        
        // Buscar secuencia
        int secuencia = 1;
        while (productoRepository.findByCodigo(codigoBase + "-" + String.format("%03d", secuencia)).isPresent()) {
            secuencia++;
        }
        
        return codigoBase + "-" + String.format("%03d", secuencia);
    }
    
    private String obtenerRutaUbicacion(UbicacionAlmacen ubicacion) {
        if (ubicacion.getUbicacionPadre() == null) {
            return ubicacion.getCodigo();
        } else {
            return obtenerRutaUbicacion(ubicacion.getUbicacionPadre()) + "_" + ubicacion.getCodigo();
        }
    }
}