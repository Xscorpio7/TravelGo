/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.travelgo.backend_travelgo.controller;


import com.travelgo.backend_travelgo.model.Administrador;
import com.travelgo.backend_travelgo.model.Credencial;
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
    @PostMapping("/register-admin")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> registerAdmin(@RequestBody CreateAdminRequest request) {
    if (credencialService.existsByCorreo(request.getCorreo())) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", "Email ya registrado"));
    }
    
    // Crear credencial
    Credencial cred = new Credencial();
    cred.setCorreo(request.getCorreo());
    cred.setContrasena(passwordEncoder.encode(request.getContrasena()));
    cred.setTipoUsuario(TipoUsuario.ADMIN);
    cred.setEstaActivo(true);
    credencialService.save(cred);
    
    // Crear administrador
    Administrador admin = new Administrador();
    admin.setCredencial(cred);
    admin.setNombre(request.getNombre());
    admin.setCargo(request.getCargo());
    administradorService.save(admin);
    
    return ResponseEntity.ok(Map.of("message", "Administrador creado"));
}
}