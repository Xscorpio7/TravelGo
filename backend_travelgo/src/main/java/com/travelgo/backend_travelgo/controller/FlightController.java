package com.travelgo.backend_travelgo.controller;

import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.Location;
import com.amadeus.resources.FlightOfferSearch;
import com.travelgo.backend_travelgo.service.AmadeusConnect;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


@RestController
@RequestMapping("/flights")
@CrossOrigin(origins = "*")
public class FlightController {

    private static final Logger logger = LoggerFactory.getLogger(FlightController.class);
    private final AmadeusConnect amadeusConnect;

    public FlightController(AmadeusConnect amadeusConnect) {
        this.amadeusConnect = amadeusConnect;
        logger.info("FlightController inicializado correctamente");
    }
    
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        logger.info("Test endpoint llamado");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Flight Controller funcionando");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/debug")
    public ResponseEntity<Map<String, Object>> debug() {
        Map<String, Object> response = new HashMap<>();
        response.put("controller", "FlightController");
        response.put("amadeusConnect", amadeusConnect != null ? "Inicializado" : "NULL");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping(value = "/locations", produces = "application/json")
public ResponseEntity<String> locations(@RequestParam String keyword) {
    logger.info("Búsqueda solicitada para: {}", keyword);

    try {
        if (amadeusConnect == null) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"AmadeusConnect es null\"}");
        }

        Location[] locations = amadeusConnect.location(keyword);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("data", locations);
        response.put("count", locations.length);

        // 🔑 Convertir a JSON válido usando Gson
        String jsonResponse = new com.google.gson.Gson().toJson(response);

        return ResponseEntity.ok(jsonResponse);

    } catch (ResponseException e) {
        return ResponseEntity.badRequest()
                .body("{\"error\":\"Error Amadeus: " + e.getMessage() + "\"}");
    } catch (Exception e) {
        return ResponseEntity.internalServerError()
                .body("{\"error\":\"Error: " + e.getMessage() + "\"}");
    }
}

    
    @GetMapping(value = "/search", produces = "application/json")
public ResponseEntity<String> search(
        @RequestParam String origin,
        @RequestParam String destination,
        @RequestParam String departure,
        @RequestParam int adults,
        @RequestParam(defaultValue = "10") int max) {

    logger.info("Búsqueda de vuelos: {} -> {}, fecha: {}, adultos: {}", origin, destination, departure, adults);

    try {
        FlightOfferSearch[] flights = amadeusConnect.searchFlights(origin, destination, departure, adults, max);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("data", flights);
        response.put("count", flights.length);

        // 🔑 Convertir todo el mapa a JSON válido con Gson
        String json = new com.google.gson.Gson().toJson(response);

        return ResponseEntity.ok(json);

    } catch (Exception e) {
        logger.error("Error en búsqueda de vuelos", e);
        return ResponseEntity.internalServerError()
                .body("{\"error\":\"" + e.getMessage() + "\"}");
    }
}
    
    /**
     * NUEVO: Ejemplo específico como tu código original
     * GET /flights/example
     */
    @GetMapping("/example")
    public ResponseEntity<Map<String, Object>> exampleSearch() {
        logger.info("Ejemplo de búsqueda MAD -> ATH");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (amadeusConnect == null) {
                response.put("error", "AmadeusConnect es null");
                return ResponseEntity.internalServerError().body(response);
            }
            
            // Replicar tu búsqueda original: MAD -> ATH
            FlightOfferSearch[] flights = amadeusConnect.searchFlights(
                "MAD",     // Madrid
                "ATH",     // Athens
                "2024-11-01", // Fecha de salida (corregí tu fecha 2622-11-01)
                1,         // 1 adulto
                1          // máximo 1 resultado
            );
            
            // Verificar respuesta como en tu código original
            if (flights.length > 0 && flights[0].getResponse().getStatusCode() != 200) {
                logger.warn("Wrong status code: {}", flights[0].getResponse().getStatusCode());
                response.put("error", "Wrong status code: " + flights[0].getResponse().getStatusCode());
                return ResponseEntity.badRequest().body(response);
            }
            
            response.put("status", "SUCCESS");
            response.put("message", "Ejemplo de búsqueda MAD -> ATH completado");
            response.put("data", flights);
            response.put("count", flights.length);
            
            if (flights.length > 0) {
                logger.info("Primer vuelo encontrado: {}", flights[0]);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (ResponseException e) {
            logger.error("Error Amadeus: {}", e.getMessage());
            response.put("error", "Error Amadeus: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            logger.error("Error general: {}", e.getMessage(), e);
            response.put("error", "Error: " + e.getMessage());
            response.put("type", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}