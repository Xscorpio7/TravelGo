/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.travelgobackend.backend.controller;

import com.travelgobackend.backend.model.Usuario;
import com.travelgobackend.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @PostMapping
    public Usuario createUsuario(@RequestBody Usuario usuario) {
        return usuarioRepository.save(usuario);
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