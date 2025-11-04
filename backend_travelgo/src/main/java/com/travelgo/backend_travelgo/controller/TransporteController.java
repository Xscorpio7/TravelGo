package com.travelgo.backend_travelgo.controller;

import com.travelgo.backend_travelgo.model.Transporte;
import com.travelgo.backend_travelgo.repository.TransporteRepository;
import com.travelgo.backend_travelgo.service.AmadeusConnect;
import com.travelgo.backend_travelgo.service.TransportService;
import com.travelgo.backend_travelgo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST completo para Transfers
 * Gestiona la búsqueda, reserva y administración de transfers
 * ✅ CORREGIDO: Sin problemas de serialización de Gson
 */
@RestController
@RequestMapping("/api/transporte")
@CrossOrigin(origins = "*")
public class TransporteController {
    
    private static final Logger logger = LoggerFactory.getLogger(TransporteController.class);
    
    @Autowired
    private TransporteRepository transporteRepository;
    
    @Autowired
    private AmadeusConnect amadeusConnect;
    
    @Autowired
    private TransportService transportService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Test del controlador
     * GET /api/transporte/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        logger.info("🧪 Test endpoint de transporte llamado");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Transport Controller funcionando correctamente");
        response.put("timestamp", System.currentTimeMillis());
        response.put("endpoints", Map.of(
            "search", "/api/transporte/search-transfers",
            "available", "/api/transporte/disponibles",
            "byType", "/api/transporte/por-tipo",
            "search", "/api/transporte/buscar",
            "reserve", "/api/transporte/{id}/reservar",
            "cancel", "/api/transporte/{id}/cancelar"
        ));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * ✅ MÉTODO CORREGIDO - Buscar transfers desde aeropuerto usando Amadeus
     * GET /api/transporte/search-transfers
     * 
     * @param airportCode Código IATA del aeropuerto (ej: "MAD", "BCN", "ATH")
     * @param cityName Nombre de la ciudad destino (ej: "Athens", "Madrid")
     * @param countryCode Código del país (ej: "GR", "ES")
     * @param dateTime Fecha y hora ISO 8601 (ej: "2025-12-15T10:00:00")
     * @param passengers Número de pasajeros (default: 1)
     */
    @GetMapping("/search-transfers")
    public ResponseEntity<Map<String, Object>> searchTransfers(
            @RequestParam String airportCode,
            @RequestParam String cityName,
            @RequestParam String countryCode,
            @RequestParam String dateTime,
            @RequestParam(defaultValue = "1") int passengers) {
        
        logger.info("🚗 Búsqueda de transfers: {} -> {} ({}), Fecha: {}, Pasajeros: {}", 
                   airportCode, cityName, countryCode, dateTime, passengers);
        
        try {
            // Validaciones
            if (airportCode == null || airportCode.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "El código del aeropuerto es requerido (ej: MAD, BCN)");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (cityName == null || cityName.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "El nombre de la ciudad es requerido");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (countryCode == null || countryCode.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "El código del país es requerido (ej: ES, GR)");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (dateTime == null || dateTime.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "La fecha y hora son requeridas (formato: 2025-12-15T10:00:00)");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (passengers < 1 || passengers > 8) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "El número de pasajeros debe estar entre 1 y 8");
                return ResponseEntity.badRequest().body(error);
            }
            
