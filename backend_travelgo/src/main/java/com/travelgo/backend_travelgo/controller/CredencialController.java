package com.travelgo.backend_travelgo.controller;

import com.travelgo.backend_travelgo.model.Credencial;
import com.travelgo.backend_travelgo.repository.CredencialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import java.util.Map;


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
    public ResponseEntity<?> crearCredencial(@RequestBody Credencial credencial) {
    if (credencialRepository.existsByCorreo(credencial.getCorreo())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El correo ya está registrado.");
    }

    Credencial nueva = credencialRepository.save(credencial);
    return ResponseEntity.ok(nueva);
}   


    // Login de credencial
    @PostMapping("/login")
    public ResponseEntity<Credencial> login(@RequestBody Credencial credencial) {
        try {
            Optional<Credencial> credencialEncontrada = credencialRepository
                    .findByCorreoAndContrasena(credencial.getCorreo(), credencial.getContrasena());

            if (credencialEncontrada.isPresent()) {
                return ResponseEntity.ok(credencialEncontrada.get());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
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
    @PutMapping("/recuperar-contrasena")
public ResponseEntity<?> recuperarContrasena(@RequestBody Map<String, String> payload) {
    String correo = payload.get("correo");
    String nuevaContrasena = payload.get("nuevaContrasena");

    return credencialRepository.findByCorreo(correo)
            .map(credencial -> {
                credencial.setContrasena(nuevaContrasena); 
                credencialRepository.save(credencial);
                return ResponseEntity.ok("Contraseña actualizada con éxito");
            })
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Correo no encontrado"));
}


    // Eliminar credencial
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCredencial(@PathVariable int id) {
        credencialRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
