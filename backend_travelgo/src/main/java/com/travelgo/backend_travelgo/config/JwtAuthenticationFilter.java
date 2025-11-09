package com.travelgo.backend_travelgo.config;

import com.travelgo.backend_travelgo.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Filtro de autenticación JWT
 * Intercepta todas las peticiones y valida el token JWT en el header Authorization
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) 
            throws ServletException, IOException {
        
        try {
            // Obtener el header Authorization
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                // Validar token
                if (!jwtUtil.isTokenExpired(token)) {
                    // Extraer información del token
                    String correo = jwtUtil.extractCorreo(token);
                    Integer usuarioId = jwtUtil.extractUsuarioId(token);
                    String tipoUsuario = jwtUtil.extractTipoUsuario(token);
                    
                    // Crear autenticación
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + tipoUsuario.toUpperCase())
                    );
                    
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(correo, null, authorities);
                    
                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Establecer autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    // Agregar información adicional a los atributos de la petición
                    request.setAttribute("usuarioId", usuarioId);
                    request.setAttribute("correo", correo);
                    request.setAttribute("tipoUsuario", tipoUsuario);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error en filtro JWT: {}", e.getMessage(), e);
            // No lanzar excepción - dejar que Spring Security maneje la falta de autenticación
        }
        
        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}