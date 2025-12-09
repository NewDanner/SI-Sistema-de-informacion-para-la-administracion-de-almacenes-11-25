package com.salesiana.inventory_system.config;

import com.salesiana.inventory_system.security.JwtRequestFilter;
import com.salesiana.inventory_system.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("=== CONFIGURANDO PASSWORD ENCODER SIN ENCRIPTACIÓN ===");
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/static/**").permitAll()
                .requestMatchers("/login", "/error", "/secure-register/**").permitAll()
                .requestMatchers("/debug/**").permitAll()
                // ✅ PERMISOS PARA TODOS LOS MÓDULOS CORREGIDOS
                .requestMatchers("/ubicaciones/**").hasAnyRole("ADMIN", "GERENTE", "ALMACENERO")
                .requestMatchers("/control-calidad/**").hasAnyRole("ADMIN", "GERENTE")
                .requestMatchers("/transferencias-ubicacion/**").hasAnyRole("ADMIN", "GERENTE", "ALMACENERO")
                .requestMatchers("/auditoria/**").hasRole("ADMIN")
                .requestMatchers("/reportes/**").hasAnyRole("ADMIN", "GERENTE")
                .requestMatchers("/alertas/**").hasAnyRole("ADMIN", "GERENTE", "ALMACENERO")
                .requestMatchers("/mapa/**").hasAnyRole("ADMIN", "GERENTE")
                // ✅ CORRECCIÓN CRÍTICA: Permitir acceso a productos para roles autorizados
                .requestMatchers("/productos/**", "/movimientos/**").hasAnyRole("ADMIN", "GERENTE", "ALMACENERO")
                // Dashboard accesible para todos los roles autenticados
                .requestMatchers("/").hasAnyRole("ADMIN", "GERENTE", "ALMACENERO", "CONSULTOR")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    
}