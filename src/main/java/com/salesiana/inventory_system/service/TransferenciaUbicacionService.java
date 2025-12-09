package com.salesiana.inventory_system.service;

import com.salesiana.inventory_system.entity.*;
import com.salesiana.inventory_system.repository.*;
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
public class TransferenciaUbicacionService {

    @Autowired
    private TransferenciaUbicacionRepository transferenciaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private UbicacionAlmacenRepository ubicacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ✅ MÉTODOS BÁSICOS CORREGIDOS
    @Transactional(readOnly = true)
    public List<TransferenciaUbicacion> obtenerTodasTransferencias() {
        return transferenciaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<TransferenciaUbicacion> obtenerUltimasTransferencias() {
        return transferenciaRepository.findUltimasTransferencias();
    }

    @Transactional(readOnly = true)
    public List<TransferenciaUbicacion> obtenerPorProducto(Integer productoId) {
        return transferenciaRepository.findByProductoId(productoId);
    }

    @Transactional(readOnly = true)
    public List<TransferenciaUbicacion> obtenerPorLote(Integer loteId) {
        return transferenciaRepository.findByLoteId(loteId);
    }

    @Transactional(readOnly = true)
    public List<TransferenciaUbicacion> obtenerPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return transferenciaRepository.findByFechaTransferenciaBetween(inicio, fin);
    }

    @Transactional(readOnly = true)
    public Optional<TransferenciaUbicacion> obtenerPorId(Integer id) {
        return transferenciaRepository.findById(id);
    }

