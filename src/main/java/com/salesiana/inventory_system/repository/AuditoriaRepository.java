package com.salesiana.inventory_system.repository;

import com.salesiana.inventory_system.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {
    
    /**
     * Busca auditorías por tabla afectada ordenadas por fecha descendente
     */
    List<Auditoria> findByTablaAfectadaOrderByFechaOperacionDesc(String tablaAfectada);
    
    /**
     * Busca auditorías por rango de fechas
     */
    List<Auditoria> findByFechaOperacionBetween(LocalDateTime inicio, LocalDateTime fin);
    
    /**
     * Busca auditorías por usuario ordenadas por fecha descendente
     */
    List<Auditoria> findByUsuarioIdOrderByFechaOperacionDesc(Integer usuarioId);
    
    /**
     * Obtiene auditorías recientes desde una fecha específica
     */
    @Query("SELECT a FROM Auditoria a WHERE a.fechaOperacion >= :fecha ORDER BY a.fechaOperacion DESC")
    List<Auditoria> findAuditoriasRecientes(@Param("fecha") LocalDateTime fecha);
    
    /**
     * Cuenta auditorías desde una fecha específica
     */
    @Query("SELECT COUNT(a) FROM Auditoria a WHERE a.fechaOperacion >= :fecha")
    Long countAuditoriasDesde(@Param("fecha") LocalDateTime fecha);
    
    /**
     * Obtiene auditorías por operación
     */
    List<Auditoria> findByOperacion(Auditoria.Operacion operacion);
    
    /**
     * Busca auditorías por tabla y operación
     */
    List<Auditoria> findByTablaAfectadaAndOperacion(String tabla, Auditoria.Operacion operacion);
    
    /**
     * Busca auditorías por registro específico
     */
    @Query("SELECT a FROM Auditoria a WHERE a.tablaAfectada = :tabla AND a.registroId = :registroId ORDER BY a.fechaOperacion DESC")
    List<Auditoria> findByTablaAndRegistroId(@Param("tabla") String tabla, @Param("registroId") Integer registroId);
}