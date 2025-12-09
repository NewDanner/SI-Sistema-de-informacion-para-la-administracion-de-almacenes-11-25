/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesiana.inventory_system.service;

/**
 *
 * @author Andrei
 */

import com.salesiana.inventory_system.entity.Alerta;
import com.salesiana.inventory_system.entity.Producto;
import com.salesiana.inventory_system.entity.Lote;
import com.salesiana.inventory_system.entity.TipoAlerta;
import com.salesiana.inventory_system.repository.AlertaRepository;
import com.salesiana.inventory_system.repository.ProductoRepository;
import com.salesiana.inventory_system.repository.LoteRepository;
import com.salesiana.inventory_system.repository.TipoAlertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertaService {
    
    @Autowired
    private AlertaRepository alertaRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private LoteRepository loteRepository;
    
    @Autowired
    private TipoAlertaRepository tipoAlertaRepository;
    
    @Scheduled(cron = "0 0 8 * * ?") // Ejecutar todos los días a las 8:00 AM
    public void verificarAlertasAutomaticas() {
        verificarStockMinimo();
        verificarVencimientos();
    }
    
    private void verificarStockMinimo() {
        TipoAlerta tipoStockMinimo = tipoAlertaRepository.findByCodigo("stock_minimo")
                .orElseThrow(() -> new RuntimeException("Tipo de alerta no encontrado"));
        
        TipoAlerta tipoStockCritico = tipoAlertaRepository.findByCodigo("stock_critico")
                .orElseThrow(() -> new RuntimeException("Tipo de alerta no encontrado"));
        
        List<Producto> productosStockBajo = productoRepository.findProductosStockBajo();
        List<Producto> productosAgotados = productoRepository.findProductosAgotados();
        
        for (Producto producto : productosStockBajo) {
            if (!existeAlertaReciente(producto.getId(), tipoStockMinimo.getId())) {
                Alerta alerta = new Alerta();
                alerta.setTipoAlerta(tipoStockMinimo);
                alerta.setProducto(producto);
                alerta.setMensaje("El producto " + producto.getNombre() + 
                                " tiene stock bajo. Stock actual: " + producto.getStockActual() +
                                ", Stock mínimo: " + producto.getStockMinimo());
                // CORREGIDO: Usar el enum directamente
                alerta.setNivelPrioridad(Alerta.NivelPrioridad.alta);
                alertaRepository.save(alerta);
            }
        }
        
        for (Producto producto : productosAgotados) {
            if (!existeAlertaReciente(producto.getId(), tipoStockCritico.getId())) {
                Alerta alerta = new Alerta();
                alerta.setTipoAlerta(tipoStockCritico);
                alerta.setProducto(producto);
                alerta.setMensaje("¡URGENTE! El producto " + producto.getNombre() + " está agotado.");
                // CORREGIDO: Usar el enum directamente
                alerta.setNivelPrioridad(Alerta.NivelPrioridad.critica);
                alertaRepository.save(alerta);
            }
        }
    }
    
    private void verificarVencimientos() {
        TipoAlerta tipoVencimientoProximo = tipoAlertaRepository.findByCodigo("vencimiento_proximo")
                .orElseThrow(() -> new RuntimeException("Tipo de alerta no encontrado"));
        
        TipoAlerta tipoVencido = tipoAlertaRepository.findByCodigo("producto_vencido")
                .orElseThrow(() -> new RuntimeException("Tipo de alerta no encontrado"));
        
        LocalDate hoy = LocalDate.now();
        LocalDate en30Dias = hoy.plusDays(30);
        
        List<Lote> lotesPorVencer = loteRepository.findByFechaVencimientoBetween(hoy, en30Dias);
        List<Lote> lotesVencidos = loteRepository.findByFechaVencimientoBeforeAndCantidadActualGreaterThan(hoy, 0);
        
        for (Lote lote : lotesPorVencer) {
            if (!existeAlertaRecienteLote(lote.getId(), tipoVencimientoProximo.getId())) {
                Alerta alerta = new Alerta();
                alerta.setTipoAlerta(tipoVencimientoProximo);
                alerta.setProducto(lote.getProducto());
                alerta.setLote(lote);
                alerta.setMensaje("El lote " + lote.getNumeroLote() + " del producto " + 
                                lote.getProducto().getNombre() + " vence el " + lote.getFechaVencimiento());
                // CORREGIDO: Usar el enum directamente
                alerta.setNivelPrioridad(Alerta.NivelPrioridad.media);
                alertaRepository.save(alerta);
            }
        }
        
        for (Lote lote : lotesVencidos) {
            if (!existeAlertaRecienteLote(lote.getId(), tipoVencido.getId())) {
                Alerta alerta = new Alerta();
                alerta.setTipoAlerta(tipoVencido);
                alerta.setProducto(lote.getProducto());
                alerta.setLote(lote);
                alerta.setMensaje("¡CRÍTICO! El lote " + lote.getNumeroLote() + " del producto " + 
                                lote.getProducto().getNombre() + " está vencido desde " + lote.getFechaVencimiento());
                // CORREGIDO: Usar el enum directamente
                alerta.setNivelPrioridad(Alerta.NivelPrioridad.critica);
                alertaRepository.save(alerta);
            }
        }
    }
    
    private boolean existeAlertaReciente(Integer productoId, Integer tipoAlertaId) {
        LocalDateTime hace24Horas = LocalDateTime.now().minusHours(24);
        return alertaRepository.existsByProductoIdAndTipoAlertaIdAndFechaAlertaAfter(
            productoId, tipoAlertaId, hace24Horas);
    }
    
    private boolean existeAlertaRecienteLote(Integer loteId, Integer tipoAlertaId) {
        LocalDateTime hace24Horas = LocalDateTime.now().minusHours(24);
        return alertaRepository.existsByLoteIdAndTipoAlertaIdAndFechaAlertaAfter(
            loteId, tipoAlertaId, hace24Horas);
    }
    
    public List<Alerta> obtenerAlertasNoLeidas() {
        return alertaRepository.findByLeidaFalseOrderByFechaAlertaDesc();
    }
    
    public void marcarAlertaComoLeida(Integer id) {
        alertaRepository.findById(id).ifPresent(alerta -> {
            alerta.setLeida(true);
            alerta.setFechaLectura(LocalDateTime.now());
            alertaRepository.save(alerta);
        });
    }
}