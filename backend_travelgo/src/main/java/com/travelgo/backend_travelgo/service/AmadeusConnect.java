package com.travelgo.backend_travelgo.service;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.Location;
import com.amadeus.resources.FlightOfferSearch;
import com.amadeus.resources.Hotel;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.referencedata.Locations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
public class AmadeusConnect {
    
    private static final Logger logger = LoggerFactory.getLogger(AmadeusConnect.class);
    private final Amadeus amadeus;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String clientId;
    private final String clientSecret;
    private String accessToken;
    private long tokenExpiration = 0;
    
    // URLs de Amadeus API
    private static final String AMADEUS_AUTH_URL = "https://test.api.amadeus.com/v1/security/oauth2/token";
    private static final String AMADEUS_TRANSFER_URL = "https://test.api.amadeus.com/v1/shopping/transfer-offers";
    
    public AmadeusConnect(
            @Value("${amadeus.client.id}") String clientId,
            @Value("${amadeus.client.secret}") String clientSecret) {
        
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        
        logger.info("=== INICIALIZANDO AMADEUS ===");
        logger.info("Client ID: {}", clientId != null ? clientId.substring(0, Math.min(8, clientId.length())) + "..." : "null");
        
        try {
            this.amadeus = Amadeus.builder(clientId.trim(), clientSecret.trim())
                .setHostname("test")
                .build();
                
            logger.info("✅ Amadeus SDK inicializado correctamente en modo TEST");
            
            // Obtener token inicial para Transfer API
            getAccessToken();
            logger.info("✅ Token de acceso obtenido para Transfer API");
            
        } catch (Exception e) {
            logger.error("❌ Error al inicializar Amadeus: {}", e.getMessage(), e);
            throw new RuntimeException("Error al inicializar Amadeus", e);
        }
    }
    
