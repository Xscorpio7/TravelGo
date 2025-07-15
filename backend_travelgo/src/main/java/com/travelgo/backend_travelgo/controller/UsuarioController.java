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
                    req.nombre_completo,
                    req.telefono,
                    req.nacionalidad,
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
                    usuario.setNombreCompleto(usuarioDetails.getNombreCompleto());
                    usuario.setTelefono(usuarioDetails.getTelefono());
                    usuario.setNacionalidad(usuarioDetails.getNacionalidad());
                    usuario.setFechaNacimiento(usuarioDetails.getFechaNacimiento());
                    usuario.setGenero(usuarioDetails.getGenero());
                    return ResponseEntity.ok(usuarioRepository.save(usuario));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable int id) {
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}