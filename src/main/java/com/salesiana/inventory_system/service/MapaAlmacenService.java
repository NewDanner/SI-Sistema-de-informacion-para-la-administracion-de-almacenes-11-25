package com.salesiana.inventory_system.service;

import com.salesiana.inventory_system.entity.Producto;
import com.salesiana.inventory_system.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MapaAlmacenService {

    @Autowired
    private ProductoRepository productoRepository;

    public Map<Integer, List<Object>> obtenerProductosPorUbicacion() {
        List<Producto> productos = productoRepository.findByActivoTrue();
        
        return productos.stream()
                .collect(Collectors.groupingBy(
                    p -> p.getUbicacionPredeterminada() != null ? p.getUbicacionPredeterminada().getId() : 0,
                    Collectors.mapping(p -> (Object) p, Collectors.toList())
                ));
    }
}