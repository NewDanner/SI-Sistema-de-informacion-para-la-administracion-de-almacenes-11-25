package com.salesiana.inventory_system.service;

import com.salesiana.inventory_system.entity.Auditoria;
import com.salesiana.inventory_system.entity.Usuario;
import com.salesiana.inventory_system.repository.AuditoriaRepository;
import com.salesiana.inventory_system.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar las operaciones de auditoría del sistema
 * Registra todas las operaciones CRUD realizadas en el sistema
 */
@Service
public class AuditoriaService {
    
    @Autowired
    private AuditoriaRepository auditoriaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    /**
     * Obtiene todas las auditorías del sistema
     * @return Lista de todas las auditorías
     */
    @Transactional(readOnly = true)
    public List<Auditoria> obtenerTodasAuditorias() {
        try {
            return auditoriaRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error al obtener todas las auditorías: " + e.getMessage());
            throw new RuntimeException("Error al obtener auditorías", e);
        }
    }
    
    /**
     * Obtiene auditorías filtradas por tabla
     * @param tabla Nombre de la tabla a filtrar
     * @return Lista de auditorías de la tabla especificada
     */
    @Transactional(readOnly = true)
    public List<Auditoria> obtenerAuditoriasPorTabla(String tabla) {
        try {
            return auditoriaRepository.findByTablaAfectadaOrderByFechaOperacionDesc(tabla);
        } catch (Exception e) {
            System.err.println("Error al obtener auditorías por tabla: " + e.getMessage());
            throw new RuntimeException("Error al filtrar auditorías por tabla", e);
        }
    }
    
    /**
     * Obtiene auditorías recientes (últimos 7 días)
     * @return Lista de auditorías recientes
     */
    @Transactional(readOnly = true)
    public List<Auditoria> obtenerAuditoriasRecientes() {
        try {
            LocalDateTime hace7Dias = LocalDateTime.now().minusDays(7);
            return auditoriaRepository.findAuditoriasRecientes(hace7Dias);
        } catch (Exception e) {
            System.err.println("Error al obtener auditorías recientes: " + e.getMessage());
            throw new RuntimeException("Error al obtener auditorías recientes", e);
        }
    }
    
