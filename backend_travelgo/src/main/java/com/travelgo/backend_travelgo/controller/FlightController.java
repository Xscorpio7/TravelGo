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

@RestController
@RequestMapping("/flights")
@CrossOrigin(origins = "*")
public class FlightController {

    private static final Logger logger = LoggerFactory.getLogger(FlightController.class);
    private final AmadeusConnect amadeusConnect;
    private final Gson gson = new Gson();

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

            String jsonResponse = gson.toJson(response);
            return ResponseEntity.ok(jsonResponse);

        } catch (ResponseException e) {
            return ResponseEntity.badRequest()
                    .body("{\"error\":\"Error Amadeus: " + e.getMessage().replace("\"", "'") + "\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Error: " + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    @GetMapping(value = "/search", produces = "application/json")
    public ResponseEntity<String> search(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String departure,
            @RequestParam(required = false) String returnDate,  
            @RequestParam(defaultValue = "1") int adults,
            @RequestParam(defaultValue = "10") int max) {

        logger.info("Búsqueda de vuelos: {} -> {}, Salida: {}, Regreso: {}, Adultos: {}", 
                   origin, destination, departure, returnDate != null ? returnDate : "N/A", adults);

        try {
            if (origin == null || origin.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("{\"error\":\"El parámetro 'origin' es requerido\"}");
            }
            
            if (destination == null || destination.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("{\"error\":\"El parámetro 'destination' es requerido\"}");
            }
            
            if (departure == null || departure.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("{\"error\":\"El parámetro 'departure' es requerido (formato: YYYY-MM-DD)\"}");
            }
            try {
                LocalDate departureDate = LocalDate.parse(departure, DateTimeFormatter.ISO_LOCAL_DATE);
                if (departureDate.isBefore(LocalDate.now())) {
                    return ResponseEntity.badRequest()
                        .body("{\"error\":\"La fecha de salida no puede ser en el pasado\"}");
                }
                if (returnDate != null && !returnDate.trim().isEmpty()) {
                    LocalDate returnLocalDate = LocalDate.parse(returnDate, DateTimeFormatter.ISO_LOCAL_DATE);
                    
                    if (returnLocalDate.isBefore(departureDate)) {
                        return ResponseEntity.badRequest()
                            .body("{\"error\":\"La fecha de regreso debe ser posterior a la fecha de salida\"}");
                    }
                }
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest()
                    .body("{\"error\":\"Formato de fecha incorrecto. Use YYYY-MM-DD\"}");
            }
            
            if (adults < 1 || adults > 9) {
                return ResponseEntity.badRequest()
                    .body("{\"error\":\"El número de adultos debe estar entre 1 y 9\"}");
            }
            
            FlightOfferSearch[] flights;
            

            if (returnDate != null && !returnDate.trim().isEmpty()) {
                logger.info("Búsqueda IDA Y VUELTA");
                flights = amadeusConnect.searchRoundTripFlights(
                    origin.toUpperCase().trim(), 
                    destination.toUpperCase().trim(), 
                    departure.trim(),
                    returnDate.trim(),
                    adults, 
                    max
                );
            } else {
                logger.info("Búsqueda SOLO IDA");
                flights = amadeusConnect.searchFlights(
                    origin.toUpperCase().trim(), 
                    destination.toUpperCase().trim(), 
                    departure.trim(), 
                    adults, 
                    max
                );
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("data", flights);
            response.put("count", flights.length);
            response.put("tripType", returnDate != null && !returnDate.trim().isEmpty() ? "round-trip" : "one-way");
            response.put("search", Map.of(
                "origin", origin.toUpperCase().trim(),
                "destination", destination.toUpperCase().trim(),
                "departure", departure.trim(),
                "returnDate", returnDate != null ? returnDate.trim() : "N/A",
                "adults", adults,
                "max", max
            ));

            String json = gson.toJson(response);
            logger.info("Búsqueda exitosa: {} vuelos encontrados", flights.length);

            return ResponseEntity.ok(json);

        } catch (ResponseException e) {
            logger.error("Error de Amadeus API", e);
            String errorMsg = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "Error desconocido";
            return ResponseEntity.badRequest()
                    .body("{\"error\":\"Error Amadeus: " + errorMsg + "\"}");
                    
        } catch (Exception e) {
            logger.error("Error en búsqueda de vuelos", e);
            String errorMsg = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "Error interno";
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"" + errorMsg + "\"}");
        }
    }
    
    @GetMapping("/example")
    public ResponseEntity<Map<String, Object>> exampleSearch() {
        logger.info("Ejemplo de búsqueda MAD -> ATH");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (amadeusConnect == null) {
                response.put("error", "AmadeusConnect es null");
                return ResponseEntity.internalServerError().body(response);
            }
            
            FlightOfferSearch[] flights = amadeusConnect.searchFlights(
                "MAD", "ATH", "2025-12-31", 1, 1
            );
            
            if (flights.length > 0 && flights[0].getResponse().getStatusCode() != 200) {
                logger.warn("Wrong status code: {}", flights[0].getResponse().getStatusCode());
                response.put("error", "Wrong status code: " + flights[0].getResponse().getStatusCode());
                return ResponseEntity.badRequest().body(response);
            }
            
            response.put("status", "SUCCESS");
            response.put("message", "Ejemplo de búsqueda MAD -> ATH completado");
            response.put("data", flights);
            response.put("count", flights.length);
            
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