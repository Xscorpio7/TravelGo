/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.travelgo.backend_travelgo.controller;


import com.travelgo.backend_travelgo.model.Administrador;
import com.travelgo.backend_travelgo.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/administrador")
@CrossOrigin("*")
public class AdministradorController {

    @Autowired
    private AdministradorRepository administradorRepository;
    

    @GetMapping
    public List<Administrador> getAllAdministradores() {
        return administradorRepository.findAll();
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
}