package com.travelgo.backend_travelgo.controller;

import com.travelgo.backend_travelgo.model.Reserva;
import com.travelgo.backend_travelgo.model.Viaje;
import com.travelgo.backend_travelgo.repository.ReservaRepository;
import com.travelgo.backend_travelgo.repository.ViajeRepository;
import com.travelgo.backend_travelgo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin("*")
public class BookingController {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    
    @Autowired
    private ViajeRepository viajeRepository;
    
    @Autowired
    private ReservaRepository reservaRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private Gson gson = new Gson();
    
    /**
     * Crear o actualizar un viaje de vuelo
     * POST /api/bookings/flights
     */
    @PostMapping("/flights")
    public ResponseEntity<?> bookFlight(
            @RequestBody Map<String, Object> flightData,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // Verificar token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token no proporcionado");
                return ResponseEntity.status(401).body(error);
            }
            
            String token = authHeader.substring(7);
            Integer usuarioId = jwtUtil.extractUsuarioId(token);
            
            if (jwtUtil.isTokenExpired(token)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token expirado");
                return ResponseEntity.status(401).body(error);
            }
            
            logger.info("Guardando vuelo para usuario: {}", usuarioId);
            
            // Crear viaje
            Viaje viaje = new Viaje();
            viaje.setFlightOfferId((String) flightData.get("id"));
            viaje.setOrigin((String) flightData.get("origin"));
            viaje.setOriginName((String) flightData.get("originName"));
            viaje.setDestinationCode((String) flightData.get("destination"));
            viaje.setDepartureDate(LocalDate.parse((String) flightData.get("departureDate")));
            
            if (flightData.containsKey("returnDate") && flightData.get("returnDate") != null) {
                String returnDateStr = (String) flightData.get("returnDate");
                if (!returnDateStr.isEmpty()) {
                    viaje.setReturnDate(LocalDate.parse(returnDateStr));
                }
            }
            
            viaje.setPrecio(new BigDecimal(flightData.get("price").toString()));
            viaje.setCurrency((String) flightData.get("currency"));
            viaje.setAirline((String) flightData.get("airline"));
            viaje.setAirlineName((String) flightData.get("airlineName"));
            viaje.setJourneyType((String) flightData.get("journeyType"));
            viaje.setBookableSeats((Integer) flightData.get("bookableSeats"));
            viaje.setFlightDetails(gson.toJson(flightData));
            viaje.setTipoViaje("vuelo");
            viaje.setTitulo(viaje.getOrigin() + " → " + viaje.getDestinationCode());
            viaje.setDestino(viaje.getDestinationCode());
            
            viajeRepository.save(viaje);
            
            logger.info("Vuelo guardado exitosamente: {}", viaje.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Vuelo guardado correctamente");
            response.put("viajeId", viaje.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al guardar vuelo: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al guardar vuelo: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.internalServerError().body(error);
        }
    }
   
    @PostMapping("/reservations")
    public ResponseEntity<?> createReservation(
            @RequestBody Map<String, Object> reservaData,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // Verificar token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token no proporcionado");
                return ResponseEntity.status(401).body(error);
            }
            
            String token = authHeader.substring(7);
            Integer usuarioId = jwtUtil.extractUsuarioId(token);
            
            if (jwtUtil.isTokenExpired(token)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token expirado");
                return ResponseEntity.status(401).body(error);
            }
            
            logger.info("Creando reserva para usuario: {}", usuarioId);
            
            // Crear reserva
            Reserva reserva = new Reserva();
            reserva.setUsuario_id(usuarioId);
            reserva.setViaje_id(Integer.parseInt(reservaData.get("viajeId").toString()));
            reserva.setEstado(Reserva.Estado.pendiente);
            
            if (reservaData.containsKey("alojamientoId") && reservaData.get("alojamientoId") != null) {
                reserva.setAlojamiento_id(Integer.parseInt(reservaData.get("alojamientoId").toString()));
            }
            
