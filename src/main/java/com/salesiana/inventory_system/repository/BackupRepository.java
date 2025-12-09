package com.salesiana.inventory_system.repository;

import com.salesiana.inventory_system.entity.Backup;
import com.salesiana.inventory_system.entity.Backup.EstadoBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BackupRepository extends JpaRepository<Backup, Integer> {
    
    List<Backup> findAllByOrderByFechaCreacionDesc();
    
    List<Backup> findByEstadoOrderByFechaCreacionDesc(EstadoBackup estado);
    
    @Query("SELECT b FROM Backup b WHERE b.fechaCreacion < :fecha")
    List<Backup> findBackupsAntiguos(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT COUNT(b) FROM Backup b WHERE b.estado = :estado")
    Long countByEstado(@Param("estado") EstadoBackup estado);
    
    @Query("SELECT COALESCE(SUM(b.tamanoBytes), 0) FROM Backup b WHERE b.estado = 'EXITOSO'")
    Long sumTamanoTotal();
}