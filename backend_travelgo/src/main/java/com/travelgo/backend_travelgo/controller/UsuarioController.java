/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.travelgo.backend_travelgo.controller;

import com.travelgo.backend_travelgo.dto.RegistroRequest;
import com.travelgo.backend_travelgo.model.Credencial;
import com.travelgo.backend_travelgo.model.Usuario;
import com.travelgo.backend_travelgo.repository.CredencialRepository;
import com.travelgo.backend_travelgo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.travelgo.backend_travelgo.dto.CambiarContrasenaRequest;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private CredencialRepository credencialRepository;

    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable int id) {
    return usuarioRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest req) {
        try {
            if (credencialRepository.existsByCorreo(req.correo)) {
                return ResponseEntity.badRequest().body("Correo ya registrado");
            }

            // Guardar la credencial primero
            Credencial credencial = new Credencial();
            credencial.setCorreo(req.correo);
            credencial.setContrasena(req.contrasena);
            credencial.setTipoUsuario(Credencial.TipoUsuario.usuario);
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
    
    @PutMapping("/cambiar-contrasena/{id}")
public ResponseEntity<?> cambiarContrasena(@PathVariable int id, @RequestBody CambiarContrasenaRequest req) {
    return credencialRepository.findById(id)
            .map(credencial -> {
                credencial.setContrasena(req.getNuevaContrasena());
                credencialRepository.save(credencial);
                return ResponseEntity.ok("Contraseña actualizada con éxito");
            })
            .orElse(ResponseEntity.notFound().build());
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable int id) {
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<Usuario>> getAllUsuarios() {
    return ResponseEntity.ok(usuarioService.findAll());
}
@PutMapping("/{id}/toggle-status")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
    Usuario usuario = usuarioService.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    
    Credencial cred = usuario.getCredencial();
    cred.setEstaActivo(!cred.getEstaActivo());
    credencialService.save(cred);
    
    return ResponseEntity.ok(Map.of("message", "Estado actualizado"));
}
@PutMapping("/{id}/promote-to-admin")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> promoteToAdmin(@PathVariable Long id) {
    Usuario usuario = usuarioService.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    
    usuario.getCredencial().setTipoUsuario(TipoUsuario.ADMIN);
    usuarioService.save(usuario);
    
    return ResponseEntity.ok(Map.of("message", "Usuario promovido"));
}
@PutMapping("/{id}/demote-to-user")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> demoteToUser(@PathVariable Long id) {
    Usuario usuario = usuarioService.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    
    usuario.getCredencial().setTipoUsuario(TipoUsuario.USUARIO);
    usuarioService.save(usuario);
    
    return ResponseEntity.ok(Map.of("message", "Admin degradado"));
}

}