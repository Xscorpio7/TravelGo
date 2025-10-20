/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.travelgo.backend_travelgo.controller;


import com.travelgo.backend_travelgo.dto.LoginRequest;
import com.travelgo.backend_travelgo.dto.LoginResponse;
import com.travelgo.backend_travelgo.model.Credencial;
import com.travelgo.backend_travelgo.model.Usuario;
import com.travelgo.backend_travelgo.repository.CredencialRepository;
import com.travelgo.backend_travelgo.repository.UsuarioRepository;
import com.travelgo.backend_travelgo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {
    
    @Autowired
    private CredencialRepository credencialRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Login endpoint
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Buscar credencial por correo
            Credencial credencial = credencialRepository.findByCorreo(request.getCorreo())
                    .orElse(null);
            
            if (credencial == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Credenciales incorrectas");
                return ResponseEntity.status(401).body(error);
            }
            
            // Verificar contraseña (en producción deberías usar BCrypt)
            if (!credencial.getContrasena().equals(request.getContrasena())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Credenciales incorrectas");
                return ResponseEntity.status(401).body(error);
            }
            
            // Verificar que la cuenta esté activa
            if (!credencial.getEstaActivo()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Cuenta desactivada");
                return ResponseEntity.status(403).body(error);
            }
            
            // Buscar usuario asociado
            Usuario usuario = usuarioRepository.findByCredencialId(credencial.getId())
                    .orElse(null);
            
            if (usuario == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Usuario no encontrado");
                return ResponseEntity.status(404).body(error);
            }
            
            // Generar token JWT
            String token = jwtUtil.generateToken(
                credencial.getCorreo(), 
                usuario.getId(), 
                credencial.getTipoUsuario().name()
            );
            
            // Crear respuesta
            LoginResponse response = new LoginResponse(
                token,
                usuario.getId(),
                credencial.getCorreo(),
                usuario.getPrimerNombre(),
                usuario.getPrimerApellido(),
                credencial.getTipoUsuario().name()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error en el servidor: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Verificar token
     * POST /api/auth/verify
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token no proporcionado");
                return ResponseEntity.status(401).body(error);
            }
            
            String token = authHeader.substring(7);
            String correo = jwtUtil.extractCorreo(token);
            Integer usuarioId = jwtUtil.extractUsuarioId(token);
            
            if (jwtUtil.isTokenExpired(token)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token expirado");
                return ResponseEntity.status(401).body(error);
            }
            
            // Buscar usuario
            Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
            
            if (usuario == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Usuario no encontrado");
                return ResponseEntity.status(404).body(error);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("usuarioId", usuarioId);
            response.put("correo", correo);
            response.put("primerNombre", usuario.getPrimerNombre());
            response.put("primerApellido", usuario.getPrimerApellido());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Token inválido");
            return ResponseEntity.status(401).body(error);
        }
    }
    
    /**
     * Obtener información del usuario actual
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token no proporcionado");
                return ResponseEntity.status(401).body(error);
            }
            
            String token = authHeader.substring(7);
            Integer usuarioId = jwtUtil.extractUsuarioId(token);
            
            if (jwtUtil.isTokenExpired(token)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token expirado");
                return ResponseEntity.status(401).body(error);
            }
            
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            return ResponseEntity.ok(usuario);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener usuario: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}