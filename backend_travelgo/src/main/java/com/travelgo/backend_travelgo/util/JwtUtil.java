package com.travelgo.backend_travelgo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;  // ⭐ AGREGAR ESTE IMPORT

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component  // ⭐ AGREGAR ESTA ANOTACIÓN
public class JwtUtil {
    // Clave secreta para firmar el JWT (en producción debe estar en variables de entorno)
    private static final String SECRET_KEY = "TravelGoSecretKeyForJWT2024MustBe256BitsLongForHS256Algorithm";
    private static final long EXPIRATION_TIME = 86400000; // 24 horas en milisegundos
    
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
    
    /**
     * Generar token JWT
     */
    public String generateToken(String correo, Integer usuarioId, String tipoUsuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("usuarioId", usuarioId);
        claims.put("tipoUsuario", tipoUsuario);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(correo)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Extraer correo del token
     */
    public String extractCorreo(String token) {
        return extractAllClaims(token).getSubject();
    }
    
    /**
     * Extraer usuario ID del token
     */
    public Integer extractUsuarioId(String token) {
        return extractAllClaims(token).get("usuarioId", Integer.class);
    }
    
    /**
     * Extraer tipo de usuario del token
     */
    public String extractTipoUsuario(String token) {
        return extractAllClaims(token).get("tipoUsuario", String.class);
    }
    
    /**
     * Verificar si el token ha expirado
     */
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
    
    /**
     * Validar token
     */
    public boolean validateToken(String token, String correo) {
        final String tokenCorreo = extractCorreo(token);
        return (tokenCorreo.equals(correo) && !isTokenExpired(token));
    }
    
    /**
     * Extraer todos los claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}