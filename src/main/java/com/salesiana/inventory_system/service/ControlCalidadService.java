package com.salesiana.inventory_system.service;

import com.salesiana.inventory_system.entity.*;
import com.salesiana.inventory_system.repository.ControlCalidadRepository;
import com.salesiana.inventory_system.repository.LoteRepository;
import com.salesiana.inventory_system.repository.UsuarioRepository;
import com.salesiana.inventory_system.repository.UbicacionAlmacenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ControlCalidadService {

    @Autowired
    private ControlCalidadRepository controlCalidadRepository;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UbicacionAlmacenRepository ubicacionAlmacenRepository;

    @Autowired
    private MovimientoService movimientoService;

    @Autowired
    private ProveedorService proveedorService;

    public List<ControlCalidad> obtenerPendientesDeInspeccion() {
        return controlCalidadRepository.findPendientesDeInspeccion();
    }

    public List<ControlCalidad> obtenerEnCuarentena() {
        return controlCalidadRepository.findEnCuarentena();
    }

    public List<ControlCalidad> obtenerPorEstado(EstadoCalidad estado) {
        return controlCalidadRepository.findByEstadoCalidadOrderByFechaCreacionDesc(estado);
    }

    public Optional<ControlCalidad> obtenerPorLote(Integer loteId) {
        return controlCalidadRepository.findById(loteId);
    }

    public ControlCalidad iniciarInspeccion(Integer controlCalidadId) {
        return controlCalidadRepository.findById(controlCalidadId).map(control -> {
            control.setEstadoCalidad(EstadoCalidad.EN_REVISION);
            control.setFechaInspeccion(LocalDateTime.now());
            control.setUsuarioInspector(obtenerUsuarioActual());
            return controlCalidadRepository.save(control);
        }).orElseThrow(() -> new RuntimeException("Control de calidad no encontrado"));
    }

    public ControlCalidad liberarLote(Integer controlCalidadId, String observaciones, UbicacionAlmacen ubicacionDestino) {
        return controlCalidadRepository.findById(controlCalidadId).map(control -> {
            control.setEstadoCalidad(EstadoCalidad.LIBERADO);
            control.setFechaLiberacion(LocalDateTime.now());
            control.setCumpleEspecificaciones(true);
            control.setObservaciones(observaciones);

            // Actualizar el lote
            Lote lote = control.getLote();
            lote.setEstadoCalidad(EstadoCalidad.LIBERADO);
            lote.setFechaLiberacion(LocalDateTime.now());

            // Mover el lote a la nueva ubicación
            if (ubicacionDestino != null) {
                lote.setUbicacion(ubicacionDestino);
            }

            loteRepository.save(lote);
            return controlCalidadRepository.save(control);
        }).orElseThrow(() -> new RuntimeException("Control de calidad no encontrado"));
    }

    public ControlCalidad rechazarLote(Integer controlCalidadId, String motivoRechazo, String accion) {
        return controlCalidadRepository.findById(controlCalidadId).map(control -> {
            control.setEstadoCalidad(EstadoCalidad.RECHAZADO);
            control.setCumpleEspecificaciones(false);
            control.setMotivoRechazo(motivoRechazo);

            // Procesar según la acción seleccionada
            Lote lote = control.getLote();
            lote.setEstadoCalidad(EstadoCalidad.RECHAZADO);

            switch (accion.trim().toLowerCase()) {
                case "devolver":
                    // Registrar devolución al proveedor
                    registrarDevolucionProveedor(lote, motivoRechazo);
                    break;
                case "merma":
                    // Registrar como merma
                    registrarMerma(lote, motivoRechazo);
                    break;
                case "descartar":
                    // Marcar lote como inactivo
                    lote.setActivo(false);
                    break;
                default:
                    throw new IllegalArgumentException("Acción no válida: " + accion);
            }

            loteRepository.save(lote);
            return controlCalidadRepository.save(control);
        }).orElseThrow(() -> new RuntimeException("Control de calidad no encontrado"));
    }

    public ControlCalidad ponerEnCuarentena(Integer controlCalidadId, String observaciones) {
        return controlCalidadRepository.findById(controlCalidadId).map(control -> {
            control.setEstadoCalidad(EstadoCalidad.EN_CUARENTENA);
            control.setObservaciones(observaciones);

            // Obtener ubicación de cuarentena (ZONA-Q-E1)
            UbicacionAlmacen zonaCuarentena = ubicacionAlmacenRepository.findByCodigo("ZONA-Q-E1")
                .orElseThrow(() -> new RuntimeException("Ubicación de cuarentena no encontrada"));

            // Mover el lote a cuarentena
            Lote lote = control.getLote();
            lote.setUbicacion(zonaCuarentena);
            lote.setEstadoCalidad(EstadoCalidad.EN_CUARENTENA);

            loteRepository.save(lote);
            return controlCalidadRepository.save(control);
        }).orElseThrow(() -> new RuntimeException("Control de calidad no encontrado"));
    }

    public ControlCalidad actualizarControl(ControlCalidad controlCalidad) {
        return controlCalidadRepository.save(controlCalidad);
    }

    public Long contarPorEstado(EstadoCalidad estado) {
        return controlCalidadRepository.countByEstadoCalidad(estado);
    }

    public Long contarTotalControles() {
        return controlCalidadRepository.count();
    }

    public List<ControlCalidad> obtenerUltimosControles(int limite) {
        return controlCalidadRepository.findAll().stream()
            .sorted((c1, c2) -> c2.getFechaCreacion().compareTo(c1.getFechaCreacion()))
            .limit(limite)
            .toList();
    }

    public List<Producto> obtenerProductosConMasProblemas(int limite) {
        // Este método necesitaría una consulta personalizada en el repositorio
        // Por ahora, devolvemos una lista vacía como placeholder
        return List.of();
    }

    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return usuarioRepository.findByUsername(auth.getName()).orElse(null);
        }
        return null;
    }

    private void registrarDevolucionProveedor(Lote lote, String motivo) {
        try {
            // Crear movimiento de devolución
            Movimiento movimiento = new Movimiento();
            movimiento.setProducto(lote.getProducto());
            movimiento.setLote(lote);

            // Buscar tipo de movimiento "devolucion"
            TipoMovimiento tipoDevolucion = new TipoMovimiento();
            tipoDevolucion.setId(4); // ID para "devolucion"
            movimiento.setTipoMovimiento(tipoDevolucion);

            movimiento.setCantidad(lote.getCantidadActual());
            movimiento.setPrecioUnitario(lote.getProducto().getPrecioCompra());
            movimiento.setMotivo("Devolución por calidad: " + motivo);
            movimiento.setDocumentoReferencia("DEV-CAL-" + lote.getId());
            movimiento.setUsuario(obtenerUsuarioActual());

            // ✅ CORRECCIÓN: Validar y guardar proveedor si es transitorio
            if (lote.getProveedor() != null) {
                if (lote.getProveedor().getId() == null) {
                    movimiento.setProveedor(proveedorService.guardarProveedor(lote.getProveedor()));
                } else {
                    movimiento.setProveedor(lote.getProveedor());
                }
            }

            movimientoService.registrarMovimiento(movimiento);
        } catch (Exception e) {
            System.err.println("Error al registrar devolución: " + e.getMessage());
        }
    }

    private void registrarMerma(Lote lote, String motivo) {
        try {
            // Crear movimiento de merma
            Movimiento movimiento = new Movimiento();
            movimiento.setProducto(lote.getProducto());
            movimiento.setLote(lote);

            // Buscar tipo de movimiento "merma"
            TipoMovimiento tipoMerma = new TipoMovimiento();
            tipoMerma.setId(5); // ID para "merma"
            movimiento.setTipoMovimiento(tipoMerma);

            movimiento.setCantidad(lote.getCantidadActual());
            movimiento.setPrecioUnitario(lote.getProducto().getPrecioCompra());
            movimiento.setMotivo("Merma por calidad: " + motivo);
            movimiento.setDocumentoReferencia("MER-CAL-" + lote.getId());
            movimiento.setUsuario(obtenerUsuarioActual());

            movimientoService.registrarMovimiento(movimiento);
        } catch (Exception e) {
            System.err.println("Error al registrar merma: " + e.getMessage());
        }
    }
}