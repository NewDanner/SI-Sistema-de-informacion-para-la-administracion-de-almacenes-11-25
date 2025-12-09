package com.salesiana.inventory_system.service;

import com.salesiana.inventory_system.entity.Producto;
import com.salesiana.inventory_system.entity.UbicacionAlmacen;
import com.salesiana.inventory_system.repository.ProductoRepository;
import com.salesiana.inventory_system.repository.UbicacionAlmacenRepository;
import com.salesiana.inventory_system.service.CodigoProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private UbicacionAlmacenRepository ubicacionAlmacenRepository;
    
    @Autowired
    private CodigoProductoService codigoProductoService;

    public List<Producto> obtenerTodosProductos() {
        try {
            List<Producto> productos = productoRepository.findByActivoTrue();
            System.out.println("Productos obtenidos: " + productos.size());
            return productos;
        } catch (Exception e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public Optional<Producto> obtenerProductoPorId(Integer id) {
        try {
            return productoRepository.findById(id);
        } catch (Exception e) {
            System.err.println("Error al obtener producto por ID: " + e.getMessage());
            throw e;
        }
    }

    public Optional<Producto> obtenerProductoPorCodigo(String codigo) {
        try {
            return productoRepository.findByCodigo(codigo);
        } catch (Exception e) {
            System.err.println("Error al obtener producto por código: " + e.getMessage());
            throw e;
        }
    }

    public List<Producto> buscarProductos(String criterio) {
        try {
            if (criterio == null || criterio.trim().isEmpty()) {
                return productoRepository.findByActivoTrue();
            }
            return productoRepository.findByNombreContainingIgnoreCase(criterio.trim());
        } catch (Exception e) {
            System.err.println("Error en búsqueda de productos: " + e.getMessage());
            throw e;
        }
    }

    public List<Producto> obtenerProductosStockBajo() {
        try {
            return productoRepository.findProductosStockBajo();
        } catch (Exception e) {
            System.err.println("Error al obtener productos con stock bajo: " + e.getMessage());
            throw e;
        }
    }

    public List<Producto> obtenerProductosAgotados() {
        try {
            return productoRepository.findProductosAgotados();
        } catch (Exception e) {
            System.err.println("Error al obtener productos agotados: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public Producto guardarProducto(Producto producto) {
        try {
            System.out.println("Guardando producto: " + producto.getNombre());
            
            // Si es un nuevo producto y no tiene código, generar uno
            if (producto.getId() == null && (producto.getCodigo() == null || producto.getCodigo().trim().isEmpty())) {
                if (producto.getUbicacionPredeterminada() != null) {
                    producto.setCodigo(codigoProductoService.generarCodigoUnico(producto, producto.getUbicacionPredeterminada()));
                    System.out.println("📝 Código generado: " + producto.getCodigo());
                } else {
                    // Asignar ubicación por defecto según categoría si no tiene
                    if (producto.getCategoria() != null) {
                        UbicacionAlmacen ubicacionPorDefecto = asignarUbicacionPorCategoria(producto.getCategoria());
                        producto.setUbicacionPredeterminada(ubicacionPorDefecto);
                        producto.setCodigo(codigoProductoService.generarCodigoUnico(producto, ubicacionPorDefecto));
                        System.out.println("📍 Ubicación asignada y código generado: " + producto.getCodigo());
                    } else {
                        // Código por defecto si no hay ubicación ni categoría
                        producto.setCodigo("PROD-" + System.currentTimeMillis());
                        System.out.println("⚠️ Código por defecto generado: " + producto.getCodigo());
                    }
                }
            }
            
            return productoRepository.save(producto);
        } catch (Exception e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void eliminarProducto(Integer id) {
        try {
            productoRepository.findById(id).ifPresent(producto -> {
                producto.setActivo(false);
                productoRepository.save(producto);
                System.out.println("Producto eliminado (desactivado): " + id);
            });
        } catch (Exception e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            throw e;
        }
    }

    public Long contarProductosActivos() {
        try {
            return productoRepository.countProductosActivos();
        } catch (Exception e) {
            System.err.println("Error al contar productos activos: " + e.getMessage());
            return 0L;
        }
    }
    
    // Nuevo método para asignar ubicación por categoría
    private UbicacionAlmacen asignarUbicacionPorCategoria(com.salesiana.inventory_system.entity.Categoria categoria) {
        // Lógica para asignar ubicación según categoría
        try {
            // Buscar por código de ubicación según la categoría
            if (categoria.getNombre().toLowerCase().contains("medicamento") || 
                categoria.getNombre().toLowerCase().contains("analgésico") || 
                categoria.getNombre().toLowerCase().contains("antibiótico")) {
                return ubicacionAlmacenRepository.findByCodigo("ZONA-A-E1-R1")
                        .orElseGet(() -> ubicacionAlmacenRepository.findUbicacionesPrincipales().get(0));
            } else if (categoria.getNombre().toLowerCase().contains("material") || 
                       categoria.getNombre().toLowerCase().contains("curación")) {
                return ubicacionAlmacenRepository.findByCodigo("ZONA-C-E1")
                        .orElseGet(() -> ubicacionAlmacenRepository.findUbicacionesPrincipales().get(1));
            } else if (categoria.getNombre().toLowerCase().contains("vitamina") || 
                       categoria.getNombre().toLowerCase().contains("suplemento")) {
                return ubicacionAlmacenRepository.findByCodigo("ZONA-B-E1")
                        .orElseGet(() -> ubicacionAlmacenRepository.findUbicacionesPrincipales().get(2));
            } else {
                // Por defecto, asignar a una zona general
                return ubicacionAlmacenRepository.findByCodigo("ZONA-B-E1")
                        .orElseGet(() -> ubicacionAlmacenRepository.findUbicacionesPrincipales().get(0));
            }
        } catch (Exception e) {
            System.err.println("Error al asignar ubicación por categoría: " + e.getMessage());
            // Si hay error, devolver la primera ubicación disponible
            return ubicacionAlmacenRepository.findUbicacionesPrincipales().get(0);
        }
    }
}