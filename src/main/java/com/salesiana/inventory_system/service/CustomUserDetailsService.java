package com.salesiana.inventory_system.service;

import com.salesiana.inventory_system.entity.Usuario;
import com.salesiana.inventory_system.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Buscando usuario: " + username);
        
        Usuario usuario = usuarioRepository.findByUsernameAndActivoTrue(username)
                .orElseThrow(() -> {
                    System.out.println("Usuario no encontrado: " + username);
                    return new UsernameNotFoundException("Usuario no encontrado: " + username);
                });
        
        System.out.println("Usuario encontrado: " + usuario.getUsername());
        System.out.println("Contraseña en BD: " + usuario.getPasswordHash());
        
        // Forzar la carga del rol dentro de la transacción
        if (usuario.getRol() != null) {
            usuario.getRol().getCodigo(); // Esto carga el rol
            System.out.println("Rol del usuario: " + usuario.getRol().getCodigo());
        } else {
            System.out.println("Usuario sin rol asignado");
        }
        
        return new User(usuario.getUsername(), usuario.getPasswordHash(), 
                       getAuthorities(usuario));
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        if (usuario.getRol() == null) {
            return Collections.emptyList();
        }
        
        String role = "ROLE_" + usuario.getRol().getCodigo().toUpperCase();
        System.out.println("Asignando autoridad: " + role);
        
        return Collections.singletonList(
            new SimpleGrantedAuthority(role)
        );
    }
}