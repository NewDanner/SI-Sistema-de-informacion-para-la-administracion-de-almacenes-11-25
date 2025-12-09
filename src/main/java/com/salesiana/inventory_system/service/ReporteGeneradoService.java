/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesiana.inventory_system.service;

/**
 *
 * @author Andrei
 */

import com.salesiana.inventory_system.entity.ReporteGenerado;
import com.salesiana.inventory_system.repository.ReporteGeneradoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReporteGeneradoService {
    
    @Autowired
    private ReporteGeneradoRepository reporteGeneradoRepository;
    
    public List<ReporteGenerado> obtenerReportesPorUsuario(Integer usuarioId) {
        return reporteGeneradoRepository.findByUsuarioIdOrderByFechaGeneracionDesc(usuarioId);
    }
    
    public List<ReporteGenerado> obtenerReportesRecientes() {
        LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);
        return reporteGeneradoRepository.findReportesRecientes(hace30Dias);
    }
    
    public ReporteGenerado guardarReporteGenerado(ReporteGenerado reporte) {
        return reporteGeneradoRepository.save(reporte);
    }
    
    public ReporteGenerado registrarReporte(String tipoReporte, ReporteGenerado.Formato formato, 
                                           Integer usuarioId, String parametros, 
                                           String nombreArchivo, String rutaArchivo) {
        ReporteGenerado reporte = new ReporteGenerado();
        reporte.setTipoReporte(tipoReporte);
        reporte.setFormato(formato);
        // usuario se setearía si se tiene el objeto completo
        reporte.setParametros(parametros);
        reporte.setNombreArchivo(nombreArchivo);
        reporte.setRutaArchivo(rutaArchivo);
        
        return reporteGeneradoRepository.save(reporte);
    }
    
    public Long contarReportesHoy() {
        LocalDateTime hoy = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return reporteGeneradoRepository.countReportesDesde(hoy);
    }
}