    /**
     * Obtiene auditorías en un rango de fechas
     * @param inicio Fecha inicial del rango
     * @param fin Fecha final del rango
     * @return Lista de auditorías en el rango especificado
     */
    @Transactional(readOnly = true)
    public List<Auditoria> obtenerAuditoriasPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        try {
            return auditoriaRepository.findByFechaOperacionBetween(inicio, fin);
        } catch (Exception e) {
            System.err.println("Error al obtener auditorías por rango de fechas: " + e.getMessage());
            throw new RuntimeException("Error al buscar auditorías por fechas", e);
        }
    }
    
    /**
     * Obtiene auditorías de un usuario específico
     * @param usuarioId ID del usuario
     * @return Lista de auditorías del usuario
     */
    @Transactional(readOnly = true)
    public List<Auditoria> obtenerAuditoriasPorUsuario(Integer usuarioId) {
        try {
            return auditoriaRepository.findByUsuarioIdOrderByFechaOperacionDesc(usuarioId);
        } catch (Exception e) {
            System.err.println("Error al obtener auditorías por usuario: " + e.getMessage());
            throw new RuntimeException("Error al filtrar auditorías por usuario", e);
        }
    }
    
    /**
     * Obtiene auditorías por tipo de operación
     * @param operacion Tipo de operación (INSERT, UPDATE, DELETE)
     * @return Lista de auditorías de la operación especificada
     */
    @Transactional(readOnly = true)
    public List<Auditoria> obtenerAuditoriasPorOperacion(Auditoria.Operacion operacion) {
        try {
            return auditoriaRepository.findByOperacion(operacion);
        } catch (Exception e) {
            System.err.println("Error al obtener auditorías por operación: " + e.getMessage());
            throw new RuntimeException("Error al filtrar auditorías por operación", e);
        }
    }
    
    /**
     * Obtiene auditorías de un registro específico
     * @param tabla Nombre de la tabla
     * @param registroId ID del registro
     * @return Lista de auditorías del registro
     */
    @Transactional(readOnly = true)
    public List<Auditoria> obtenerAuditoriasPorRegistro(String tabla, Integer registroId) {
        try {
            return auditoriaRepository.findByTablaAndRegistroId(tabla, registroId);
        } catch (Exception e) {
            System.err.println("Error al obtener auditorías por registro: " + e.getMessage());
            throw new RuntimeException("Error al buscar auditorías del registro", e);
        }
    }
    
    /**
     * Guarda una nueva auditoría en la base de datos
     * @param auditoria Objeto de auditoría a guardar
     * @return Auditoría guardada
     */
    @Transactional
    public Auditoria guardarAuditoria(Auditoria auditoria) {
        try {
            if (auditoria.getFechaOperacion() == null) {
                auditoria.setFechaOperacion(LocalDateTime.now());
            }
            
            Auditoria auditoriaGuardada = auditoriaRepository.save(auditoria);
            System.out.println("✅ Auditoría guardada: " + auditoria.getOperacion() + 
                             " en " + auditoria.getTablaAfectada());
            return auditoriaGuardada;
            
        } catch (Exception e) {
            System.err.println("Error al guardar auditoría: " + e.getMessage());
            throw new RuntimeException("Error al guardar auditoría", e);
        }
    }
    
    /**
     * Registra una auditoría manualmente
     * @param tabla Nombre de la tabla afectada
     * @param operacion Tipo de operación realizada
     * @param registroId ID del registro afectado
     * @param usuarioId ID del usuario que realizó la operación
     * @param datosAnteriores Datos antes de la operación (JSON)
     * @param datosNuevos Datos después de la operación (JSON)
     */
    @Transactional
    public void registrarAuditoria(String tabla, Auditoria.Operacion operacion, 
                                   Integer registroId, Integer usuarioId, 
                                   String datosAnteriores, String datosNuevos) {
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.setTablaAfectada(tabla);
            auditoria.setOperacion(operacion);
            auditoria.setRegistroId(registroId);
            auditoria.setDatosAnteriores(datosAnteriores);
            auditoria.setDatosNuevos(datosNuevos);
            auditoria.setFechaOperacion(LocalDateTime.now());
            
            // Asignar usuario si se proporciona
            if (usuarioId != null) {
                Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
                usuario.ifPresent(auditoria::setUsuario);
            }
            
            auditoriaRepository.save(auditoria);
            System.out.println("✅ Auditoría registrada manualmente: " + operacion + " en " + tabla);
            
        } catch (Exception e) {
            System.err.println("Error al registrar auditoría manual: " + e.getMessage());
            throw new RuntimeException("Error al registrar auditoría", e);
        }
    }
    
    /**
     * Registra una auditoría simplificada (sin datos JSON)
     * @param tabla Nombre de la tabla afectada
     * @param operacion Tipo de operación realizada
     * @param registroId ID del registro afectado
     * @param usuarioId ID del usuario que realizó la operación
     */
    @Transactional
    public void registrarAuditoriaSimple(String tabla, Auditoria.Operacion operacion, 
                                        Integer registroId, Integer usuarioId) {
        registrarAuditoria(tabla, operacion, registroId, usuarioId, null, null);
    }
    
    /**
     * Cuenta el total de auditorías del día actual
     * @return Cantidad de auditorías registradas hoy
     */
    @Transactional(readOnly = true)
    public Long contarAuditoriasHoy() {
        try {
            LocalDateTime hoy = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            return auditoriaRepository.countAuditoriasDesde(hoy);
        } catch (Exception e) {
            System.err.println("Error al contar auditorías de hoy: " + e.getMessage());
            return 0L;
        }
    }
    
    /**
     * Cuenta auditorías por tipo de operación
     * @param operacion Tipo de operación a contar
     * @return Cantidad de auditorías de la operación
     */
    @Transactional(readOnly = true)
    public Long contarAuditoriasPorOperacion(Auditoria.Operacion operacion) {
        try {
            return auditoriaRepository.findByOperacion(operacion).stream().count();
        } catch (Exception e) {
            System.err.println("Error al contar auditorías por operación: " + e.getMessage());
            return 0L;
        }
    }
    
    /**
     * Cuenta auditorías desde una fecha específica
     * @param fecha Fecha desde la cual contar
     * @return Cantidad de auditorías desde la fecha
     */
    @Transactional(readOnly = true)
    public Long contarAuditoriasDesde(LocalDateTime fecha) {
        try {
            return auditoriaRepository.countAuditoriasDesde(fecha);
        } catch (Exception e) {
            System.err.println("Error al contar auditorías desde fecha: " + e.getMessage());
            return 0L;
        }
    }
    
    /**
     * Obtiene estadísticas de auditoría
     * @return Mapa con estadísticas (total, por operación, etc.)
     */
    @Transactional(readOnly = true)
    public java.util.Map<String, Long> obtenerEstadisticasAuditoria() {
        try {
            java.util.Map<String, Long> estadisticas = new java.util.HashMap<>();
            
            estadisticas.put("total", (long) auditoriaRepository.findAll().size());
            estadisticas.put("hoy", contarAuditoriasHoy());
            estadisticas.put("inserciones", contarAuditoriasPorOperacion(Auditoria.Operacion.INSERT));
            estadisticas.put("actualizaciones", contarAuditoriasPorOperacion(Auditoria.Operacion.UPDATE));
            estadisticas.put("eliminaciones", contarAuditoriasPorOperacion(Auditoria.Operacion.DELETE));
            
            return estadisticas;
        } catch (Exception e) {
            System.err.println("Error al obtener estadísticas: " + e.getMessage());
            return new java.util.HashMap<>();
        }
    }
    
    /**
     * Limpia auditorías antiguas (mayores a X días)
     * @param dias Cantidad de días a mantener
     * @return Cantidad de registros eliminados
     */
    @Transactional
    public int limpiarAuditoriasAntiguas(int dias) {
        try {
            LocalDateTime fechaLimite = LocalDateTime.now().minusDays(dias);
            List<Auditoria> auditoriasAntiguas = auditoriaRepository.findAll().stream()
                .filter(a -> a.getFechaOperacion().isBefore(fechaLimite))
                .toList();
            
            int cantidad = auditoriasAntiguas.size();
            auditoriaRepository.deleteAll(auditoriasAntiguas);
            
            System.out.println("🧹 Limpieza de auditorías: " + cantidad + " registros eliminados");
            return cantidad;
            
        } catch (Exception e) {
            System.err.println("Error al limpiar auditorías antiguas: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Obtiene auditorías del mes actual
     * @return Lista de auditorías del mes
     */
    @Transactional(readOnly = true)
    public List<Auditoria> obtenerAuditoriasDelMes() {
        try {
            LocalDateTime inicioMes = LocalDateTime.now()
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0);
            
            LocalDateTime finMes = LocalDateTime.now()
                .withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth())
                .withHour(23)
                .withMinute(59)
                .withSecond(59);
            
            return auditoriaRepository.findByFechaOperacionBetween(inicioMes, finMes);
        } catch (Exception e) {
            System.err.println("Error al obtener auditorías del mes: " + e.getMessage());
            throw new RuntimeException("Error al obtener auditorías del mes", e);
        }
    }
    
    /**
     * Obtiene las últimas N auditorías
     * @param limite Cantidad de auditorías a obtener
     * @return Lista de últimas auditorías
     */
    @Transactional(readOnly = true)
    public List<Auditoria> obtenerUltimasAuditorias(int limite) {
        try {
            return auditoriaRepository.findAll().stream()
                .sorted((a1, a2) -> a2.getFechaOperacion().compareTo(a1.getFechaOperacion()))
                .limit(limite)
                .toList();
        } catch (Exception e) {
            System.err.println("Error al obtener últimas auditorías: " + e.getMessage());
            throw new RuntimeException("Error al obtener últimas auditorías", e);
        }
    }
    
    /**
     * Verifica si existe auditoría para un registro específico
     * @param tabla Nombre de la tabla
     * @param registroId ID del registro
     * @return true si existe auditoría, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean existeAuditoriaPara(String tabla, Integer registroId) {
        try {
            List<Auditoria> auditorias = auditoriaRepository.findByTablaAndRegistroId(tabla, registroId);
            return !auditorias.isEmpty();
        } catch (Exception e) {
            System.err.println("Error al verificar existencia de auditoría: " + e.getMessage());
            return false;
        }
    }
}