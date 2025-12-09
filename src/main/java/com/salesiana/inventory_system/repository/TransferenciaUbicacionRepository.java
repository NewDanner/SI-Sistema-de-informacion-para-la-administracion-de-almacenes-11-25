package com.salesiana.inventory_system.repository;

import com.salesiana.inventory_system.entity.TransferenciaUbicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransferenciaUbicacionRepository extends JpaRepository<TransferenciaUbicacion, Integer> {

    // ✅ MÉTODOS CORREGIDOS - Usando consultas @Query explícitas
    @Query("SELECT t FROM TransferenciaUbicacion t WHERE t.producto.id = :productoId ORDER BY t.fechaTransferencia DESC")
    List<TransferenciaUbicacion> findByProductoId(@Param("productoId") Integer productoId);

    @Query("SELECT t FROM TransferenciaUbicacion t WHERE t.lote.id = :loteId ORDER BY t.fechaTransferencia DESC")
    List<TransferenciaUbicacion> findByLoteId(@Param("loteId") Integer loteId);

    @Query("SELECT t FROM TransferenciaUbicacion t WHERE t.ubicacionOrigen.id = :origenId OR t.ubicacionDestino.id = :destinoId ORDER BY t.fechaTransferencia DESC")
    List<TransferenciaUbicacion> findByUbicacionOrigenIdOrUbicacionDestinoId(@Param("origenId") Integer origenId, 
                                                                             @Param("destinoId") Integer destinoId);

    List<TransferenciaUbicacion> findByFechaTransferenciaBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT t FROM TransferenciaUbicacion t ORDER BY t.fechaTransferencia DESC LIMIT 50")
    List<TransferenciaUbicacion> findUltimasTransferencias();

    @Query("SELECT t FROM TransferenciaUbicacion t WHERE t.ubicacionDestino.id = :ubicacionDestinoId ORDER BY t.fechaTransferencia DESC")
    List<TransferenciaUbicacion> findByUbicacionDestinoId(@Param("ubicacionDestinoId") Integer ubicacionDestinoId);

    @Query("SELECT t FROM TransferenciaUbicacion t WHERE t.ubicacionOrigen.id = :ubicacionOrigenId ORDER BY t.fechaTransferencia DESC")
    List<TransferenciaUbicacion> findByUbicacionOrigenId(@Param("ubicacionOrigenId") Integer ubicacionOrigenId);
}
