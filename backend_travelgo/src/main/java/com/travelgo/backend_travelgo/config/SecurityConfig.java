package com.travelgo.backend_travelgo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad para JWT
 * ✅ CORREGIDO: Endpoints de búsqueda son PÚBLICOS
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
                // ✅ ENDPOINTS PÚBLICOS - Autenticación
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/login").permitAll()
                .requestMatchers("/api/usuarios", "/api/usuarios/**").permitAll()
                
                // ✅ CRÍTICO: ENDPOINTS PÚBLICOS - Búsqueda (SIN AUTENTICACIÓN)
                .requestMatchers(HttpMethod.GET, "/flights/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/hotels/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/transporte/search-transfers").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/transporte/disponibles").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/transporte/por-tipo").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/transporte/buscar").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/transporte/example").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/transporte/test").permitAll()
                
                // ✅ ENDPOINTS PROTEGIDOS - Reservas (REQUIEREN AUTENTICACIÓN)
                .requestMatchers("/api/viajes/**").authenticated()
                .requestMatchers("/api/reservas/**").authenticated()
                .requestMatchers("/api/pago/**").authenticated()
                .requestMatchers("/api/bookings/**").authenticated()
                
                // ✅ ENDPOINTS PROTEGIDOS - Transporte (acciones que modifican datos)
                .requestMatchers(HttpMethod.POST, "/api/transporte").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/transporte/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/transporte/**").authenticated()
                
                // ✅ ADMIN - Solo administradores
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/administrador/**").hasAuthority("ROLE_ADMIN")
                
                // Por defecto, todo requiere autenticación
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}