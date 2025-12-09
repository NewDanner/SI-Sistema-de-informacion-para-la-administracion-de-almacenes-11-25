package com.salesiana.inventory_system.service;

import com.salesiana.inventory_system.entity.Movimiento;
import com.salesiana.inventory_system.entity.Producto;
import com.salesiana.inventory_system.entity.Usuario;
import com.salesiana.inventory_system.entity.TipoMovimiento;
import com.salesiana.inventory_system.repository.MovimientoRepository;
import com.salesiana.inventory_system.repository.ProductoRepository;
import com.salesiana.inventory_system.repository.TipoMovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TipoMovimientoRepository tipoMovimientoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProveedorService proveedorService;

    @Transactional(readOnly = true)
    public List<Movimiento> obtenerTodosMovimientos() {
        try {
            List<Movimiento> movimientos = movimientoRepository.findAll();
            for (Movimiento movimiento : movimientos) {
                if (movimiento.getProducto() != null) {
                    movimiento.getProducto().getNombre();
                }
                if (movimiento.getUsuario() != null) {
                    movimiento.getUsuario().getNombreCompleto();
                }
                if (movimiento.getTipoMovimiento() != null) {
                    movimiento.getTipoMovimiento().getNombre();
                }
            }
            return movimientos;
        } catch (Exception e) {
            System.err.println("Error al obtener movimientos: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Movimiento> obtenerMovimientosPorProducto(Integer productoId) {
        try {
            List<Movimiento> movimientos = movimientoRepository.findByProductoIdOrderByFechaMovimientoDesc(productoId);
            for (Movimiento movimiento : movimientos) {
                if (movimiento.getUsuario() != null) {
                    movimiento.getUsuario().getNombreCompleto();
                }
                if (movimiento.getTipoMovimiento() != null) {
                    movimiento.getTipoMovimiento().getNombre();
                }
            }
            return movimientos;
        } catch (Exception e) {
            System.err.println("Error al obtener movimientos por producto: " + e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Movimiento> obtenerMovimientosPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        try {
            List<Movimiento> movimientos = movimientoRepository.findMovimientosPorRangoFechas(inicio, fin);
            for (Movimiento movimiento : movimientos) {
                if (movimiento.getProducto() != null) {
                    movimiento.getProducto().getNombre();
                }
                if (movimiento.getUsuario() != null) {
                    movimiento.getUsuario().getNombreCompleto();
                }
                if (movimiento.getTipoMovimiento() != null) {
                    movimiento.getTipoMovimiento().getNombre();
                }
            }
            return movimientos;
        } catch (Exception e) {
            System.err.println("Error al obtener movimientos por rango de fechas: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public Movimiento registrarMovimiento(Movimiento movimiento) {
        try {
            System.out.println("=== REGISTRANDO MOVIMIENTO ===");

            // ✅ CORRECCIÓN CRÍTICA: Cargar TipoMovimiento completo desde la BD
            if (movimiento.getTipoMovimiento() == null || movimiento.getTipoMovimiento().getId() == null) {
                throw new RuntimeException("Debe seleccionar un tipo de movimiento");
            }

            TipoMovimiento tipoMovimiento = tipoMovimientoRepository.findById(movimiento.getTipoMovimiento().getId())
                .orElseThrow(() -> new RuntimeException("Tipo de movimiento no encontrado"));
            movimiento.setTipoMovimiento(tipoMovimiento);

            System.out.println("Tipo Movimiento cargado: " + tipoMovimiento.getNombre());
            System.out.println("Afecta Stock: " + tipoMovimiento.getAfectaStock());

            // Validar que el producto existe
            Producto producto = productoRepository.findById(movimiento.getProducto().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            movimiento.setProducto(producto);

            // ✅ CORRECCIÓN: Validar y manejar proveedor correctamente
            if (movimiento.getProveedor() != null) {
                // Si el proveedor tiene ID, verificar que existe en BD
                if (movimiento.getProveedor().getId() != null && movimiento.getProveedor().getId() > 0) {
                    // Proveedor existente, no hacer nada (ya está en BD)
                    System.out.println("✅ Usando proveedor existente ID: " + movimiento.getProveedor().getId());
                } 
                // Si NO tiene ID pero tampoco tiene nombre, significa que no se seleccionó proveedor
                else if (movimiento.getProveedor().getId() == null && 
                         (movimiento.getProveedor().getNombre() == null || 
                          movimiento.getProveedor().getNombre().trim().isEmpty())) {
                    // No se seleccionó proveedor, establecer como null
                    System.out.println("⚠️ No se seleccionó proveedor, estableciendo como null");
                    movimiento.setProveedor(null);
                }
                // Si NO tiene ID pero SÍ tiene nombre, es un proveedor nuevo
                else if (movimiento.getProveedor().getId() == null && 
                         movimiento.getProveedor().getNombre() != null && 
                         !movimiento.getProveedor().getNombre().trim().isEmpty()) {
                    System.out.println("⚠️ Proveedor transitorio detectado, guardando...");
                    movimiento.setProveedor(proveedorService.guardarProveedor(movimiento.getProveedor()));
                    System.out.println("✅ Proveedor guardado con ID: " + movimiento.getProveedor().getId());
                }
            }

            // Asignar usuario actual si no está asignado
            if (movimiento.getUsuario() == null || movimiento.getUsuario().getId() == null) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    String username = auth.getName();
                    System.out.println("Usuario autenticado: " + username);

                    Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
                    movimiento.setUsuario(usuario);
                    System.out.println("Usuario asignado: " + usuario.getNombreCompleto());
                } else {
                    throw new RuntimeException("No hay usuario autenticado");
                }
            }

            // Asignar fecha si no está asignada
            if (movimiento.getFechaMovimiento() == null) {
                movimiento.setFechaMovimiento(LocalDateTime.now());
            }

            // ✅ CORRECCIÓN: Validar stock ANTES de guardar
            Integer afectaStock = tipoMovimiento.getAfectaStock();
            System.out.println("Validando stock - Afecta: " + afectaStock);

            if (afectaStock == -1) {
                // Es una salida, verificar stock
                if (producto.getStockActual() < movimiento.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente. Stock actual: " + 
                        producto.getStockActual() + ", Cantidad solicitada: " + movimiento.getCantidad());
                }
                System.out.println("✅ Stock suficiente para salida");
            }

            // Guardar el movimiento (el trigger actualizará el stock)
            Movimiento movimientoGuardado = movimientoRepository.save(movimiento);
            System.out.println("✅ Movimiento registrado exitosamente - ID: " + movimientoGuardado.getId());

            return movimientoGuardado;

        } catch (Exception e) {
            System.err.println("❌ Error al registrar movimiento: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al registrar movimiento: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Movimiento> obtenerMovimientoPorId(Integer id) {
        try {
            Optional<Movimiento> movimientoOpt = movimientoRepository.findById(id);
            if (movimientoOpt.isPresent()) {
                Movimiento movimiento = movimientoOpt.get();
                if (movimiento.getProducto() != null) movimiento.getProducto().getNombre();
                if (movimiento.getUsuario() != null) movimiento.getUsuario().getNombreCompleto();
                if (movimiento.getTipoMovimiento() != null) movimiento.getTipoMovimiento().getNombre();
            }
            return movimientoOpt;
        } catch (Exception e) {
            System.err.println("Error al obtener movimiento por ID: " + e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Movimiento> obtenerUltimosMovimientos(int limite) {
        try {
            LocalDateTime hace7Dias = LocalDateTime.now().minusDays(7);
            List<Movimiento> movimientos = movimientoRepository.findMovimientosPorRangoFechas(hace7Dias, LocalDateTime.now());

            for (Movimiento movimiento : movimientos) {
                if (movimiento.getProducto() != null) movimiento.getProducto().getNombre();
                if (movimiento.getUsuario() != null) movimiento.getUsuario().getNombreCompleto();
                if (movimiento.getTipoMovimiento() != null) movimiento.getTipoMovimiento().getNombre();
            }

            return movimientos.stream().limit(limite).toList();
        } catch (Exception e) {
            System.err.println("Error al obtener últimos movimientos: " + e.getMessage());
            throw e;
        }
    }
}