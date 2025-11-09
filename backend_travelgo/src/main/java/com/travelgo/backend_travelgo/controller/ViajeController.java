package com.travelgo.backend_travelgo.controller;


import com.travelgo.model.Viaje;
import com.travelgo.service.ViajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para gestionar viajes
 * SOLUCIÓN al error 415: Acepta correctamente application/json
 */
@RestController
@RequestMapping("/api/viajes")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ViajeController {

    @Autowired
    private ViajeService viajeService;

    /**
     * Crear nuevo viaje
     * ACEPTA: Content-Type: application/json
     */
    @PostMapping(
        consumes = "application/json",  // ✅ CRÍTICO: Acepta JSON
        produces = "application/json"
    )
    public ResponseEntity<Map<String, Object>> crearViaje(@RequestBody Viaje viaje) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("📥 Recibiendo viaje: " + viaje);
            
            // Validaciones básicas
            if (viaje.getOrigin() == null || viaje.getOrigin().isEmpty()) {
                response.put("error", "El origen es requerido");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (viaje.getDestinationCode() == null || viaje.getDestinationCode().isEmpty()) {
                response.put("error", "El destino es requerido");
            }
            
            if (viaje.getDepartureDate() == null) {
                response.put("error", "La fecha de salida es requerida");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Guardar viaje
            Viaje viajeGuardado = viajeService.guardarViaje(viaje);
            
            System.out.println("✅ Viaje guardado con ID: " + viajeGuardado.getId());
            
            response.put("status", "SUCCESS");
            response.put("data", viajeGuardado);
            response.put("message", "Viaje creado exitosamente");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error al crear viaje: " + e.getMessage());
            e.printStackTrace();
            
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtener todos los viajes
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<Map<String, Object>> obtenerViajes() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Viaje> viajes = viajeService.obtenerTodos();
            
            response.put("status", "SUCCESS");
            response.put("data", viajes);
            response.put("count", viajes.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtener viaje por ID
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Map<String, Object>> obtenerViajePorId(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Viaje viaje = viajeService.obtenerPorId(id);
            
            if (viaje == null) {
                response.put("status", "ERROR");
                response.put("error", "Viaje no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("status", "SUCCESS");
            response.put("data", viaje);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Actualizar viaje
     */
    @PutMapping(
        value = "/{id}",
        consumes = "application/json",
        produces = "application/json"
    )
    public ResponseEntity<Map<String, Object>> actualizarViaje(
            @PathVariable Long id,
            @RequestBody Viaje viaje) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            viaje.setId(id);
            Viaje viajeActualizado = viajeService.actualizarViaje(viaje);
            
            response.put("status", "SUCCESS");
            response.put("data", viajeActualizado);
            response.put("message", "Viaje actualizado exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Eliminar viaje
     */
    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Map<String, Object>> eliminarViaje(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            viajeService.eliminarViaje(id);
            
            response.put("status", "SUCCESS");
            response.put("message", "Viaje eliminado exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}