            // ✅ CORREGIDO: Buscar en Amadeus y guardar
            // El servicio devuelve List<Transporte> que Spring puede serializar sin problemas
            List<Transporte> transfers = transportService.buscarYGuardarTransfers(
                airportCode.toUpperCase().trim(),
                cityName.trim(),
                countryCode.toUpperCase().trim(),
                dateTime.trim(),
                passengers
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("data", transfers);
            response.put("count", transfers.size());
            response.put("search", Map.of(
                "airportCode", airportCode.toUpperCase().trim(),
                "cityName", cityName.trim(),
                "countryCode", countryCode.toUpperCase().trim(),
                "dateTime", dateTime.trim(),
                "passengers", passengers
            ));
            
            logger.info("✅ Búsqueda exitosa: {} transfers encontrados", transfers.size());
            
            // ✅ Retornar Map directamente - Spring lo serializa con Jackson (no Gson)
            // Jackson no tiene problemas con LocalDateTime
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("❌ Error en búsqueda de transfers: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage() != null ? e.getMessage() : "Error en búsqueda");
            error.put("status", "ERROR");
            
            if (e.getMessage() != null && (e.getMessage().contains("Amadeus") || e.getMessage().contains("API"))) {
                return ResponseEntity.badRequest().body(error);
            }
            
            return ResponseEntity.internalServerError().body(error);
                    
        } catch (Exception e) {
            logger.error("❌ Error inesperado en búsqueda de transfers", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage() != null ? e.getMessage() : "Error interno");
            error.put("status", "ERROR");
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Obtener todos los transportes disponibles
     * GET /api/transporte/disponibles
     */
    @GetMapping("/disponibles")
    public ResponseEntity<Map<String, Object>> getDisponibles() {
        try {
            logger.info("📋 Obteniendo transportes disponibles");
            
            List<Transporte> transportes = transportService.buscarDisponibles();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("data", transportes);
            response.put("count", transportes.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error al obtener transportes disponibles: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al obtener transportes: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Buscar transportes por tipo
     * GET /api/transporte/por-tipo?tipo=Transfer
     */
    @GetMapping("/por-tipo")
    public ResponseEntity<Map<String, Object>> getPorTipo(@RequestParam String tipo) {
        try {
            logger.info("📋 Buscando transportes por tipo: {}", tipo);
            
            Transporte.Tipo tipoEnum = Transporte.Tipo.valueOf(tipo);
            List<Transporte> transportes = transportService.buscarDisponiblesPorTipo(tipoEnum);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("data", transportes);
            response.put("count", transportes.size());
            response.put("tipo", tipo);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Tipo de transporte inválido. Valores válidos: Avion, Bus, Tren, Barco, Auto_Rental, Taxi, Transfer");
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Buscar transportes por origen y destino
     * GET /api/transporte/buscar?origen=MAD&destino=Athens
     */
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscarPorOrigenDestino(
            @RequestParam(required = false) String origen,
            @RequestParam(required = false) String destino,
            @RequestParam(required = false) String tipo) {
        
        try {
            logger.info("🔍 Búsqueda: origen={}, destino={}, tipo={}", origen, destino, tipo);
            
            List<Transporte> transportes;
            
            if (origen != null && destino != null && tipo != null) {
                Transporte.Tipo tipoEnum = Transporte.Tipo.valueOf(tipo);
                transportes = transportService.buscarPorOrigenDestinoTipo(origen, destino, tipoEnum);
            } else if (origen != null && destino != null) {
                transportes = transportService.buscarPorOrigenDestino(origen, destino);
            } else if (origen != null) {
                transportes = transportService.buscarPorOrigen(origen);
            } else if (destino != null) {
                transportes = transportService.buscarPorDestino(destino);
            } else {
                transportes = transportService.buscarDisponibles();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("data", transportes);
            response.put("count", transportes.size());
            response.put("filters", Map.of(
                "origen", origen != null ? origen : "todos",
                "destino", destino != null ? destino : "todos",
                "tipo", tipo != null ? tipo : "todos"
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error en búsqueda: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Obtener transporte por ID
     * GET /api/transporte/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        try {
            logger.info("📄 Obteniendo transporte: {}", id);
            
            return transportService.obtenerPorId(id)
                    .map(transporte -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("status", "SUCCESS");
                        response.put("data", transporte);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        Map<String, Object> error = new HashMap<>();
                        error.put("error", "Transporte no encontrado");
                        return ResponseEntity.status(404).body(error);
                    });
                    
        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Crear transporte manualmente
     * POST /api/transporte
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody Transporte transporte) {
        try {
            logger.info("➕ Creando transporte: {} -> {}", transporte.getOrigen(), transporte.getDestino());
            
            Transporte creado = transportService.crearTransporte(transporte);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Transporte creado correctamente");
            response.put("data", creado);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error al crear transporte: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Reservar un transporte
     * PUT /api/transporte/{id}/reservar
     */
    @PutMapping("/{id}/reservar")
    public ResponseEntity<Map<String, Object>> reservar(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // Verificar autenticación
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Token no proporcionado");
                return ResponseEntity.status(401).body(error);
            }
            
            String token = authHeader.substring(7);
            Integer usuarioId = jwtUtil.extractUsuarioId(token);
            
            if (jwtUtil.isTokenExpired(token)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Token expirado");
                return ResponseEntity.status(401).body(error);
            }
            
            logger.info("🎫 Usuario {} reservando transporte {}", usuarioId, id);
            
            Transporte reservado = transportService.reservarTransporte(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Transporte reservado correctamente");
            response.put("data", reservado);
            response.put("reservadoPor", usuarioId);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("❌ Error al reservar: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Cancelar reserva de transporte
     * PUT /api/transporte/{id}/cancelar
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Map<String, Object>> cancelar(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Token no proporcionado");
                return ResponseEntity.status(401).body(error);
            }
            
            String token = authHeader.substring(7);
            Integer usuarioId = jwtUtil.extractUsuarioId(token);
            
            if (jwtUtil.isTokenExpired(token)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Token expirado");
                return ResponseEntity.status(401).body(error);
            }
            
            logger.info("❌ Usuario {} cancelando transporte {}", usuarioId, id);
            
            Transporte cancelado = transportService.cancelarReserva(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Reserva cancelada correctamente");
            response.put("data", cancelado);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Actualizar transporte
     * PUT /api/transporte/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Integer id, 
            @RequestBody Transporte transporteDetails) {
        try {
            logger.info("✏️ Actualizando transporte: {}", id);
            
            Transporte actualizado = transportService.actualizarTransporte(id, transporteDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Transporte actualizado correctamente");
            response.put("data", actualizado);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(404).body(error);
        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Eliminar transporte
     * DELETE /api/transporte/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Integer id) {
        try {
            logger.info("🗑️ Eliminando transporte: {}", id);
            
            transportService.eliminarTransporte(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Transporte eliminado correctamente");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(404).body(error);
        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Obtener estadísticas de transfers
     * GET /api/transporte/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        try {
            logger.info("📊 Obteniendo estadísticas");
            
            Map<String, Object> stats = transportService.obtenerEstadisticas();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Ejemplo de búsqueda de transfers (Athens)
     * GET /api/transporte/example
     */
    @GetMapping("/example")
    public ResponseEntity<Map<String, Object>> example() {
        logger.info("🧪 Ejemplo de búsqueda de transfers ATH -> Athens");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Transporte> transfers = transportService.buscarYGuardarTransfers(
                "ATH",
                "Athens",
                "GR",
                "2025-12-15T10:00:00",
                2
            );
            
            response.put("status", "SUCCESS");
            response.put("message", "Ejemplo de búsqueda completado");
            response.put("data", transfers);
            response.put("count", transfers.size());
            response.put("search", Map.of(
                "airportCode", "ATH",
                "cityName", "Athens",
                "countryCode", "GR",
                "dateTime", "2025-12-15T10:00:00",
                "passengers", 2
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error en ejemplo: {}", e.getMessage(), e);
            response.put("status", "ERROR");
            response.put("error", "Error: " + e.getMessage());
            response.put("type", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}