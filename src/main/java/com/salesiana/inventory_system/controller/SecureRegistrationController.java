package com.salesiana.inventory_system.controller;

import com.salesiana.inventory_system.entity.Usuario;
import com.salesiana.inventory_system.entity.Rol;
import com.salesiana.inventory_system.service.UsuarioService;
import com.salesiana.inventory_system.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.support.SessionStatus;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/secure-register")
@SessionAttributes({"masterKeyVerified", "verificationTime"})
public class SecureRegistrationController {

    @Value("${app.registration.master-key:ClaveMaestra2024!}")
    private String masterKey;

    @Value("${app.registration.enabled:true}")
    private boolean registrationEnabled;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    // ✅ CORRECCIÓN: Limpiar sesión al entrar al registro
    @GetMapping
    public String showSecureRegistrationForm(Model model, SessionStatus sessionStatus) {
        if (!registrationEnabled) {
            model.addAttribute("error", "El registro está deshabilitado temporalmente por motivos de seguridad.");
            return "error/registration-disabled";
        }

        // ✅ LIMPIAR SESIÓN SIEMPRE AL ENTRAR
        sessionStatus.setComplete();
        
        // Cargar roles
        List<Rol> roles = rolService.obtenerRolesActivos();
        model.addAttribute("roles", roles);
        model.addAttribute("usuario", new Usuario());
        
        // Siempre mostrar formulario de clave maestra al inicio
        model.addAttribute("showMasterKeyForm", true);
        
        return "auth/secure-register";
    }

    @PostMapping("/verify")
    public String verifyMasterKey(
            @RequestParam("masterKeyInput") String masterKeyInput,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (!registrationEnabled) {
            redirectAttributes.addFlashAttribute("error", "El registro está deshabilitado temporalmente");
            return "redirect:/login";
        }

        // Verificación robusta de clave maestra
        if (!masterKey.trim().equals(masterKeyInput.trim())) {
            redirectAttributes.addFlashAttribute("error", 
                "❌ Clave maestra incorrecta. Verifica mayúsculas, minúsculas y caracteres especiales.");
            redirectAttributes.addFlashAttribute("showMasterKeyForm", true);
            return "redirect:/secure-register";
        }

        // ✅ Clave correcta - establecer en sesión
        model.addAttribute("masterKeyVerified", true);
        model.addAttribute("verificationTime", LocalDateTime.now());
        
        List<Rol> roles = rolService.obtenerRolesActivos();
        redirectAttributes.addFlashAttribute("roles", roles);
        redirectAttributes.addFlashAttribute("usuario", new Usuario());
        redirectAttributes.addFlashAttribute("showMasterKeyForm", false);
        redirectAttributes.addFlashAttribute("success", "✅ Clave maestra verificada correctamente");
        
        System.out.println("✅ Clave maestra verificada - Sesión establecida");
        
        return "redirect:/secure-register/form";
    }

    // ✅ NUEVO ENDPOINT: Formulario de registro (después de verificar clave)
    @GetMapping("/form")
    public String showRegistrationForm(Model model, SessionStatus sessionStatus) {
        // Verificar estado de sesión
        Boolean masterKeyVerified = (Boolean) model.getAttribute("masterKeyVerified");
        LocalDateTime verificationTime = (LocalDateTime) model.getAttribute("verificationTime");
        
        // Verificar timeout (15 minutos)
        if (verificationTime == null || LocalDateTime.now().minusMinutes(15).isAfter(verificationTime)) {
            sessionStatus.setComplete();
            return "redirect:/secure-register?error=timeout";
        }

        if (masterKeyVerified == null || !masterKeyVerified) {
            return "redirect:/secure-register";
        }

        // Cargar datos para el formulario
        List<Rol> roles = rolService.obtenerRolesActivos();
        model.addAttribute("roles", roles);
        
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new Usuario());
        }
        
        model.addAttribute("showMasterKeyForm", false);
        
        return "auth/secure-register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute Usuario usuario,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("rolId") Integer rolId,
            RedirectAttributes redirectAttributes,
            Model model,
            SessionStatus sessionStatus) {

        if (!registrationEnabled) {
            redirectAttributes.addFlashAttribute("error", "El registro está deshabilitado temporalmente");
            return "redirect:/login";
        }

        // Verificar estado de sesión
        Boolean masterKeyVerified = (Boolean) model.getAttribute("masterKeyVerified");
        LocalDateTime verificationTime = (LocalDateTime) model.getAttribute("verificationTime");
        
        // Verificar timeout (15 minutos)
        if (verificationTime == null || LocalDateTime.now().minusMinutes(15).isAfter(verificationTime)) {
            sessionStatus.setComplete();
            redirectAttributes.addFlashAttribute("error", "⏱️ La verificación ha expirado. Verifica nuevamente la clave maestra.");
            return "redirect:/secure-register";
        }

        if (masterKeyVerified == null || !masterKeyVerified) {
            redirectAttributes.addFlashAttribute("error", "Debe verificar la clave maestra primero");
            return "redirect:/secure-register";
        }

        // Validaciones de contraseña
        if (!usuario.getPasswordHash().equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "❌ Las contraseñas no coinciden");
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/secure-register/form";
        }

        if (usuario.getPasswordHash().length() < 6) {
            redirectAttributes.addFlashAttribute("error", "❌ La contraseña debe tener al menos 6 caracteres");
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/secure-register/form";
        }

        try {
            // Validar existencia de usuario
            if (usuarioService.existeUsuarioConUsername(usuario.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "❌ El nombre de usuario ya existe");
                redirectAttributes.addFlashAttribute("usuario", usuario);
                return "redirect:/secure-register/form";
            }

            if (usuarioService.existeUsuarioConEmail(usuario.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "❌ El email ya está registrado");
                redirectAttributes.addFlashAttribute("usuario", usuario);
                return "redirect:/secure-register/form";
            }

            // Obtener y asignar rol
            Rol rol = rolService.obtenerRolPorId(rolId)
                    .orElseThrow(() -> new RuntimeException("Rol no válido"));
            usuario.setRol(rol);

            // Crear usuario
            Usuario usuarioCreado = usuarioService.crearUsuario(usuario);

            System.out.println("✅ Usuario creado: " + usuarioCreado.getUsername());

            // ✅ Limpiar la sesión después del registro exitoso
            sessionStatus.setComplete();
            
            redirectAttributes.addFlashAttribute("success", 
                "✅ Usuario '" + usuario.getUsername() + "' registrado exitosamente. Ya puede iniciar sesión.");
            
            // ✅ Redirigir al login después del registro exitoso
            return "redirect:/login";

        } catch (Exception e) {
            System.err.println("❌ Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", 
                "❌ Error al registrar usuario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/secure-register/form";
        }
    }

    @GetMapping("/status")
    @ResponseBody
    public String registrationStatus() {
        return registrationEnabled ? 
            "REGISTRO HABILITADO - Clave maestra requerida" : 
            "REGISTRO DESHABILITADO";
    }
    
    // ✅ Método para cancelar y volver al login (limpiando sesión)
    @GetMapping("/cancel")
    public String cancelRegistration(SessionStatus sessionStatus, RedirectAttributes redirectAttributes) {
        sessionStatus.setComplete();
        redirectAttributes.addFlashAttribute("info", "Registro cancelado");
        return "redirect:/login";
    }
}