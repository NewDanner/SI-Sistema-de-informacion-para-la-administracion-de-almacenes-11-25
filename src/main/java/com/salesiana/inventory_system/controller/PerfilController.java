package com.salesiana.inventory_system.controller;

import com.salesiana.inventory_system.entity.Usuario;
import com.salesiana.inventory_system.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtener el usuario actual desde el contexto de seguridad
     */
    private Usuario obtenerUsuarioActual() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                String username = auth.getName();
                return usuarioService.obtenerUsuarioPorUsername(username).orElse(null);
            }
        } catch (Exception e) {
            System.err.println("❌ Error al obtener usuario actual: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Ver perfil del usuario actual
     */
    @GetMapping
    public String verPerfil(Model model) {
        try {
            System.out.println("=== CARGANDO PERFIL DE USUARIO ===");
            
            Usuario usuario = obtenerUsuarioActual();
            
            if (usuario == null) {
                System.out.println("❌ No se pudo obtener el usuario actual");
                return "redirect:/login?error=session";
            }

            model.addAttribute("usuario", usuario);
            System.out.println("✅ Perfil cargado para: " + usuario.getUsername());
            
            return "perfil/index";
        } catch (Exception e) {
            System.err.println("❌ Error al cargar perfil: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/dashboard";
        }
    }

    /**
     * Mostrar formulario para editar perfil
     */
    @GetMapping("/editar")
    public String mostrarFormularioEditar(Model model) {
        try {
            System.out.println("=== CARGANDO FORMULARIO EDITAR PERFIL ===");
            
            Usuario usuario = obtenerUsuarioActual();
            
            if (usuario == null) {
                return "redirect:/login?error=session";
            }

            model.addAttribute("usuario", usuario);
            System.out.println("✅ Formulario de edición cargado");
            
            return "perfil/editar";
        } catch (Exception e) {
            System.err.println("❌ Error al cargar formulario de edición: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/perfil";
        }
    }

    /**
     * Actualizar datos del perfil
     */
    @PostMapping("/actualizar")
    public String actualizarPerfil(
            @RequestParam String nombreCompleto,
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== ACTUALIZANDO PERFIL ===");
            
            Usuario usuario = obtenerUsuarioActual();
            
            if (usuario == null) {
                return "redirect:/login?error=session";
            }

            // Validaciones
            if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El nombre completo es requerido");
                return "redirect:/perfil/editar";
            }

            if (email == null || email.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El email es requerido");
                return "redirect:/perfil/editar";
            }

            // Verificar si el email ya está en uso por otro usuario
            Usuario usuarioConEmail = usuarioService.obtenerUsuarioPorUsername(email).orElse(null);
            if (usuarioConEmail != null && !usuarioConEmail.getId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("error", "El email ya está en uso por otro usuario");
                return "redirect:/perfil/editar";
            }

            // Actualizar datos
            usuario.setNombreCompleto(nombreCompleto.trim());
            usuario.setEmail(email.trim());

            usuarioService.guardarUsuario(usuario);

            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
            System.out.println("✅ Perfil actualizado exitosamente");

            return "redirect:/perfil";
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar perfil: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
            return "redirect:/perfil/editar";
        }
    }

    /**
     * Mostrar formulario para cambiar contraseña
     */
    @GetMapping("/cambiar-password")
    public String mostrarFormularioCambiarPassword(Model model) {
        try {
            System.out.println("=== CARGANDO FORMULARIO CAMBIAR CONTRASEÑA ===");
            
            Usuario usuario = obtenerUsuarioActual();
            
            if (usuario == null) {
                return "redirect:/login?error=session";
            }

            model.addAttribute("usuario", usuario);
            return "perfil/cambiar-password";
        } catch (Exception e) {
            System.err.println("❌ Error al cargar formulario cambiar contraseña: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/perfil";
        }
    }

    /**
     * Cambiar contraseña
     */
    @PostMapping("/cambiar-password")
    public String cambiarPassword(
            @RequestParam String passwordActual,
            @RequestParam String passwordNueva,
            @RequestParam String passwordConfirmacion,
            RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== CAMBIANDO CONTRASEÑA ===");
            
            Usuario usuario = obtenerUsuarioActual();
            
            if (usuario == null) {
                return "redirect:/login?error=session";
            }

            // Validaciones
            if (passwordActual == null || passwordActual.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "La contraseña actual es requerida");
                return "redirect:/perfil/cambiar-password";
            }

            if (passwordNueva == null || passwordNueva.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "La contraseña nueva es requerida");
                return "redirect:/perfil/cambiar-password";
            }

            if (passwordNueva.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                return "redirect:/perfil/cambiar-password";
            }

            if (!passwordNueva.equals(passwordConfirmacion)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
                return "redirect:/perfil/cambiar-password";
            }

            // Verificar contraseña actual (NOTA: Tu sistema guarda en texto plano)
            if (!usuario.getPasswordHash().equals(passwordActual)) {
                redirectAttributes.addFlashAttribute("error", "La contraseña actual es incorrecta");
                return "redirect:/perfil/cambiar-password";
            }

            // Actualizar contraseña
            usuario.setPasswordHash(passwordNueva);
            usuarioService.guardarUsuario(usuario);

            redirectAttributes.addFlashAttribute("success", "Contraseña actualizada correctamente");
            System.out.println("✅ Contraseña actualizada exitosamente");

            return "redirect:/perfil";
        } catch (Exception e) {
            System.err.println("❌ Error al cambiar contraseña: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cambiar la contraseña: " + e.getMessage());
            return "redirect:/perfil/cambiar-password";
        }
    }
}