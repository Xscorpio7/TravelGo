package com.travelgo.backend_travelgo.controller;

import com.travelgo.backend_travelgo.model.Reserva;
import com.travelgo.backend_travelgo.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin("*")
public class ReservaController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReservaController.class);
    
    @Autowired
    private ReservaRepository reservaRepository;
    
    /**
     * Obtener todas las reservas
     * GET /api/reservas
     */
    @GetMapping
    public ResponseEntity<?> getAllReservas() {
        try {
            logger.info("Obteniendo todas las reservas");
            List<Reserva> reservas = reservaRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("data", reservas);
            response.put("count", reservas.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener reservas: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener reservas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Obtener una reserva por ID
     * GET /api/reservas/{id}
     */
    @GetMapping("/{id}")
public ResponseEntity<Map<String, Object>> getReservaById(@PathVariable Integer id) {
    Map<String, Object> response = new HashMap<>();

    try {
        logger.info("Obteniendo reserva: {}", id);

        return reservaRepository.findById(id)
                .map(reserva -> {
                    response.put("status", "SUCCESS");
                    response.put("message", "Reserva obtenida correctamente");
                    response.put("data", reserva);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("status", "ERROR");
                    response.put("message", "Reserva no encontrada");
                    response.put("data", null);
                    return ResponseEntity.status(404).body(response);
                });

    } catch (Exception e) {
        logger.error("Error al obtener reserva: {}", e.getMessage());
        response.put("status", "ERROR");
        response.put("message", "Error al obtener reserva: " + e.getMessage());
        response.put("data", null);
        return ResponseEntity.internalServerError().body(response);
    }
}
    
    /**
     * Crear una nueva reserva
     * POST /api/reservas
     */
    @PostMapping
    public ResponseEntity<?> createReserva(@RequestBody Reserva reserva) {
        try {
            logger.info("Creando nueva reserva para usuario: {}", reserva.getUsuario_id());
            
            if (reserva.getUsuario_id() == null || reserva.getViaje_id() == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Usuario ID y Viaje ID son requeridos");
                return ResponseEntity.badRequest().body(error);
            }
            
            Reserva reservaGuardada = reservaRepository.save(reserva);
            
            logger.info("Reserva creada exitosamente: {}", reservaGuardada.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Reserva creada correctamente");
            response.put("data", reservaGuardada);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al crear reserva: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear reserva: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Actualizar una reserva existente
     * PUT /api/reservas/{id}
     */
    @PutMapping("/reservas/{id}")
public ResponseEntity<Map<String, Object>> updateReserva(@PathVariable Integer id, @RequestBody Reserva reservaDetails) {
    Map<String, Object> response = new HashMap<>();

    try {
        logger.info("Actualizando reserva: {}", id);

        return reservaRepository.findById(id)
                .map(reserva -> {
                    // Actualizar campos
                    if (reservaDetails.getUsuario_id() != null) {
                        reserva.setUsuario_id(reservaDetails.getUsuario_id());
                    }
                    if (reservaDetails.getViaje_id() != null) {
                        reserva.setViaje_id(reservaDetails.getViaje_id());
                    }
                    if (reservaDetails.getAlojamiento_id() != null) {
                        reserva.setAlojamiento_id(reservaDetails.getAlojamiento_id());
                    }
                    if (reservaDetails.getTransporte_id() != null) {
                        reserva.setTransporte_id(reservaDetails.getTransporte_id());
                    }
                    if (reservaDetails.getEstado() != null) {
                        reserva.setEstado(reservaDetails.getEstado());
                    }

                    Reserva reservaActualizada = reservaRepository.save(reserva);

                    logger.info("Reserva actualizada exitosamente: {}", id);

                    response.put("status", "SUCCESS");
                    response.put("message", "Reserva actualizada correctamente");
                    response.put("data", reservaActualizada);

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("status", "ERROR");
                    response.put("message", "Reserva no encontrada");
                    response.put("data", null);
                    return ResponseEntity.status(404).body(response);
                });

    } catch (Exception e) {
        logger.error("Error al actualizar reserva: {}", e.getMessage());
        response.put("status", "ERROR");
        response.put("message", "Error al actualizar reserva: " + e.getMessage());
        response.put("data", null);
        return ResponseEntity.internalServerError().body(response);
    }
}
    
    /**
     * Eliminar una reserva
     * DELETE /api/reservas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReserva(@PathVariable Integer id) {
        try {
            logger.info("Eliminando reserva: {}", id);
            
            if (!reservaRepository.existsById(id)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Reserva no encontrada");
                return ResponseEntity.status(404).body(error);
            }
            
            reservaRepository.deleteById(id);
            
            logger.info("Reserva eliminada exitosamente: {}", id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Reserva eliminada correctamente");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al eliminar reserva: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar reserva: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Cambiar estado de una reserva
     * PUT /api/reservas/{id}/estado
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstadoReserva(@PathVariable Integer id, @RequestBody Map<String, String> estadoData) {
        try {
            logger.info("Cambiando estado de reserva: {}", id);
            
            String nuevoEstado = estadoData.get("estado");
            
            if (nuevoEstado == null || nuevoEstado.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El estado es requerido");
                return ResponseEntity.badRequest().body(error);
            }
            
            return reservaRepository.findById(id)
                    .map(reserva -> {
                        try {
                            Reserva.Estado estado = Reserva.Estado.valueOf(nuevoEstado);
                            reserva.setEstado(estado);
                            Reserva reservaActualizada = reservaRepository.save(reserva);
                            
                            logger.info("Estado de reserva actualizado a: {}", nuevoEstado);
                            
                            Map<String, Object> response = new HashMap<>();
                            response.put("status", "SUCCESS");
                            response.put("message", "Estado actualizado correctamente");
                            response.put("data", reservaActualizada);
                            
                            return ResponseEntity.ok(response);
                        } catch (IllegalArgumentException e) {
                            Map<String, String> error = new HashMap<>();
                            error.put("error", "Estado inválido. Valores permitidos: pendiente, confirmada, cancelada");
                            return ResponseEntity.badRequest().body(error);
                        }
                    })
                    .orElse(ResponseEntity.status(404).body(new HashMap<String, String>() {{
                        put("error", "Reserva no encontrada");
                    }}));
        } catch (Exception e) {
            logger.error("Error al cambiar estado: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al cambiar estado: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}