package com.salesiana.inventory_system.aspect;

import com.salesiana.inventory_system.entity.Auditoria;
import com.salesiana.inventory_system.entity.Usuario;
import com.salesiana.inventory_system.repository.AuditoriaRepository;
import com.salesiana.inventory_system.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * Aspecto AOP para auditoría automática de operaciones en el sistema
 */
@Aspect
@Component
public class AuditoriaAspect {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Audita operaciones de creación (INSERT)
     */
    @AfterReturning(
        pointcut = "execution(* com.salesiana.inventory_system.service.*.guardar*(..)) || " +
                  "execution(* com.salesiana.inventory_system.service.*.crear*(..)) || " +
                  "execution(* com.salesiana.inventory_system.service.*.registrar*(..))",
        returning = "result"
    )
    public void auditorCreacion(JoinPoint joinPoint, Object result) {
        try {
            if (result == null) return;

            String nombreMetodo = joinPoint.getSignature().getName();
            String nombreClase = joinPoint.getTarget().getClass().getSimpleName();
            String tablaAfectada = extraerNombreTabla(nombreClase);

            Auditoria auditoria = new Auditoria();
            auditoria.setTablaAfectada(tablaAfectada);
            auditoria.setOperacion(Auditoria.Operacion.INSERT);
            auditoria.setRegistroId(extraerIdDeObjeto(result));
            auditoria.setUsuario(obtenerUsuarioActual());
            auditoria.setDatosNuevos(convertirAJson(result));
            auditoria.setIpAddress(obtenerIpCliente());
            auditoria.setFechaOperacion(LocalDateTime.now());

            auditoriaRepository.save(auditoria);
            System.out.println("✅ Auditoría registrada: INSERT en " + tablaAfectada);
        } catch (Exception e) {
            System.err.println("❌ Error al registrar auditoría de creación: " + e.getMessage());
        }
    }

    /**
     * Audita operaciones de actualización (UPDATE)
     */
    @Around("execution(* com.salesiana.inventory_system.service.*.actualizar*(..))")
    public Object auditarActualizacion(ProceedingJoinPoint joinPoint) throws Throwable {
        Object resultado = null;
        Object valorAnterior = null;

        try {
            // Obtener el ID del objeto a actualizar
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                Integer id = extraerIdDeObjeto(args[0]);
                if (id != null) {
                    // Cargar estado anterior desde la BD
                    String nombreClase = joinPoint.getTarget().getClass().getSimpleName();
                    valorAnterior = obtenerEstadoAnterior(nombreClase, id);
                }
            }

            // Ejecutar el método
            resultado = joinPoint.proceed();

            // Registrar auditoría
            if (resultado != null) {
                String nombreClase = joinPoint.getTarget().getClass().getSimpleName();
                String tablaAfectada = extraerNombreTabla(nombreClase);

                Auditoria auditoria = new Auditoria();
                auditoria.setTablaAfectada(tablaAfectada);
                auditoria.setOperacion(Auditoria.Operacion.UPDATE);
                auditoria.setRegistroId(extraerIdDeObjeto(resultado));
                auditoria.setUsuario(obtenerUsuarioActual());
                auditoria.setDatosAnteriores(convertirAJson(valorAnterior));
                auditoria.setDatosNuevos(convertirAJson(resultado));
                auditoria.setIpAddress(obtenerIpCliente());
                auditoria.setFechaOperacion(LocalDateTime.now());

                auditoriaRepository.save(auditoria);
                System.out.println("✅ Auditoría registrada: UPDATE en " + tablaAfectada);
            }
        } catch (Exception e) {
            System.err.println("❌ Error al registrar auditoría de actualización: " + e.getMessage());
        }

