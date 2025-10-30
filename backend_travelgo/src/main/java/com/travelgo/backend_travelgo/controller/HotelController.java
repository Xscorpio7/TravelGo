package com.travelgo.backend_travelgo.controller;

import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.Hotel;
import com.travelgo.backend_travelgo.service.AmadeusConnect;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;

@RestController
@RequestMapping("/hotels")
@CrossOrigin(origins = "*")
public class HotelController {

    private static final Logger logger = LoggerFactory.getLogger(HotelController.class);
    private final AmadeusConnect amadeusConnect;
    private final Gson gson = new Gson();
    
    // ⭐ CONSTANTE: Límite máximo de resultados
    private static final int MAX_RESULTS = 10;

    public HotelController(AmadeusConnect amadeusConnect) {
        this.amadeusConnect = amadeusConnect;
        logger.info("✅ HotelController inicializado correctamente (max {} resultados)", MAX_RESULTS);
    }
    
    /**
     * Test del controlador
     * GET /hotels/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        logger.info("🧪 Test endpoint llamado");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Hotel Controller funcionando");
        response.put("maxResults", MAX_RESULTS);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Buscar hoteles por ciudad (máximo 10 resultados)
     * GET /hotels/search?cityCode=NYC
     * GET /hotels/search?cityCode=NYC&max=5  (límite personalizado)
     */
    @GetMapping(value = "/search", produces = "application/json")
    public ResponseEntity<String> searchHotels(
            @RequestParam String cityCode,
            @RequestParam(required = false, defaultValue = "10") Integer max) {

        logger.info("🏨 Búsqueda de hoteles en ciudad: {} (max: {})", cityCode, max);

        try {
            // Validaciones
            if (cityCode == null || cityCode.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("{\"error\":\"El parámetro 'cityCode' es requerido (código IATA de 3 letras)\"}");
            }
            
            // ⭐ VALIDAR Y LIMITAR max a 10
            if (max == null || max < 1) {
                max = MAX_RESULTS;
            } else if (max > MAX_RESULTS) {
                logger.warn("⚠️ Límite solicitado ({}) excede el máximo ({}). Usando {}", 
                           max, MAX_RESULTS, MAX_RESULTS);
                max = MAX_RESULTS;
            }
            
            // Buscar hoteles
            Hotel[] hotels = amadeusConnect.searchHotelsByCity(cityCode.toUpperCase().trim());
            
            // ⭐ LIMITAR resultados
            Hotel[] limitedHotels = limitResults(hotels, max);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("data", limitedHotels);
            response.put("count", limitedHotels.length);
            response.put("totalFound", hotels.length);
            response.put("maxAllowed", MAX_RESULTS);
            response.put("search", Map.of(
                "cityCode", cityCode.toUpperCase().trim(),
                "requestedMax", max
            ));

            String json = gson.toJson(response);
            logger.info("✅ Búsqueda exitosa: {} hoteles encontrados, mostrando {}", 
                       hotels.length, limitedHotels.length);

            return ResponseEntity.ok(json);

        } catch (ResponseException e) {
            logger.error("❌ Error de Amadeus API", e);
            String errorMsg = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "Error desconocido";
            return ResponseEntity.badRequest()
                    .body("{\"error\":\"Error Amadeus: " + errorMsg + "\"}");
                    
        } catch (Exception e) {
            logger.error("❌ Error en búsqueda de hoteles", e);
            String errorMsg = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "Error interno";
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"" + errorMsg + "\"}");
        }
    }
    
    /**
     * Buscar hoteles por coordenadas geográficas (máximo 10 resultados)
     * GET /hotels/search-by-location?lat=40.7128&lon=-74.0060&radius=5
     * GET /hotels/search-by-location?lat=40.7128&lon=-74.0060&radius=5&max=5
     */
    @GetMapping(value = "/search-by-location", produces = "application/json")
    public ResponseEntity<String> searchHotelsByLocation(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(required = false) Integer radius,
            @RequestParam(required = false, defaultValue = "10") Integer max) {

        logger.info("🏨 Búsqueda de hoteles por ubicación: Lat: {}, Lon: {} (max: {})", 
                   lat, lon, max);

        try {
            // ⭐ VALIDAR Y LIMITAR max
            if (max == null || max < 1) {
                max = MAX_RESULTS;
            } else if (max > MAX_RESULTS) {
                max = MAX_RESULTS;
            }
            
            // Buscar hoteles
            Hotel[] hotels = amadeusConnect.searchHotelsByGeoCode(lat, lon, radius);
            
            // ⭐ LIMITAR resultados
            Hotel[] limitedHotels = limitResults(hotels, max);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("data", limitedHotels);
            response.put("count", limitedHotels.length);
            response.put("totalFound", hotels.length);
            response.put("maxAllowed", MAX_RESULTS);
            response.put("search", Map.of(
                "latitude", lat,
                "longitude", lon,
                "radius", radius != null ? radius : "default",
                "requestedMax", max
            ));

            String json = gson.toJson(response);
            logger.info("✅ Búsqueda exitosa: {} hoteles encontrados, mostrando {}", 
                       hotels.length, limitedHotels.length);

            return ResponseEntity.ok(json);

        } catch (ResponseException e) {
            logger.error("❌ Error de Amadeus API", e);
            String errorMsg = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "Error desconocido";
            return ResponseEntity.badRequest()
                    .body("{\"error\":\"Error Amadeus: " + errorMsg + "\"}");
                    
        } catch (Exception e) {
            logger.error("❌ Error en búsqueda de hoteles", e);
            String errorMsg = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "Error interno";
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"" + errorMsg + "\"}");
        }
    }
    
    /**
     * Obtener detalles de hoteles por IDs (máximo 10)
     * GET /hotels/by-ids?ids=HOTEL1,HOTEL2,HOTEL3
     */
    @GetMapping(value = "/by-ids", produces = "application/json")
    public ResponseEntity<String> getHotelsByIds(@RequestParam String ids) {
        logger.info("🏨 Obteniendo detalles de hoteles por IDs: {}", ids);

        try {
            String[] hotelIds = ids.split(",");
            
            // ⭐ LIMITAR a 10 IDs máximo
            if (hotelIds.length > MAX_RESULTS) {
                logger.warn("⚠️ Se solicitaron {} hoteles, limitando a {}", 
                           hotelIds.length, MAX_RESULTS);
                hotelIds = Arrays.copyOf(hotelIds, MAX_RESULTS);
            }
            
            Hotel[] hotels = amadeusConnect.searchHotelsByHotelIds(hotelIds);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("data", hotels);
            response.put("count", hotels.length);
            response.put("maxAllowed", MAX_RESULTS);

            String json = gson.toJson(response);
            logger.info("✅ Detalles de {} hoteles obtenidos", hotels.length);

            return ResponseEntity.ok(json);

        } catch (ResponseException e) {
            logger.error("❌ Error de Amadeus API", e);
            String errorMsg = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "Error desconocido";
            return ResponseEntity.badRequest()
                    .body("{\"error\":\"Error Amadeus: " + errorMsg + "\"}");
                    
        } catch (Exception e) {
            logger.error("❌ Error al obtener detalles de hoteles", e);
            String errorMsg = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "Error interno";
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"" + errorMsg + "\"}");
        }
    }
    
    /**
     * Ejemplo de búsqueda de hoteles en Nueva York
     * GET /hotels/example
     */
    @GetMapping("/example")
    public ResponseEntity<Map<String, Object>> exampleSearch() {
        logger.info("🧪 Ejemplo de búsqueda de hoteles en NYC");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Hotel[] hotels = amadeusConnect.searchHotelsByCity("NYC");
            
            // ⭐ LIMITAR a 10
            Hotel[] limitedHotels = limitResults(hotels, MAX_RESULTS);
            
            response.put("status", "SUCCESS");
            response.put("message", "Ejemplo de búsqueda NYC completado");
            response.put("data", limitedHotels);
            response.put("count", limitedHotels.length);
            response.put("totalFound", hotels.length);
            response.put("maxAllowed", MAX_RESULTS);
            
            return ResponseEntity.ok(response);
            
        } catch (ResponseException e) {
            logger.error("❌ Error Amadeus: {}", e.getMessage());
            response.put("error", "Error Amadeus: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            logger.error("❌ Error general: {}", e.getMessage(), e);
            response.put("error", "Error: " + e.getMessage());
            response.put("type", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * ⭐ MÉTODO AUXILIAR: Limitar resultados a un máximo
     */
    private Hotel[] limitResults(Hotel[] hotels, int max) {
        if (hotels == null || hotels.length == 0) {
            return hotels;
        }
        
        if (hotels.length <= max) {
            return hotels;
        }
        
        return Arrays.copyOf(hotels, max);
    }
}