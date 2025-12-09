package com.salesiana.inventory_system.service;

import com.salesiana.inventory_system.entity.Usuario;
import com.salesiana.inventory_system.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<Usuario> obtenerTodosUsuarios() {
        return usuarioRepository.findAll();
    }
    
    public List<Usuario> obtenerUsuariosActivos() {
        return usuarioRepository.findAll().stream()
                .filter(Usuario::getActivo)
                .toList();
    }
    
    public Optional<Usuario> obtenerUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id);
    }
    
    public Optional<Usuario> obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsernameAndActivoTrue(username);
    }
    
    public Optional<Usuario> obtenerUsuarioPorUsernameOrEmail(String username) {
        return usuarioRepository.findByUsernameOrEmailAndActivoTrue(username);
    }
    
    public Usuario guardarUsuario(Usuario usuario) {
        // NO encriptar contraseña - guardar en texto plano
        usuario.setFechaActualizacion(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }
    
    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        try {
            System.out.println("=== INICIANDO CREACIÓN DE USUARIO ===");
            System.out.println("Usuario: " + usuario.getUsername());
            System.out.println("Email: " + usuario.getEmail());
            System.out.println("Contraseña: " + usuario.getPasswordHash());
            
            // Validar datos requeridos
            if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre de usuario es requerido");
            }
            if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("El email es requerido");
            }
            if (usuario.getPasswordHash() == null || usuario.getPasswordHash().trim().isEmpty()) {
                throw new IllegalArgumentException("La contraseña es requerida");
            }
            if (usuario.getNombreCompleto() == null || usuario.getNombreCompleto().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre completo es requerido");
            }
            if (usuario.getRol() == null) {
                throw new IllegalArgumentException("El rol es requerido");
            }

            // NO encriptar contraseña - guardar en texto plano
            // La contraseña se guarda tal cual sin encriptación
            
            // Establecer valores por defecto
            usuario.setActivo(true);
            usuario.setFechaCreacion(LocalDateTime.now());
            usuario.setFechaActualizacion(LocalDateTime.now());
            
            // Guardar usuario
            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            
            // Forzar flush para detectar errores inmediatamente
            usuarioRepository.flush();
            
            System.out.println("✅ Usuario creado exitosamente: " + usuario.getUsername());
            
            return usuarioGuardado;
            
        } catch (DataIntegrityViolationException e) {
            System.err.println("❌ Error de integridad de datos: " + e.getMessage());
            throw new RuntimeException("Error de integridad de datos: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("❌ Error al crear usuario: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al crear usuario: " + e.getMessage(), e);
        }
    }
    
    public void eliminarUsuario(Integer id) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            usuario.setActivo(false);
            usuario.setFechaActualizacion(LocalDateTime.now());
            usuarioRepository.save(usuario);
        });
    }
    
    public boolean existeUsuarioConUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }
    
    public boolean existeUsuarioConEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    
    public void actualizarUltimoAcceso(Integer usuarioId) {
        usuarioRepository.findById(usuarioId).ifPresent(usuario -> {
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(usuario);
        });
    }
    
    public Long contarUsuariosActivos() {
        return usuarioRepository.countUsuariosActivos();
    }
    
    // Método para verificar credenciales de login
    public boolean verificarCredenciales(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsernameAndActivoTrue(username);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Comparar en texto plano
            boolean coincide = password.equals(usuario.getPasswordHash());
            System.out.println("Verificación de credenciales para " + username + ": " + coincide);
            return coincide;
        }
        return false;
    }
    
    // Método para actualizar contraseña
    public void actualizarPassword(Integer usuarioId, String nuevaPassword) {
        usuarioRepository.findById(usuarioId).ifPresent(usuario -> {
            // No encriptar, guardar en texto plano
            usuario.setPasswordHash(nuevaPassword);
            usuario.setFechaActualizacion(LocalDateTime.now());
            usuarioRepository.save(usuario);
        });
    }
}