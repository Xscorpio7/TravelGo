package com.travelgo.backend_travelgo.controller;

import com.travelgo.backend_travelgo.model.Alojamiento;
import com.travelgo.backend_travelgo.model.Pago;
import com.travelgo.backend_travelgo.model.Reserva;
import com.travelgo.backend_travelgo.model.Usuario;
import com.travelgo.backend_travelgo.model.Viaje;
import com.travelgo.backend_travelgo.repository.ReservaRepository;
import com.travelgo.backend_travelgo.service.ReservaService;
import com.travelgo.backend_travelgo.util.JwtUtil;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin("*")
public class ReservaController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReservaController.class);
    
    @Autowired
    private ReservaRepository reservaRepository;
    
    @Autowired
    private ReservaService reservaService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Obtener todas las reservas (solo para admin)
     * GET /api/reservas
     */
    @GetMapping
    public ResponseEntity<?> getAllReservas(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Verificar autenticación
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Token no proporcionado"));
            }
            
            String token = authHeader.substring(7);
            
            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token expirado"));
            }
            
            // Verificar que sea admin
            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            if (!"admin".equalsIgnoreCase(tipoUsuario)) {
                return ResponseEntity.status(403).body(Map.of("error", "Acceso denegado. Solo administradores"));
            }
            
            List<Reserva> reservas = reservaService.findAll();
            return ResponseEntity.ok(reservas);
            
        } catch (Exception e) {
            logger.error("Error al obtener reservas: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Error al obtener reservas"));
        }
    }
    
    /**
     * ✅ CORREGIDO - Obtener reservas por usuario
     * GET /api/reservas/usuario/{usuarioId}
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> getReservasByUsuario(
            @PathVariable Integer usuarioId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Verificar autenticación
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Token no proporcionado"));
            }
            
            String token = authHeader.substring(7);
            
            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token expirado"));
            }
            
            // Verificar que el usuario autenticado es el mismo o es admin
            Integer tokenUsuarioId = jwtUtil.extractUsuarioId(token);
            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            
            if (!tokenUsuarioId.equals(usuarioId) && !"admin".equalsIgnoreCase(tipoUsuario)) {
                return ResponseEntity.status(403).body(Map.of("error", "No tienes permiso para ver estas reservas"));
            }
            
            logger.info("📋 Obteniendo reservas para usuario: {}", usuarioId);
            
            // ✅ USAR EL MÉTODO CORRECTO
            List<Reserva> reservas = reservaService.findByUsuarioId(usuarioId);
            
            logger.info("✅ Encontradas {} reservas para usuario {}", reservas.size(), usuarioId);
            
            return ResponseEntity.ok(reservas);
            
        } catch (Exception e) {
            logger.error("❌ Error al obtener reservas del usuario {}: {}", usuarioId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener reservas: " + e.getMessage()));
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
            logger.info("Creando nueva reserva para usuario: {}", reserva.getUsuarioId());
            
            if (reserva.getUsuarioId() == null || reserva.getViajeId() == null) {
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
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateReserva(@PathVariable Integer id, @RequestBody Reserva reservaDetails) {
        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Actualizando reserva: {}", id);

            return reservaRepository.findById(id)
                    .map(reserva -> {
                        // Actualizar campos
                        if (reservaDetails.getUsuarioId() != null) {
                            reserva.setUsuarioId(reservaDetails.getUsuarioId());
                        }
                        if (reservaDetails.getViajeId() != null) {
                            reserva.setViajeId(reservaDetails.getViajeId());
                        }
                        if (reservaDetails.getAlojamientoId() != null) {
                            reserva.setAlojamientoId(reservaDetails.getAlojamientoId());
                        }
                        if (reservaDetails.getTransporteId() != null) {
                            reserva.setTransporteId(reservaDetails.getTransporteId());
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
    @PostMapping("/crear-completa")
public ResponseEntity<?> crearReservaCompleta(@RequestBody Map<String, Object> payload) {
    try {
        System.out.println("📦 Recibiendo payload completo de reserva...");
        
        // Extraer datos del payload
        Integer usuarioId = (Integer) payload.get("usuarioId");
        Map<String, Object> viajeData = (Map<String, Object>) payload.get("viajeData");
        Integer alojamientoId = (Integer) payload.get("alojamientoId");
        Integer transporteId = (Integer) payload.get("transporteId");
        Map<String, Object> pagoData = (Map<String, Object>) payload.get("pagoData");
        Map<String, Object> pasajeroData = (Map<String, Object>) payload.get("pasajeroData");
        
        // 1. Crear el viaje
        System.out.println("✈️ Creando viaje...");
        Viaje viaje = new Viaje();
        viaje.setFlightOfferId((String) viajeData.get("flightOfferId"));
        viaje.setOrigin((String) viajeData.get("origin"));
        viaje.setDestinationCode((String) viajeData.get("destinationCode"));
        viaje.setDepartureDate((String) viajeData.get("departureDate"));
        viaje.setReturnDate((String) viajeData.get("returnDate"));
        viaje.setPrecio(Double.parseDouble(viajeData.get("precio").toString()));
        viaje.setCurrency((String) viajeData.get("currency"));
        viaje.setAirline((String) viajeData.get("airline"));
        viaje.setBookableSeats((Integer) viajeData.get("bookableSeats"));
        viaje.setTipoViaje((String) viajeData.get("tipoViaje"));
        viaje.setTitulo((String) viajeData.get("titulo"));
        
        Viaje ViajeGuardado =viajeService.guardarViaje(viaje);
        System.out.println("✅ Viaje guardado con ID: " + viajeGuardado.getId());
        
        // 2. Crear la reserva
        System.out.println("📋 Creando reserva...");
        Reserva reserva = new Reserva();
        
        Usuario usuario = usuarioService.obtenerUsuarioPorId(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        reserva.setUsuario(usuario);
        
        reserva.setViaje(viajeGuardado);
        
        if (alojamientoId != null) {
            Alojamiento alojamiento = alojamientoService.obtenerAlojamientoPorId(alojamientoId)
                .orElse(null);
            reserva.setAlojamiento(alojamiento);
        }
        
        if (transporteId != null) {
            Transporte transporte = transporteService.obtenerTransportePorId(transporteId)
                .orElse(null);
            reserva.setTransporte(transporte);
        }
        
        reserva.setEstado("confirmada");
        reserva.setFechaReserva(LocalDate.now());
        
        Reserva ReservaGuardada = reservaService.crearReserva(reserva);
        System.out.println("✅ Reserva guardada con ID: " + reservaGuardada.getId());
        
        // 3. Crear el pago
        System.out.println("💰 Registrando pago...");
        Pago pago = new Pago();
        pago.setReserva(reservaGuardada);
        pago.setMetodoPago((String) pagoData.get("metodoPago"));
        pago.setMonto(Double.parseDouble(pagoData.get("monto").toString()));
        pago.setEstado((String) pagoData.get("estado"));
        pago.setFechaPago(LocalDate.parse((String) pagoData.get("fechaPago")));
        
        Pago pagoGuardado = pagoService.guardarPago(pago);
        System.out.println("✅ Pago registrado con ID: " + pagoGuardado.getId());
        
        // 4. Preparar respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("reservaId", reservaGuardada.getId());
        response.put("viajeId", viajeGuardado.getId());
        response.put("pagoId", pagoGuardado.getId());
        response.put("estado", "confirmada");
        response.put("mensaje", "Reserva creada exitosamente");
        
        Map<String, Object> finalResponse = new HashMap<>();
        finalResponse.put("status", "SUCCESS");
        finalResponse.put("message", "Reserva completa creada exitosamente");
        finalResponse.put("data", response);
        
        System.out.println("🎉 Proceso completo exitoso");
        return ResponseEntity.ok(finalResponse);
        
    } catch (Exception e) {
        System.err.println("❌ Error en crear-completa: " + e.getMessage());
        e.printStackTrace();
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("status", "ERROR");
        errorResponse.put("error", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
}