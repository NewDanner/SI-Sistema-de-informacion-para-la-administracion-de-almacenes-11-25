package com.salesiana.inventory_system.repository;

import com.salesiana.inventory_system.entity.UbicacionAlmacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UbicacionAlmacenRepository extends JpaRepository<UbicacionAlmacen, Integer> {
    Optional<UbicacionAlmacen> findByCodigo(String codigo);
    List<UbicacionAlmacen> findByActivaTrue();
    List<UbicacionAlmacen> findByTipo(UbicacionAlmacen.TipoUbicacion tipo);
    List<UbicacionAlmacen> findByUbicacionPadreId(Integer ubicacionPadreId);
    
    @Query("SELECT u FROM UbicacionAlmacen u WHERE u.ubicacionPadre IS NULL AND u.activa = true")
    List<UbicacionAlmacen> findUbicacionesPrincipales();
    
    @Query("SELECT u FROM UbicacionAlmacen u WHERE u.capacidadMaxima IS NOT NULL AND u.capacidadActual < u.capacidadMaxima AND u.activa = true")
    List<UbicacionAlmacen> findUbicacionesConCapacidad();
}