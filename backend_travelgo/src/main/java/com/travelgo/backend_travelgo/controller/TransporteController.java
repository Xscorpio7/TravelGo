package com.travelgo.backend_travelgo.controller;

import com.travelgo.backend_travelgo.model.Transporte;
import com.travelgo.backend_travelgo.service.TransportService;
import com.travelgo.backend_travelgo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transporte")
@CrossOrigin("*")
public class TransporteController {
    
    private static final Logger logger = LoggerFactory.getLogger(TransporteController.class);
    private static final int MAX_RESULTS = 10;

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
        logger.info("🧪 Test endpoint llamado");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Transport Controller funcionando");
        response.put("maxResults", MAX_RESULTS);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 🔍 BÚSQUEDA DE TRANSPORTE - Estilo Vuelos/Hoteles
     * GET /api/transporte/search?origen=Aeropuerto&destino=Hotel&tipo=Transfer
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchTransporte(
            @RequestParam(required = false) String origen,
            @RequestParam(required = false) String destino,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false, defaultValue = "10") Integer max) {
        try {
            logger.info("🔍 Búsqueda de transporte: origen={}, destino={}, tipo={}", 
                       origen, destino, tipo);
            
            // Validar y limitar max
            if (max == null || max < 1) max = MAX_RESULTS;
            if (max > MAX_RESULTS) max = MAX_RESULTS;
            
            List<Transporte> transportes;
            
            // Búsqueda por filtros
            if (tipo != null && !tipo.isEmpty()) {
                try {
                    Transporte.Tipo tipoEnum = Transporte.Tipo.valueOf(tipo);
                    
                    if (origen != null && destino != null) {
                        // Buscar por origen, destino y tipo
                        transportes = transportService.buscarPorOrigenDestinoTipo(
                            origen, destino, tipoEnum
                        );
                    } else {
                        // Buscar solo por tipo y estado disponible
                        transportes = transportService.buscarDisponiblesPorTipo(tipoEnum);
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "Tipo de transporte inválido. Valores: Avion, Bus, Tren, Barco, Auto_Rental, Taxi, Transfer"));
                }
            } else if (origen != null && destino != null) {
                // Buscar por origen y destino
                transportes = transportService.buscarPorOrigenDestino(origen, destino);
            } else if (destino != null) {
                // Buscar por destino
                transportes = transportService.buscarPorDestino(destino);
            } else {
                // Listar todos los disponibles
                transportes = transportService.buscarDisponibles();
            }
            
            // Limitar resultados
            List<Transporte> limitedTransportes = transportes.stream()
                .limit(max)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("data", limitedTransportes);
            response.put("count", limitedTransportes.size());
            response.put("totalFound", transportes.size());
            response.put("maxAllowed", MAX_RESULTS);
            response.put("search", Map.of(
                "origen", origen != null ? origen : "all",
                "destino", destino != null ? destino : "all",
                "tipo", tipo != null ? tipo : "all",
                "requestedMax", max
            ));
            
            logger.info("✅ Búsqueda exitosa: {} transportes encontrados, mostrando {}", 
                       transportes.size(), limitedTransportes.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error en búsqueda: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error en búsqueda: " + e.getMessage()));
        }
    }
    
    /**
     * 🚖 BÚSQUEDA DE TRANSFERS AEROPUERTO
     * GET /api/transporte/aeropuerto?ciudad=Barcelona&max=5
     */
    @GetMapping("/aeropuerto")
    public ResponseEntity<?> searchAeropuertoTransfers(
            @RequestParam String ciudad,
            @RequestParam(required = false, defaultValue = "10") Integer max) {
        try {
            logger.info("✈️ Buscando transfers de aeropuerto en: {}", ciudad);
            
            if (max > MAX_RESULTS) max = MAX_RESULTS;
            
            List<Transporte> transfers = transportService.buscarTransferAeropuerto(ciudad);
            
            // Limitar resultados
            List<Transporte> limitedTransfers = transfers.stream()
                .limit(max)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("data", limitedTransfers);
            response.put("count", limitedTransfers.size());
            response.put("totalFound", transfers.size());
            response.put("maxAllowed", MAX_RESULTS);
            response.put("search", Map.of(
                "ciudad", ciudad,
                "tipo", "Transfer",
                "requestedMax", max
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 💰 CALCULAR PRECIO ESTIMADO
     * GET /api/transporte/precio?origen=Aeropuerto&destino=Hotel&tipo=Taxi
     */
    @GetMapping("/precio")
    public ResponseEntity<?> calcularPrecio(
            @RequestParam String origen,
            @RequestParam String destino,
            @RequestParam String tipo) {
        try {
            logger.info("💰 Calculando precio: {} -> {} ({})", origen, destino, tipo);
            
            Transporte.Tipo tipoEnum = Transporte.Tipo.valueOf(tipo);
            BigDecimal precio = transportService.calcularPrecioEstimado(origen, destino, tipoEnum);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("origen", origen);
            response.put("destino", destino);
            response.put("tipo", tipo);
            response.put("precio_estimado", precio);
            response.put("currency", "USD");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Tipo de transporte inválido"));
        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 📋 VER TIPOS DE TRANSPORTE DISPONIBLES
     * GET /api/transporte/tipos
     */
    @GetMapping("/tipos")
    public ResponseEntity<?> getTiposTransporte() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("tipos", Transporte.Tipo.values());
        response.put("count", Transporte.Tipo.values().length);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 🔍 VER TRANSPORTE POR ID
     * GET /api/transporte/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransporteById(@PathVariable Integer id) {
        try {
            logger.info("🔍 Obteniendo transporte ID: {}", id);
            
            return transportService.obtenerPorId(id)
                .map(transporte -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "SUCCESS");
                    response.put("data", transporte);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(404)
                    .body(Map.of("error", "Transporte no encontrado")));
                
        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 📝 RESERVAR TRANSPORTE (Requiere autenticación)
     * POST /api/transporte/{id}/reservar
     */
    @PostMapping("/{id}/reservar")
    public ResponseEntity<?> reservarTransporte(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Verificar autenticación
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401)
                    .body(Map.of("error", "Token no proporcionado"));
            }
            
            String token = authHeader.substring(7);
            Integer usuarioId = jwtUtil.extractUsuarioId(token);
            
            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(401)
                    .body(Map.of("error", "Token expirado"));
            }
            
            logger.info("📝 Usuario {} reservando transporte ID: {}", usuarioId, id);
            
            Transporte transporte = transportService.reservarTransporte(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Transporte reservado correctamente");
            response.put("data", transporte);
            response.put("usuarioId", usuarioId);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("❌ Error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error al reservar: " + e.getMessage()));
        }
    }
    
    /**
     * ❌ CANCELAR RESERVA DE TRANSPORTE
     * PUT /api/transporte/{id}/cancelar
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarReserva(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Verificar autenticación
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401)
                    .body(Map.of("error", "Token no proporcionado"));
            }
            
            String token = authHeader.substring(7);
            
            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(401)
                    .body(Map.of("error", "Token expirado"));
            }
            
            logger.info("❌ Cancelando reserva de transporte ID: {}", id);
            
            Transporte transporte = transportService.cancelarReserva(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Reserva cancelada correctamente");
            response.put("data", transporte);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("❌ Error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error al cancelar: " + e.getMessage()));
        }
    }
    
    /**
     * 🎯 EJEMPLO DE BÚSQUEDA
     * GET /api/transporte/example
     */
    @GetMapping("/example")
    public ResponseEntity<Map<String, Object>> exampleSearch() {
        logger.info("🧪 Ejemplo de búsqueda de transporte");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Transporte> transportes = transportService.buscarDisponibles();
            
            // Limitar a 5 para el ejemplo
            List<Transporte> limited = transportes.stream()
                .limit(5)
                .collect(Collectors.toList());
            
            response.put("status", "SUCCESS");
            response.put("message", "Ejemplo de búsqueda completado");
            response.put("data", limited);
            response.put("count", limited.size());
            response.put("totalFound", transportes.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage());
            response.put("error", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}