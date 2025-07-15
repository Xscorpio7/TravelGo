/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.travelgobackend.backend.controller;

import com.travelgobackend.backend.model.Credencial;
import com.travelgobackend.backend.repository.CredencialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/credenciales")
@CrossOrigin(origins = "*")
public class CredencialController {

    @Autowired
    private CredencialRepository credencialRepository;

    // Obtener todas las credenciales
    @GetMapping
    public List<Credencial> getAllCredenciales() {
        return credencialRepository.findAll();
    }

    // Crear nueva credencial
    @PostMapping
    public Credencial createCredencial(@RequestBody Credencial credencial) {
        return credencialRepository.save(credencial);
    }

    // Obtener una credencial por ID
    @GetMapping("/{id}")
    public ResponseEntity<Credencial> getCredencialById(@PathVariable int id) {
        Optional<Credencial> credencial = credencialRepository.findById(id);
        return credencial.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Actualizar credencial
    @PutMapping("/{id}")
    public ResponseEntity<Credencial> updateCredencial(@PathVariable int id, @RequestBody Credencial credencialDetails) {
        return credencialRepository.findById(id)
                .map(credencial -> {
                    credencial.setCorreo(credencialDetails.getCorreo());
                    credencial.setContrasena(credencialDetails.getContrasena());
                    credencial.setTipoUsuario(credencialDetails.getTipoUsuario());
                    credencial.setEstaActivo(credencialDetails.getEstaActivo());
                    return ResponseEntity.ok(credencialRepository.save(credencial));
                }).orElse(ResponseEntity.notFound().build());
    }

    // Eliminar credencial
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCredencial(@PathVariable int id) {
        credencialRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}