            if (reservaData.containsKey("transporteId") && reservaData.get("transporteId") != null) {
                reserva.setTransporte_id(Integer.parseInt(reservaData.get("transporteId").toString()));
            }
            
            reservaRepository.save(reserva);
            
            logger.info("Reserva creada exitosamente: {} para usuario: {}", reserva.getId(), usuarioId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Reserva creada correctamente");
            response.put("reservaId", reserva.getId());
            response.put("estado", reserva.getEstado().name());
            response.put("fecha_reserva", reserva.getFecha_reserva());
            
            return ResponseEntity.ok(response);
            
        } catch (NumberFormatException e) {
            logger.error("Error de formato: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Formato de datos incorrecto");
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            logger.error("Error al crear reserva: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear reserva: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Obtener todas las reservas del usuario autenticado
     * GET /api/bookings/reservations
     */
    @GetMapping("/reservations")
    public ResponseEntity<?> getUserReservations(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token no proporcionado");
                return ResponseEntity.status(401).body(error);
            }
            
            String token = authHeader.substring(7);
            Integer usuarioId = jwtUtil.extractUsuarioId(token);
            
            if (jwtUtil.isTokenExpired(token)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token expirado");
                return ResponseEntity.status(401).body(error);
            }
            
            logger.info("Obteniendo reservas para usuario: {}", usuarioId);
            
            List<Reserva> reservas = reservaRepository.findByUsuario_id(usuarioId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("data", reservas);
            response.put("count", reservas.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener reservas: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener reservas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Obtener una reserva específica
     * GET /api/bookings/reservations/{id}
     */
    @GetMapping("/reservations/{id}")
    public ResponseEntity<?> getReservation(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token no proporcionado");
                return ResponseEntity.status(401).body(error);
            }
            
            String token = authHeader.substring(7);
            Integer usuarioId = jwtUtil.extractUsuarioId(token);
            
            if (jwtUtil.isTokenExpired(token)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token expirado");
                return ResponseEntity.status(401).body(error);
            }
            
            logger.info("Obteniendo reserva: {} para usuario: {}", id, usuarioId);
            
            Reserva reserva = reservaRepository.findById(id).orElse(null);
            
            if (reserva == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Reserva no encontrada");
                return ResponseEntity.status(404).body(error);
            }
            
            // Verificar que la reserva pertenezca al usuario autenticado
            if (!reserva.getUsuario_id().equals(usuarioId)) {
                logger.warn("Acceso denegado: usuario {} intenta acceder a reserva de usuario {}", usuarioId, reserva.getUsuario_id());
                Map<String, String> error = new HashMap<>();
                error.put("error", "No tienes permiso para ver esta reserva");
                return ResponseEntity.status(403).body(error);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("data", reserva);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener reserva: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener reserva: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Cancelar una reserva
     * PUT /api/bookings/reservations/{id}/cancel
     */
    @PutMapping("/reservations/{id}/cancel")
    public ResponseEntity<?> cancelReservation(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token no proporcionado");
                return ResponseEntity.status(401).body(error);
            }
            
            String token = authHeader.substring(7);
            Integer usuarioId = jwtUtil.extractUsuarioId(token);
            
            if (jwtUtil.isTokenExpired(token)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token expirado");
                return ResponseEntity.status(401).body(error);
            }
            
            logger.info("Cancelando reserva: {} para usuario: {}", id, usuarioId);
            
            Reserva reserva = reservaRepository.findById(id).orElse(null);
            
            if (reserva == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Reserva no encontrada");
                return ResponseEntity.status(404).body(error);
            }
            
            if (!reserva.getUsuario_id().equals(usuarioId)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No tienes permiso para cancelar esta reserva");
                return ResponseEntity.status(403).body(error);
            }
            
            reserva.setEstado(Reserva.Estado.cancelada);
            reservaRepository.save(reserva);
            
            logger.info("Reserva cancelada: {}", id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Reserva cancelada correctamente");
            response.put("reservaId", reserva.getId());
            response.put("estado", reserva.getEstado().name());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al cancelar reserva: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al cancelar reserva: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}