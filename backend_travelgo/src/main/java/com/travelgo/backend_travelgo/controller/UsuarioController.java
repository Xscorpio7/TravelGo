package com.travelgo.backend_travelgo.controller;

import com.travelgo.backend_travelgo.dto.RegistroRequest;
import com.travelgo.backend_travelgo.dto.CambiarContrasenaRequest;
import com.travelgo.backend_travelgo.exception.ResourceNotFoundException;
import com.travelgo.backend_travelgo.model.Credencial;
import com.travelgo.backend_travelgo.model.Usuario;
import com.travelgo.backend_travelgo.repository.CredencialRepository;
import com.travelgo.backend_travelgo.repository.UsuarioRepository;
import com.travelgo.backend_travelgo.service.UsuarioService;
import com.travelgo.backend_travelgo.service.CredencialService;
import com.travelgo.backend_travelgo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private CredencialRepository credencialRepository;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private CredencialService credencialService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    /**
     * Obtener todos los usuarios (solo admin)
     * GET /api/usuarios
     */
    @GetMapping
    public ResponseEntity<?> getAllUsuarios(@RequestHeader(value = "Authorization", required = false) String authHeader) {
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
            
            return ResponseEntity.ok(usuarioService.findAll());
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al obtener usuarios"));
        }
    }
    
    /**
     * Obtener usuario por ID
     * GET /api/usuarios/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable int id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Registrar nuevo usuario
     * POST /api/usuarios
     */
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest req) {
        try {
            if (credencialRepository.existsByCorreo(req.correo)) {
                return ResponseEntity.badRequest().body("Correo ya registrado");
            }

            // Guardar la credencial primero con contraseña encriptada
            Credencial credencial = new Credencial();
            credencial.setCorreo(req.correo);
            credencial.setContrasena(passwordEncoder.encode(req.contrasena)); // ✅ ENCRIPTADO
            credencial.setTipoUsuario(Credencial.TipoUsuario.usuario);
            credencial.setEstaActivo(true);
            credencialRepository.save(credencial);

            // Crear el usuario
            Usuario usuario = new Usuario(
                req.primerNombre,
                req.primerApellido,
                req.telefono,
                Usuario.Nacionalidad.valueOf(req.nacionalidad),
                LocalDate.parse(req.fecha_nacimiento),
                Usuario.Genero.valueOf(req.genero) 
            );
            usuario.setCredencial(credencial);

            usuarioRepository.save(usuario);

            return ResponseEntity.ok("Usuario registrado correctamente");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Actualizar usuario
     * PUT /api/usuarios/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable int id, @RequestBody Usuario usuarioDetails) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setPrimerNombre(usuarioDetails.getPrimerNombre());
                    usuario.setPrimerApellido(usuarioDetails.getPrimerApellido());
                    usuario.setTelefono(usuarioDetails.getTelefono());
                    usuario.setNacionalidad(usuarioDetails.getNacionalidad());
                    usuario.setFechaNacimiento(usuarioDetails.getFechaNacimiento());
                    usuario.setGenero(usuarioDetails.getGenero());
                    return ResponseEntity.ok(usuarioRepository.save(usuario));
                }).orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Cambiar contraseña
     * PUT /api/usuarios/cambiar-contrasena/{id}
     */
    @PutMapping("/cambiar-contrasena/{id}")
    public ResponseEntity<?> cambiarContrasena(@PathVariable int id, @RequestBody CambiarContrasenaRequest req) {
        return credencialRepository.findById(id)
                .map(credencial -> {
                    credencial.setContrasena(passwordEncoder.encode(req.getNuevaContrasena())); // ✅ ENCRIPTADO
                    credencialRepository.save(credencial);
                    return ResponseEntity.ok("Contraseña actualizada con éxito");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Eliminar usuario
     * DELETE /api/usuarios/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable int id) {
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Activar/Desactivar usuario (solo admin)
     * PUT /api/usuarios/{id}/toggle-status
     */
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(
            @PathVariable Integer id,
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
            
            Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            
            Credencial cred = usuario.getCredencial();
            cred.setEstaActivo(!cred.getEstaActivo());
            credencialService.save(cred);
            
            return ResponseEntity.ok(Map.of("message", "Estado actualizado"));
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al actualizar estado"));
        }
    }
    
    /**
     * Promover usuario a admin (solo admin)
     * PUT /api/usuarios/{id}/promote-to-admin
     */
    @PutMapping("/{id}/promote-to-admin")
    public ResponseEntity<?> promoteToAdmin(
            @PathVariable Integer id,
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
            
            Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            
            usuario.getCredencial().setTipoUsuario(Credencial.TipoUsuario.admin);
            usuarioService.save(usuario);
            
            return ResponseEntity.ok(Map.of("message", "Usuario promovido a administrador"));
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al promover usuario"));
        }
    }
    
    /**
     * Degradar admin a usuario (solo admin)
     * PUT /api/usuarios/{id}/demote-to-user
     */
    @PutMapping("/{id}/demote-to-user")
    public ResponseEntity<?> demoteToUser(
            @PathVariable Integer id,
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
            
            Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            
            usuario.getCredencial().setTipoUsuario(Credencial.TipoUsuario.usuario);
            usuarioService.save(usuario);
            
            return ResponseEntity.ok(Map.of("message", "Admin degradado a usuario"));
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al degradar administrador"));
        }
    }
}