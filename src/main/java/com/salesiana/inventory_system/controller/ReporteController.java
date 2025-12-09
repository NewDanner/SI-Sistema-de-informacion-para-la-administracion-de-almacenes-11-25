package com.salesiana.inventory_system.controller;

import com.salesiana.inventory_system.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    //============================================
    // REPORTES DE INVENTARIO
    //============================================
    @GetMapping("/inventario/excel")
    public ResponseEntity<ByteArrayResource> descargarReporteInventarioExcel() {
        try {
            byte[] excelContent = reporteService.generarReporteInventarioExcel();
            String filename = "reporte_inventario_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(new ByteArrayResource(excelContent));
        } catch (IOException e) {
            System.err.println("Error generando reporte Excel: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/inventario/pdf")
    public ResponseEntity<ByteArrayResource> descargarReporteInventarioPdf() {
        try {
            byte[] htmlContent = reporteService.generarReporteInventarioPdf();
            String filename = "reporte_inventario_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".html";
            
            // INLINE para visualizar en navegador (con botón de descarga en el HTML)
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(new ByteArrayResource(htmlContent));
        } catch (IOException e) {
            System.err.println("Error generando reporte PDF: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    //============================================
    // REPORTES DE STOCK BAJO Y AGOTADOS
    //============================================
    @GetMapping("/stock-bajo/excel")
    public ResponseEntity<ByteArrayResource> descargarReporteStockBajoExcel() {
        try {
            byte[] excelContent = reporteService.generarReporteStockBajoExcel();
            String filename = "reporte_stock_bajo_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(new ByteArrayResource(excelContent));
        } catch (IOException e) {
            System.err.println("Error generando reporte Excel: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/stock-bajo/pdf")
    public ResponseEntity<ByteArrayResource> descargarReporteStockBajoPdf() {
        try {
            byte[] htmlContent = reporteService.generarReporteStockBajoPdf();
            String filename = "reporte_stock_bajo_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".html";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(new ByteArrayResource(htmlContent));
        } catch (IOException e) {
            System.err.println("Error generando reporte PDF: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    //============================================
    // REPORTES DE VENCIMIENTOS
    //============================================
    @GetMapping("/vencimientos/excel")
    public ResponseEntity<ByteArrayResource> descargarReporteVencimientosExcel() {
        try {
            byte[] excelContent = reporteService.generarReporteVencimientosExcelReal();
            String filename = "reporte_vencimientos_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(new ByteArrayResource(excelContent));
        } catch (IOException e) {
            System.err.println("Error generando reporte: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/vencimientos/pdf")
    public ResponseEntity<ByteArrayResource> descargarReporteVencimientosPdf() {
        try {
            byte[] htmlContent = reporteService.generarReporteVencimientosPdfReal();
            String filename = "reporte_vencimientos_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".html";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(new ByteArrayResource(htmlContent));
        } catch (IOException e) {
            System.err.println("Error generando reporte: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    //============================================
    // REPORTES DE ROTACIÓN
    //============================================
    @GetMapping("/rotacion/excel")
    public ResponseEntity<ByteArrayResource> descargarReporteRotacionExcel() {
        try {
            byte[] excelContent = reporteService.generarReporteRotacionExcelReal();
            String filename = "reporte_rotacion_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(new ByteArrayResource(excelContent));
        } catch (IOException e) {
            System.err.println("Error generando reporte: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/rotacion/pdf")
    public ResponseEntity<ByteArrayResource> descargarReporteRotacionPdf() {
        try {
            byte[] htmlContent = reporteService.generarReporteRotacionPdfReal();
            String filename = "reporte_rotacion_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".html";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(new ByteArrayResource(htmlContent));
        } catch (IOException e) {
            System.err.println("Error generando reporte: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    //============================================
    // REPORTES DE MOVIMIENTOS SEMANALES
    //============================================
    @GetMapping("/movimientos-semanales/excel")
    public ResponseEntity<ByteArrayResource> descargarReporteMovimientosSemanalesExcel() {
        try {
            byte[] excelContent = reporteService.generarReporteMovimientosSemanalesExcelReal();
            String filename = "reporte_movimientos_semanales_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(new ByteArrayResource(excelContent));
        } catch (IOException e) {
            System.err.println("Error generando reporte: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/movimientos-semanales/pdf")
    public ResponseEntity<ByteArrayResource> descargarReporteMovimientosSemanalesPdf() {
        try {
            byte[] htmlContent = reporteService.generarReporteMovimientosSemanalesPdfReal();
            String filename = "reporte_movimientos_semanales_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".html";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(new ByteArrayResource(htmlContent));
        } catch (IOException e) {
            System.err.println("Error generando reporte: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    //============================================
    // REPORTES DE MOVIMIENTOS ANUALES
    //============================================
    @GetMapping("/movimientos-anuales/excel")
    public ResponseEntity<ByteArrayResource> descargarReporteMovimientosAnualesExcel() {
        try {
            byte[] excelContent = reporteService.generarReporteMovimientosAnualesExcelReal();
            String filename = "reporte_movimientos_anuales_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(new ByteArrayResource(excelContent));
        } catch (IOException e) {
            System.err.println("Error generando reporte: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/movimientos-anuales/pdf")
    public ResponseEntity<ByteArrayResource> descargarReporteMovimientosAnualesPdf() {
        try {
            byte[] htmlContent = reporteService.generarReporteMovimientosAnualesPdfReal();
            String filename = "reporte_movimientos_anuales_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".html";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(new ByteArrayResource(htmlContent));
        } catch (IOException e) {
            System.err.println("Error generando reporte: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    //============================================
    // PÁGINA PRINCIPAL DE REPORTES
    //============================================
    @GetMapping
    public String mostrarPaginaReportes() {
        return "reportes/lista";
    }
}