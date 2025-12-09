package com.salesiana.inventory_system.service;

import com.salesiana.inventory_system.entity.Backup;
import com.salesiana.inventory_system.entity.Usuario;
import com.salesiana.inventory_system.repository.BackupRepository;
import com.salesiana.inventory_system.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BackupService {
    
    @Autowired
    private BackupRepository backupRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Value("${spring.datasource.url}")
    private String datasourceUrl;
    
    @Value("${spring.datasource.username}")
    private String dbUsername;
    
    @Value("${spring.datasource.password}")
    private String dbPassword;
    
    // Directorio donde se guardarán los backups
    private static final String BACKUP_DIRECTORY = "backups/";
    
    /**
     * Crea un backup manual de la base de datos
     */
    @Transactional
    public Backup crearBackupManual() {
        log.info("Iniciando backup manual...");
        return crearBackup(Backup.TipoBackup.MANUAL, obtenerUsuarioActual());
    }
    
    /**
     * Tarea programada: Backup automático cada 5 días a las 2:00 AM
     */
    @Scheduled(cron = "0 0 2 */5 * ?") // Cada 5 días a las 2:00 AM
    @Transactional
    public void backupAutomatico() {
        log.info("Iniciando backup automático programado...");
        crearBackup(Backup.TipoBackup.AUTOMATICO, null);
    }
    
    /**
     * Tarea programada: Limpia backups antiguos (mayores a 10 días) cada día a las 3:00 AM
     */
    @Scheduled(cron = "0 0 3 * * ?") // Todos los días a las 3:00 AM
    @Transactional
    public void limpiarBackupsAntiguos() {
        log.info("Iniciando limpieza de backups antiguos...");
        
        try {
            LocalDateTime fechaLimite = LocalDateTime.now().minusDays(10);
            List<Backup> backupsAntiguos = backupRepository.findBackupsAntiguos(fechaLimite);
            
            int eliminados = 0;
            for (Backup backup : backupsAntiguos) {
                try {
                    // Eliminar archivo físico
                    File archivo = new File(backup.getRutaArchivo());
                    if (archivo.exists()) {
                        archivo.delete();
                        log.info("Archivo físico eliminado: {}", backup.getNombreArchivo());
                    }
                    
                    // Eliminar registro de BD
                    backupRepository.delete(backup);
                    eliminados++;
                    
                } catch (Exception e) {
                    log.error("Error al eliminar backup antiguo {}: {}", backup.getNombreArchivo(), e.getMessage());
                }
            }
            
            log.info("Limpieza completada. Backups eliminados: {}", eliminados);
            
        } catch (Exception e) {
            log.error("Error en limpieza de backups antiguos: {}", e.getMessage(), e);
        }
    }
    
    /**
 * Método principal para crear un backup
 */
private Backup crearBackup(Backup.TipoBackup tipo, Usuario usuario) {
    Backup backup = new Backup();
    backup.setTipoBackup(tipo);
    backup.setUsuario(usuario);
    backup.setEstado(Backup.EstadoBackup.EN_PROCESO);
    backup.setFechaCreacion(LocalDateTime.now());
    backup.setTamanoBytes(0L); // ✅ INICIALIZAR EN 0 para evitar null
    
    try {
        // Crear directorio si no existe
        File backupDir = new File(BACKUP_DIRECTORY);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        
        // Extraer nombre de BD de la URL
        String dbName = extraerNombreBaseDatos(datasourceUrl);
        
        // Generar nombre de archivo con timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nombreArchivo = String.format("backup_%s_%s.sql", dbName, timestamp);
        String rutaCompleta = BACKUP_DIRECTORY + nombreArchivo;
        
        backup.setNombreArchivo(nombreArchivo);
        backup.setRutaArchivo(rutaCompleta);
        backup.setDescripcion(String.format("Backup %s de la base de datos %s", 
            tipo.name().toLowerCase(), dbName));
        
        // ✅ GUARDAR REGISTRO INICIAL CON tamanoBytes = 0
        backup = backupRepository.save(backup);
        
        // Ejecutar mysqldump
        boolean exito = ejecutarMysqldump(dbName, rutaCompleta);
        
        if (exito) {
            // Verificar que el archivo se creó correctamente
            File archivoBackup = new File(rutaCompleta);
            if (archivoBackup.exists() && archivoBackup.length() > 0) {
                backup.setEstado(Backup.EstadoBackup.EXITOSO);
                backup.setTamanoBytes(archivoBackup.length()); // ✅ ACTUALIZAR tamaño real
                log.info("✅ Backup creado exitosamente: {} ({})", nombreArchivo, backup.getTamanoLegible());
            } else {
                backup.setEstado(Backup.EstadoBackup.FALLIDO);
                backup.setMensajeError("El archivo de backup está vacío o no se creó correctamente");
                log.error("❌ Backup falló: archivo vacío o no creado");
            }
        } else {
            backup.setEstado(Backup.EstadoBackup.FALLIDO);
            backup.setMensajeError("Error al ejecutar mysqldump");
            log.error("❌ Error al ejecutar mysqldump");
        }
        
    } catch (Exception e) {
        backup.setEstado(Backup.EstadoBackup.FALLIDO);
        backup.setMensajeError(e.getMessage());
        log.error("❌ Error al crear backup: {}", e.getMessage(), e);
    }
    
    // ✅ GUARDAR REGISTRO FINAL con todos los datos actualizados
    return backupRepository.save(backup);
}
    
    /**
 * Ejecuta el comando mysqldump para crear el backup
 */
private boolean ejecutarMysqldump(String dbName, String rutaArchivo) {
    try {
        // Construir comando mysqldump con ruta completa
        String comando = String.format(
            "\"%s\" -u%s -p%s --databases %s --result-file=\"%s\" --single-transaction --quick --lock-tables=false",
            mysqldumpPath, dbUsername, dbPassword, dbName, rutaArchivo
        );
        
        log.info("Ejecutando comando: {}", comando.replace(dbPassword, "****")); // Log sin password
        
        // Ejecutar comando
        Process proceso = Runtime.getRuntime().exec(comando);
        
        // Leer salida de error
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(proceso.getErrorStream()));
        StringBuilder errorOutput = new StringBuilder();
        String linea;
        while ((linea = errorReader.readLine()) != null) {
            errorOutput.append(linea).append("\n");
        }
        
        // Esperar a que termine el proceso
        int exitCode = proceso.waitFor();
        
        if (exitCode != 0) {
            log.error("Error en mysqldump: {}", errorOutput.toString());
            return false;
        }
        
        return true;
        
    } catch (Exception e) {
        log.error("Error al ejecutar mysqldump: {}", e.getMessage(), e);
        return false;
    }
}
    
    /**
     * Extrae el nombre de la base de datos de la URL JDBC
     */
    private String extraerNombreBaseDatos(String url) {
        // jdbc:mysql://localhost:3306/nombre_bd?params
        try {
            String[] partes = url.split("/");
            String ultimaParte = partes[partes.length - 1];
            return ultimaParte.split("\\?")[0];
        } catch (Exception e) {
            return "database";
        }
    }
    
    /**
     * Obtiene usuario actual del contexto de seguridad
     */
    private Usuario obtenerUsuarioActual() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                return usuarioRepository.findByUsername(auth.getName()).orElse(null);
            }
        } catch (Exception e) {
            log.error("Error al obtener usuario actual: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Lista todos los backups ordenados por fecha
     */
    public List<Backup> listarTodos() {
        return backupRepository.findAllByOrderByFechaCreacionDesc();
    }
    
    /**
     * Obtiene un backup por ID
     */
    public Optional<Backup> obtenerPorId(Integer id) {
        return backupRepository.findById(id);
    }
    
    /**
     * Elimina un backup (archivo y registro)
     */
    @Transactional
    public boolean eliminar(Integer id) {
        try {
            Optional<Backup> backupOpt = backupRepository.findById(id);
            if (backupOpt.isPresent()) {
                Backup backup = backupOpt.get();
                
                // Eliminar archivo físico
                File archivo = new File(backup.getRutaArchivo());
                if (archivo.exists()) {
                    archivo.delete();
                }
                
                // Eliminar registro
                backupRepository.delete(backup);
                log.info("Backup eliminado: {}", backup.getNombreArchivo());
                return true;
            }
        } catch (Exception e) {
            log.error("Error al eliminar backup: {}", e.getMessage(), e);
        }
        return false;
    }
    
    /**
     * Obtiene estadísticas de backups
     */
    public BackupStats obtenerEstadisticas() {
        BackupStats stats = new BackupStats();
        stats.setTotalBackups(backupRepository.count());
        stats.setBackupsExitosos(backupRepository.countByEstado(Backup.EstadoBackup.EXITOSO));
        stats.setBackupsFallidos(backupRepository.countByEstado(Backup.EstadoBackup.FALLIDO));
        stats.setEspacioTotalBytes(backupRepository.sumTamanoTotal());
        return stats;
    }
    
    // Clase auxiliar para estadísticas
    public static class BackupStats {
        private Long totalBackups;
        private Long backupsExitosos;
        private Long backupsFallidos;
        private Long espacioTotalBytes;
        
        public Long getTotalBackups() { return totalBackups; }
        public void setTotalBackups(Long totalBackups) { this.totalBackups = totalBackups; }
        
        public Long getBackupsExitosos() { return backupsExitosos; }
        public void setBackupsExitosos(Long backupsExitosos) { this.backupsExitosos = backupsExitosos; }
        
        public Long getBackupsFallidos() { return backupsFallidos; }
        public void setBackupsFallidos(Long backupsFallidos) { this.backupsFallidos = backupsFallidos; }
        
        public Long getEspacioTotalBytes() { return espacioTotalBytes; }
        public void setEspacioTotalBytes(Long espacioTotalBytes) { this.espacioTotalBytes = espacioTotalBytes; }
        
        public String getEspacioTotal() {
            if (espacioTotalBytes == null || espacioTotalBytes == 0) return "0 KB";
            
            double kb = espacioTotalBytes / 1024.0;
            double mb = kb / 1024.0;
            double gb = mb / 1024.0;
            
            if (gb >= 1) {
                return String.format("%.2f GB", gb);
            } else if (mb >= 1) {
                return String.format("%.2f MB", mb);
            } else {
                return String.format("%.2f KB", kb);
            }
        }
    }
    @Value("${backup.mysqldump.path:mysqldump}")
    private String mysqldumpPath;
}