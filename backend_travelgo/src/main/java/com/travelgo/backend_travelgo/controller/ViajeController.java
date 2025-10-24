package com.travelgo.backend_travelgo.controller;

import com.travelgo.backend_travelgo.model.Viaje;
import com.travelgo.backend_travelgo.repository.ViajeRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/viajes")
@CrossOrigin(origins = "*")
public class ViajeController {
    
    private static final Logger logger = LoggerFactory.getLogger(ViajeController.class);
    
    @Autowired
    private ViajeRepository viajeRepository;
    
    /**
     * Obtener todos los viajes
     * GET /api/viajes
     */
    @GetMapping
    public ResponseEntity<?> getAllViajes() {
        try {
            logger.info("Obteniendo todos los viajes");
            List<Viaje> viajes = viajeRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("data", viajes);
            response.put("count", viajes.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener viajes: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener viajes: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Obtener un viaje por ID
     * GET /api/viajes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getViajeById(@PathVariable Integer id) {
        try {
            logger.info("Obteniendo viaje: {}", id);
            Optional<Viaje> viaje = viajeRepository.findById(id);
            
            if (viaje.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Viaje no encontrado");
                return ResponseEntity.status(404).body(error);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("data", viaje.get());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener viaje: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener viaje: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Crear un nuevo viaje
     * POST /api/viajes
     */
    @PostMapping
    public ResponseEntity<?> createViaje(@RequestBody Viaje viajeDetails) {
        try {
            logger.info("Creando nuevo viaje de vuelo: {} -> {}", viajeDetails.getOrigin(), viajeDetails.getDestination());
            
            if (viajeDetails.getOrigin() == null || viajeDetails.getOrigin().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El origen es requerido");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (viajeDetails.getDestination() == null || viajeDetails.getDestination().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El destino es requerido");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (viajeDetails.getPrecio() == null || viajeDetails.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "El precio debe ser mayor a 0");
    return ResponseEntity.badRequest().body(error);
}

            
            Viaje viajeGuardado = viajeRepository.save(viajeDetails);
            
            logger.info("Viaje creado exitosamente: {}", viajeGuardado.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Viaje creado correctamente");
            response.put("data", viajeGuardado);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al crear viaje: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear viaje: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Actualizar un viaje existente
     * PUT /api/viajes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateViaje(@PathVariable Integer id, @RequestBody Viaje viajeDetails) {
        try {
            logger.info("Actualizando viaje: {}", id);
            
            Optional<Viaje> optionalViaje = viajeRepository.findById(id);
            
            if (optionalViaje.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Viaje no encontrado");
                return ResponseEntity.status(404).body(error);
            }
            
            Viaje viaje = optionalViaje.get();
            
            // Actualizar campos de vuelo
            if (viajeDetails.getFlightOfferId() != null) {
                viaje.setFlightOfferId(viajeDetails.getFlightOfferId());
            }
            if (viajeDetails.getOrigin() != null) {
                viaje.setOrigin(viajeDetails.getOrigin());
            }
            if (viajeDetails.getDestination() != null) {
                viaje.setDestination(viajeDetails.getDestination());
            }
            if (viajeDetails.getDepartureDate() != null) {
                viaje.setDepartureDate(viajeDetails.getDepartureDate());
            }
            if (viajeDetails.getReturnDate() != null) {
                viaje.setReturnDate(viajeDetails.getReturnDate());
            }
            if (viajeDetails.getPrecio()!= null && viajeDetails.getPrecio().compareTo(BigDecimal.ZERO) > 0) {
    viaje.setPrecio(viajeDetails.getPrecio());
}
            if (viajeDetails.getCurrency() != null) {
                viaje.setCurrency(viajeDetails.getCurrency());
            }
            if (viajeDetails.getAirline() != null) {
                viaje.setAirline(viajeDetails.getAirline());
            }
            if (viajeDetails.getFlightDetails() != null) {
                viaje.setFlightDetails(viajeDetails.getFlightDetails());
            }
            
            Viaje viajeActualizado = viajeRepository.save(viaje);
            
            logger.info("Viaje actualizado exitosamente: {}", id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Viaje actualizado correctamente");
            response.put("data", viajeActualizado);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al actualizar viaje: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar viaje: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Eliminar un viaje por ID
     * DELETE /api/viajes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteViaje(@PathVariable Integer id) {
        try {
            logger.info("Eliminando viaje: {}", id);
            
            if (!viajeRepository.existsById(id)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Viaje no encontrado");
                return ResponseEntity.status(404).body(error);
            }
            
            viajeRepository.deleteById(id);
            
            logger.info("Viaje eliminado exitosamente: {}", id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Viaje eliminado correctamente");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al eliminar viaje: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar viaje: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Buscar viajes por origen y destino
     * GET /api/viajes/search?origin=MAD&destination=ATH
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchViajes(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination) {
        try {
            logger.info("Buscando viajes: origin={}, destination={}", origin, destination);
            
            List<Viaje> viajes;
            
            if (origin != null && destination != null) {
                viajes = viajeRepository.findByOriginAndDestination(origin, destination);
            } else {
                viajes = viajeRepository.findAll();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("data", viajes);
            response.put("count", viajes.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al buscar viajes: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al buscar viajes: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}