        return resultado;
    }

    /**
     * Audita operaciones de eliminación (DELETE)
     */
    @Before("execution(* com.salesiana.inventory_system.service.*.eliminar*(..)) || " +
            "execution(* com.salesiana.inventory_system.service.*.desactivar*(..))")
    public void auditarEliminacion(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof Integer) {
                Integer id = (Integer) args[0];
                String nombreClase = joinPoint.getTarget().getClass().getSimpleName();
                String tablaAfectada = extraerNombreTabla(nombreClase);

                // Obtener estado anterior
                Object estadoAnterior = obtenerEstadoAnterior(nombreClase, id);

                Auditoria auditoria = new Auditoria();
                auditoria.setTablaAfectada(tablaAfectada);
                auditoria.setOperacion(Auditoria.Operacion.DELETE);
                auditoria.setRegistroId(id);
                auditoria.setUsuario(obtenerUsuarioActual());
                auditoria.setDatosAnteriores(convertirAJson(estadoAnterior));
                auditoria.setIpAddress(obtenerIpCliente());
                auditoria.setFechaOperacion(LocalDateTime.now());

                auditoriaRepository.save(auditoria);
                System.out.println("✅ Auditoría registrada: DELETE en " + tablaAfectada);
            }
        } catch (Exception e) {
            System.err.println("❌ Error al registrar auditoría de eliminación: " + e.getMessage());
        }
    }

    // ===============================
    // MÉTODOS AUXILIARES CORREGIDOS
    // ===============================

    /**
     * Obtiene el usuario actual desde el contexto de seguridad
     */
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

    /**
     * Obtiene la IP del cliente desde la petición HTTP
     */
    private String obtenerIpCliente() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty()) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            System.err.println("Error al obtener IP del cliente: " + e.getMessage());
        }
        return "unknown";
    }

    /**
     * Extrae el nombre de la tabla desde el nombre del servicio
     */
    private String extraerNombreTabla(String nombreClase) {
        String tabla = nombreClase.replace("Service", "").toLowerCase();
        
        // Pluralizar nombres comunes
        if (tabla.equals("producto")) return "productos";
        if (tabla.equals("usuario")) return "usuarios";
        if (tabla.equals("categoria")) return "categorias";
        if (tabla.equals("proveedor")) return "proveedores";
        if (tabla.equals("movimiento")) return "movimientos";
        if (tabla.equals("alerta")) return "alertas";
        if (tabla.equals("lote")) return "lotes";
        
        return tabla + "s";
    }

    /**
     * Extrae el ID de un objeto usando reflexión
     */
    private Integer extraerIdDeObjeto(Object objeto) {
        if (objeto == null) return null;
        
        try {
            // Intentar obtener el método getId()
            var metodo = objeto.getClass().getMethod("getId");
            Object id = metodo.invoke(objeto);
            if (id instanceof Integer) {
                return (Integer) id;
            }
        } catch (Exception e) {
            // Ignorar si no tiene método getId()
        }
        return null;
    }

    /**
     * CORRECCIÓN CRÍTICA: Convierte un objeto a JSON de forma segura
     */
    private String convertirAJson(Object objeto) {
        if (objeto == null) return null;
        
        try {
            // Configurar ObjectMapper para evitar errores de serialización
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            objectMapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
            objectMapper.configure(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL, true);
            
            // Excluir propiedades problemáticas
            objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
            objectMapper.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
            objectMapper.setVisibility(PropertyAccessor.SETTER, Visibility.NONE);
            objectMapper.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);
            
            return objectMapper.writeValueAsString(objeto);
        } catch (Exception e) {
            System.err.println("Error al convertir a JSON: " + e.getMessage());
            // En lugar de retornar toString() que puede causar recursión, retornar mensaje seguro
            return "{\"error\": \"No se pudo serializar el objeto: " + e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    /**
     * Obtiene el estado anterior de un objeto desde la BD
     */
    private Object obtenerEstadoAnterior(String nombreServicio, Integer id) {
        try {
            // Este método debería llamar al servicio correspondiente
            // Por simplicidad, retornamos null
            // En una implementación completa, usarías reflection o un mapa de servicios
            return null;
        } catch (Exception e) {
            System.err.println("Error al obtener estado anterior: " + e.getMessage());
            return null;
        }
    }
}