    // ✅ MÉTODO PRINCIPAL CORREGIDO - REGISTRAR TRANSFERENCIA
    @Transactional
public TransferenciaUbicacion registrarTransferencia(TransferenciaUbicacion transferencia) {
    try {
        System.out.println("=== REGISTRANDO TRANSFERENCIA DE UBICACIÓN ===");

        // 1. VALIDACIONES BÁSICAS
        if (transferencia.getProducto() == null || transferencia.getProducto().getId() == null) {
            throw new RuntimeException("Debe seleccionar un producto");
        }
        if (transferencia.getUbicacionDestino() == null || transferencia.getUbicacionDestino().getId() == null) {
            throw new RuntimeException("Debe seleccionar una ubicación destino");
        }
        if (transferencia.getCantidad() == null || transferencia.getCantidad() <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }

        // 2. CARGAR ENTIDADES COMPLETAS DESDE BD
        Producto producto = productoRepository.findById(transferencia.getProducto().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + transferencia.getProducto().getId()));
        transferencia.setProducto(producto);

        UbicacionAlmacen ubicacionDestino = ubicacionRepository.findById(transferencia.getUbicacionDestino().getId())
                .orElseThrow(() -> new RuntimeException("Ubicación destino no encontrada con ID: " + transferencia.getUbicacionDestino().getId()));
        transferencia.setUbicacionDestino(ubicacionDestino);

        // 3. CARGAR UBICACIÓN ORIGEN SI EXISTE
        if (transferencia.getUbicacionOrigen() != null && transferencia.getUbicacionOrigen().getId() != null) {
            UbicacionAlmacen ubicacionOrigen = ubicacionRepository.findById(transferencia.getUbicacionOrigen().getId())
                    .orElseThrow(() -> new RuntimeException("Ubicación origen no encontrada"));
            transferencia.setUbicacionOrigen(ubicacionOrigen);

            // Validar que origen y destino sean diferentes
            if (ubicacionOrigen.getId().equals(ubicacionDestino.getId())) {
                throw new RuntimeException("La ubicación de origen y destino no pueden ser iguales");
            }
        }

        // 4. ✅ CARGAR LOTE CON VALIDACIÓN MEJORADA
        if (transferencia.getLote() != null && transferencia.getLote().getId() != null) {
            Lote lote = loteRepository.findById(transferencia.getLote().getId())
                    .orElseThrow(() -> new RuntimeException("Lote no encontrado con ID: " + transferencia.getLote().getId()));
            
            // ✅ VERIFICAR QUE EL LOTE TIENE PRODUCTO ASOCIADO
            if (lote.getProducto() == null) {
                throw new RuntimeException("El lote seleccionado no tiene un producto asociado en la base de datos");
            }
            
            transferencia.setLote(lote);

            // ✅ VERIFICAR QUE EL LOTE PERTENECE AL PRODUCTO SELECCIONADO
            if (!lote.getProducto().getId().equals(producto.getId())) {
                throw new RuntimeException("El lote seleccionado no pertenece al producto seleccionado. Lote: " + 
                    lote.getProducto().getNombre() + ", Producto seleccionado: " + producto.getNombre());
            }
        }

        // 5. ASIGNAR USUARIO ACTUAL
        Usuario usuario = obtenerUsuarioActual();
        if (usuario == null) {
            throw new RuntimeException("No hay usuario autenticado");
        }
        transferencia.setUsuario(usuario);

        // 6. ASIGNAR FECHA
        if (transferencia.getFechaTransferencia() == null) {
            transferencia.setFechaTransferencia(LocalDateTime.now());
        }

        // 7. VERIFICAR CAPACIDAD DESTINO
        if (ubicacionDestino.getCapacidadMaxima() != null) {
            int capacidadDisponible = ubicacionDestino.getCapacidadMaxima() - ubicacionDestino.getCapacidadActual();
            if (capacidadDisponible < transferencia.getCantidad()) {
                throw new RuntimeException("Capacidad insuficiente en destino. Disponible: " + capacidadDisponible + ", Requerida: " + transferencia.getCantidad());
            }
        }

        // 8. ACTUALIZAR CAPACIDADES
        if (transferencia.getUbicacionOrigen() != null) {
            UbicacionAlmacen ubicacionOrigen = transferencia.getUbicacionOrigen();
            int nuevaCapacidadOrigen = ubicacionOrigen.getCapacidadActual() - transferencia.getCantidad();
            ubicacionOrigen.setCapacidadActual(Math.max(nuevaCapacidadOrigen, 0));
            ubicacionRepository.save(ubicacionOrigen);
        }

        // Actualizar capacidad destino
        int nuevaCapacidadDestino = ubicacionDestino.getCapacidadActual() + transferencia.getCantidad();
        ubicacionDestino.setCapacidadActual(nuevaCapacidadDestino);
        ubicacionRepository.save(ubicacionDestino);

        // 9. ACTUALIZAR UBICACIÓN DEL LOTE
        if (transferencia.getLote() != null) {
            Lote lote = transferencia.getLote();
            lote.setUbicacion(ubicacionDestino);
            loteRepository.save(lote);
        }

        // 10. GUARDAR TRANSFERENCIA
        TransferenciaUbicacion transferenciaGuardada = transferenciaRepository.save(transferencia);

        System.out.println("✅ Transferencia registrada exitosamente - ID: " + transferenciaGuardada.getId());
        return transferenciaGuardada;

    } catch (Exception e) {
        System.err.println("❌ Error al registrar transferencia: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Error al registrar transferencia: " + e.getMessage(), e);
    }
}

    @Transactional
    public void eliminarTransferencia(Integer id) {
        try {
            TransferenciaUbicacion transferencia = transferenciaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Transferencia no encontrada"));

            // Revertir capacidades
            if (transferencia.getUbicacionOrigen() != null) {
                UbicacionAlmacen ubicacionOrigen = transferencia.getUbicacionOrigen();
                ubicacionOrigen.setCapacidadActual(ubicacionOrigen.getCapacidadActual() + transferencia.getCantidad());
                ubicacionRepository.save(ubicacionOrigen);
            }

            UbicacionAlmacen ubicacionDestino = transferencia.getUbicacionDestino();
            ubicacionDestino.setCapacidadActual(ubicacionDestino.getCapacidadActual() - transferencia.getCantidad());
            ubicacionRepository.save(ubicacionDestino);

            transferenciaRepository.delete(transferencia);
            System.out.println("✅ Transferencia eliminada - ID: " + id);

        } catch (Exception e) {
            System.err.println("❌ Error al eliminar transferencia: " + e.getMessage());
            throw new RuntimeException("Error al eliminar transferencia", e);
        }
    }

    // ✅ MÉTODO AUXILIAR
    private Usuario obtenerUsuarioActual() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                String username = auth.getName();
                return usuarioRepository.findByUsername(username).orElse(null);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener usuario actual: " + e.getMessage());
        }
        return null;
    }
}