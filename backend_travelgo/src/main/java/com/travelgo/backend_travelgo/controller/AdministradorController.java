package com.travelgo.backend_travelgo.controller;

import com.travelgo.backend_travelgo.dto.CreateAdminRequest;
import com.travelgo.backend_travelgo.model.Administrador;
import com.travelgo.backend_travelgo.model.Credencial;
import com.travelgo.backend_travelgo.repository.AdministradorRepository;
import com.travelgo.backend_travelgo.service.AdministradorService;
import com.travelgo.backend_travelgo.service.CredencialService;
import com.travelgo.backend_travelgo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/administrador")
@CrossOrigin("*")
public class AdministradorController {

    @Autowired
    private AdministradorRepository administradorRepository;
    
    @Autowired
    private AdministradorService administradorService;
    
    @Autowired
    private CredencialService credencialService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    /**
     * Obtener todos los administradores (solo para admin)
     */
    @GetMapping
    public ResponseEntity<?> getAllAdministradores(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Verificar autenticación
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Token no proporcionado"));
            }
            
            String token = authHeader.substring(7);
            
            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token expirado"));
            }
            
            // Verificar que sea admin
            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"admin".equalsIgnoreCase(tipoUsuario)) {
                return ResponseEntity.status(403).body(Map.of("error", "Acceso denegado. Solo administradores"));
            }
            
            return ResponseEntity.ok(administradorRepository.findAll());
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al obtener administradores"));
        }
    }

    @PostMapping
    public Administrador creatorAdministrador(@RequestBody Administrador administrador) {
        return administradorRepository.save(administrador);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Administrador> updateUsuario(@PathVariable int id, @RequestBody Administrador administradorDetails) {
        return administradorRepository.findById(id)
                .map(administrador -> {
                    administrador.setNombre(administradorDetails.getNombre());
                    administrador.setCargo(administradorDetails.getCargo());
                    return ResponseEntity.ok(administradorRepository.save(administrador));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdministrador(@PathVariable int id) {
        administradorRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Registrar nuevo administrador (solo admin puede hacerlo)
     * POST /api/administrador/register-admin
     */
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(
            @RequestBody CreateAdminRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Verificar autenticación
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Token no proporcionado"));
            }
            
            String token = authHeader.substring(7);
            
            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token expirado"));
            }
            
            // Verificar que sea admin
            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"admin".equalsIgnoreCase(tipoUsuario)) {
                return ResponseEntity.status(403).body(Map.of("error", "Acceso denegado. Solo administradores"));
            }
            
            // Verificar si el correo ya existe
            if (credencialService.existsByCorreo(request.getCorreo())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email ya registrado"));
            }
            
            // Crear credencial con contraseña encriptada
            Credencial cred = new Credencial();
            cred.setCorreo(request.getCorreo());
            cred.setContrasena(passwordEncoder.encode(request.getContrasena())); // ✅ ENCRIPTADO
            cred.setTipoUsuario(Credencial.TipoUsuario.admin);
            cred.setEstaActivo(true);
            credencialService.save(cred);
            
            // Crear administrador
            Administrador admin = new Administrador();
            admin.setCredencial(cred);
            admin.setNombre(request.getNombre());
            admin.setCargo(request.getCargo());
            administradorService.save(admin);
            
            return ResponseEntity.ok(Map.of("message", "Administrador creado exitosamente"));
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Error al crear administrador: " + e.getMessage()));
        }
    }
}