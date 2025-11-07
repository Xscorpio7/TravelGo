package com.travelgo.backend_travelgo.controller;

import com.travelgo.backend_travelgo.dto.LoginRequest;
import com.travelgo.backend_travelgo.model.Credencial;
import com.travelgo.backend_travelgo.repository.CredencialRepository;
import com.travelgo.backend_travelgo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminAuthController {
    
    @Autowired
    private CredencialRepository credencialRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Login de administrador
     * POST /api/admin/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest request) {
        try {
            System.out.println("🔐 Intento de login admin: " + request.getCorreo());
            
            // Buscar credencial por correo
            Credencial credencial = credencialRepository.findByCorreo(request.getCorreo())
                    .orElse(null);
            
            if (credencial == null) {
                System.out.println("❌ Credencial no encontrada: " + request.getCorreo());
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Credenciales incorrectas"));
            }
            
            // Verificar contraseña
            if (!passwordEncoder.matches(request.getContrasena(), credencial.getContrasena())) {
                System.out.println("❌ Contraseña incorrecta");
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Credenciales incorrectas"));
            }
            
            // Verificar que sea admin
            if (credencial.getTipoUsuario() != Credencial.TipoUsuario.admin) {
                System.out.println("❌ Usuario no es admin: " + credencial.getTipoUsuario());
                return ResponseEntity.status(403)
                        .body(Map.of("error", "Acceso denegado. Solo administradores"));
            }
            
            // Verificar que esté activo
            if (!credencial.getEstaActivo()) {
                System.out.println("❌ Cuenta desactivada");
                return ResponseEntity.status(403)
                        .body(Map.of("error", "Cuenta desactivada"));
            }
            
            // Generar token JWT
            String token = jwtUtil.generateToken(
                credencial.getCorreo(), 
                credencial.getId(), 
                credencial.getTipoUsuario().name()
            );
            
            System.out.println("✅ Login admin exitoso: " + credencial.getCorreo());
            
            // Respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("usuarioId", credencial.getId());
            response.put("correo", credencial.getCorreo());
            response.put("tipoUsuario", credencial.getTipoUsuario().name());
            response.put("primerNombre", "Admin"); // Puedes mejorar esto buscando en Administrador
            response.put("primerApellido", "");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error en login admin: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error en el servidor: " + e.getMessage()));
        }
    }
    
    /**
     * Verificar token de admin
     * POST /api/admin/verify
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyAdminToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Token no proporcionado"));
            }
            
            String token = authHeader.substring(7);
            
            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Token expirado"));
            }
            
            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"admin".equalsIgnoreCase(tipoUsuario)) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", "Acceso denegado. Solo administradores"));
            }
            
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "tipoUsuario", tipoUsuario
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Token inválido"));
        }
    }
}