    /**
     * Obtener token de acceso OAuth2 para Transfer API
     */
    private String getAccessToken() {
        try {
            // Verificar si el token actual sigue válido
            if (accessToken != null && System.currentTimeMillis() < tokenExpiration) {
                return accessToken;
            }
            
            logger.info("🔐 Obteniendo nuevo token de acceso...");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            String body = "grant_type=client_credentials&client_id=" + clientId + 
                         "&client_secret=" + clientSecret;
            
            HttpEntity<String> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                AMADEUS_AUTH_URL, request, JsonNode.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode json = response.getBody();
                accessToken = json.get("access_token").asText();
                int expiresIn = json.get("expires_in").asInt();
                
                // Guardar tiempo de expiración (con margen de 5 minutos)
                tokenExpiration = System.currentTimeMillis() + ((expiresIn - 300) * 1000L);
                
                logger.info("✅ Token obtenido exitosamente. Expira en {} segundos", expiresIn);
                return accessToken;
            }
            
            throw new RuntimeException("Error al obtener token de acceso");
            
        } catch (Exception e) {
            logger.error("❌ Error al obtener token: {}", e.getMessage());
            throw new RuntimeException("Error de autenticación con Amadeus", e);
        }
    }
    
    /**
     * Test de autenticación
     */
    public boolean testAuthentication() {
        try {
            logger.info("🔐 Probando autenticación...");
            
            Location[] locations = amadeus.referenceData.locations.get(
                Params.with("keyword", "LON")
                    .and("subType", Locations.AIRPORT)
            );
            
            logger.info("✅ Autenticación exitosa - {} ubicaciones encontradas", locations.length);
            return true;
            
        } catch (Exception e) {
            logger.error("❌ Fallo en autenticación: {}", e.getMessage());
            return false;
        }
    }
    
    // ========================================
    // VUELOS
    // ========================================
    
    public Location[] location(String keyword) throws ResponseException {
        logger.info("🔍 Buscando ubicaciones para: {}", keyword);
        
        try {
            Location[] locations = amadeus.referenceData.locations.get(
                Params.with("keyword", keyword)
                    .and("subType", Locations.AIRPORT)
            );
            
            logger.info("✅ Encontradas {} ubicaciones", locations.length);
            return locations;
            
        } catch (ResponseException e) {
            logger.error("❌ Error en búsqueda de ubicaciones: {}", e.getMessage());
            throw e;
        }
    }
    
    public FlightOfferSearch[] searchFlights(String originLocationCode, 
                                           String destinationLocationCode, 
                                           String departureDate, 
                                           int adults, 
                                           int max) throws ResponseException {
        
        logger.info("✈️ Búsqueda de vuelo SOLO IDA:");
        logger.info("   {} -> {}, Salida: {}, Adultos: {}, Max: {}", 
                   originLocationCode, destinationLocationCode, departureDate, adults, max);
        
        try {
            Params params = Params.with("originLocationCode", originLocationCode.trim())
                .and("destinationLocationCode", destinationLocationCode.trim())
                .and("departureDate", departureDate.trim())
                .and("adults", adults)
                .and("max", max)
                .and("currencyCode", "USD");
            
            logger.info("📡 Llamando a Amadeus API (solo ida)...");
            
            FlightOfferSearch[] flightOffers = amadeus.shopping.flightOffersSearch.get(params);
            
            logger.info("✅ Encontrados {} vuelos de ida", flightOffers.length);
            return flightOffers;
            
        } catch (ResponseException e) {
            logger.error("❌ Error Amadeus API: Status={}, Message={}", 
                e.getResponse() != null ? e.getResponse().getStatusCode() : "N/A",
                e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("❌ Error inesperado: {}", e.getMessage(), e);
            throw new RuntimeException("Error en búsqueda de vuelos", e);
        }
    }
    
    public FlightOfferSearch[] searchRoundTripFlights(String originLocationCode, 
                                                     String destinationLocationCode, 
                                                     String departureDate,
                                                     String returnDate,
                                                     int adults, 
                                                     int max) throws ResponseException {
        
        logger.info("🔄 Búsqueda de vuelo IDA Y VUELTA:");
        logger.info("   {} -> {}, Salida: {}, Regreso: {}, Adultos: {}, Max: {}", 
                   originLocationCode, destinationLocationCode, departureDate, returnDate, adults, max);
        
        try {
            if (returnDate == null || returnDate.trim().isEmpty()) {
                throw new IllegalArgumentException("La fecha de regreso es requerida para vuelos ida y vuelta");
            }
            
            if (returnDate.compareTo(departureDate) <= 0) {
                throw new IllegalArgumentException(
                    "La fecha de regreso debe ser posterior a la fecha de salida");
            }
            
            Params params = Params.with("originLocationCode", originLocationCode.trim())
                .and("destinationLocationCode", destinationLocationCode.trim())
                .and("departureDate", departureDate.trim())
                .and("returnDate", returnDate.trim())
                .and("adults", adults)
                .and("max", max)
                .and("currencyCode", "USD");
            
            logger.info("📡 Llamando a Amadeus API (ida y vuelta)...");
            
            FlightOfferSearch[] flightOffers = amadeus.shopping.flightOffersSearch.get(params);
            
            logger.info("✅ Encontrados {} vuelos de ida y vuelta", flightOffers.length);
            return flightOffers;
            
        } catch (ResponseException e) {
            logger.error("❌ Error Amadeus API: Status={}, Message={}", 
                e.getResponse() != null ? e.getResponse().getStatusCode() : "N/A",
                e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("❌ Error de validación: {}", e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            logger.error("❌ Error inesperado: {}", e.getMessage(), e);
            throw new RuntimeException("Error en búsqueda de vuelos", e);
        }
    }
    
    // ========================================
    // HOTELES
    // ========================================
    
    public Hotel[] searchHotelsByCity(String cityCode) throws ResponseException {
        logger.info("🏨 Búsqueda de hoteles en ciudad: {}", cityCode);
        
        try {
            Params params = Params.with("cityCode", cityCode.trim());
            
            logger.info("📡 Llamando a Amadeus Hotel List API...");
            
            Hotel[] hotels = amadeus.referenceData.locations.hotels.byCity.get(params);
            
            logger.info("✅ Encontrados {} hoteles", hotels.length);
            return hotels;
            
        } catch (ResponseException e) {
            logger.error("❌ Error Amadeus API: Status={}, Message={}", 
                e.getResponse() != null ? e.getResponse().getStatusCode() : "N/A",
                e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("❌ Error inesperado: {}", e.getMessage(), e);
            throw new RuntimeException("Error en búsqueda de hoteles", e);
        }
    }
    
    public Hotel[] searchHotelsByGeoCode(double latitude, double longitude, Integer radius) throws ResponseException {
        logger.info("🏨 Búsqueda de hoteles por geocode:");
        logger.info("   Lat: {}, Lon: {}, Radio: {}", latitude, longitude, radius);
        
        try {
            Params params = Params.with("latitude", latitude)
                .and("longitude", longitude);
            
            if (radius != null && radius > 0) {
                params.and("radius", radius);
                params.and("radiusUnit", "KM");
            }
            
            logger.info("📡 Llamando a Amadeus Hotel List API (geocode)...");
            
            Hotel[] hotels = amadeus.referenceData.locations.hotels.byGeocode.get(params);
            
            logger.info("✅ Encontrados {} hoteles", hotels.length);
            return hotels;
            
        } catch (ResponseException e) {
            logger.error("❌ Error Amadeus API: {}", e.getMessage());
            throw e;
        }
    }
    
    public Hotel[] searchHotelsByHotelIds(String[] hotelIds) throws ResponseException {
        logger.info("🏨 Buscando {} hoteles por IDs", hotelIds.length);
        
        try {
            String hotelIdsStr = String.join(",", hotelIds);
            
            Params params = Params.with("hotelIds", hotelIdsStr);
            
            logger.info("📡 Llamando a Amadeus Hotel List API (by IDs)...");
            
            Hotel[] hotels = amadeus.referenceData.locations.hotels.byHotels.get(params);
            
            logger.info("✅ Encontrados {} hoteles", hotels.length);
            return hotels;
            
        } catch (ResponseException e) {
            logger.error("❌ Error Amadeus API: {}", e.getMessage());
            throw e;
        }
    }
    
    // ========================================
    // ⭐ TRANSFERS - Llamadas HTTP Directas
    // ========================================
    
    /**
     * Buscar transfers desde aeropuerto a dirección
     * @param startLocationCode Código IATA del aeropuerto (ej: "MAD")
     * @param endAddressLine Dirección de destino
     * @param endCityName Ciudad de destino
     * @param endZipCode Código postal (opcional)
     * @param endCountryCode Código del país ISO 3166-1 (ej: "ES")
     * @param startDateTime Fecha y hora ISO 8601 (ej: "2025-11-20T10:30:00")
     * @param passengers Número de pasajeros (1-99)
     * @param transferType Tipo: "PRIVATE" o "SHARED"
     * @return JsonNode con ofertas de transfer
     */
    public JsonNode searchTransfers(
            String startLocationCode,
            String endAddressLine,
            String endCityName,
            String endZipCode,
            String endCountryCode,
            String startDateTime,
            int passengers,
            String transferType) {
        
        logger.info("🚗 Búsqueda de transfers:");
        logger.info("   Origen: {}, Destino: {} ({})", startLocationCode, endCityName, endCountryCode);
        logger.info("   Fecha: {}, Pasajeros: {}, Tipo: {}", startDateTime, passengers, transferType);
        
        try {
            // Obtener token válido
            String token = getAccessToken();
            
            // Construir URL con parámetros
            StringBuilder urlBuilder = new StringBuilder(AMADEUS_TRANSFER_URL);
            urlBuilder.append("?startLocationCode=").append(startLocationCode.trim());
            urlBuilder.append("&endAddressLine=").append(endAddressLine.trim().replace(" ", "%20"));
            urlBuilder.append("&endCityName=").append(endCityName.trim().replace(" ", "%20"));
            urlBuilder.append("&endCountryCode=").append(endCountryCode.trim());
            urlBuilder.append("&startDateTime=").append(startDateTime.trim());
            urlBuilder.append("&passengers=").append(passengers);
            urlBuilder.append("&transferType=").append(transferType.toUpperCase());
            
            if (endZipCode != null && !endZipCode.trim().isEmpty()) {
                urlBuilder.append("&endZipCode=").append(endZipCode.trim());
            }
            
            String url = urlBuilder.toString();
            
            logger.info("📡 URL: {}", url);
            
            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Hacer request
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, JsonNode.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode result = response.getBody();
                int count = result.has("data") ? result.get("data").size() : 0;
                logger.info("✅ Encontrados {} transfers", count);
                return result;
            }
            
            throw new RuntimeException("Respuesta inválida de Amadeus Transfer API");
            
        } catch (Exception e) {
            logger.error("❌ Error en búsqueda de transfers: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar transfers: " + e.getMessage(), e);
        }
    }
    
    /**
     * Buscar transfers desde aeropuerto (simplificado)
     * Usa "City Center" como destino por defecto
     */
    public JsonNode searchAirportTransfers(
            String airportCode,
            String cityName,
            String countryCode,
            String dateTime,
            int passengers) {
        
        logger.info("✈️🚗 Búsqueda de transfers desde aeropuerto: {} a {}", airportCode, cityName);
        
        // Dirección genérica del centro de la ciudad
        String addressLine = "City Center";
        
        return searchTransfers(
            airportCode,
            addressLine,
            cityName,
            null, // Sin código postal
            countryCode,
            dateTime,
            passengers,
            "PRIVATE" // Por defecto privado
        );
    }
    
    /**
     * Buscar transfers con más opciones
     */
    public JsonNode searchTransfersAdvanced(Map<String, String> params) {
        return searchTransfers(
            params.get("startLocationCode"),
            params.getOrDefault("endAddressLine", "City Center"),
            params.get("endCityName"),
            params.get("endZipCode"),
            params.get("endCountryCode"),
            params.get("startDateTime"),
            Integer.parseInt(params.getOrDefault("passengers", "1")),
            params.getOrDefault("transferType", "PRIVATE")
        );
    }
}