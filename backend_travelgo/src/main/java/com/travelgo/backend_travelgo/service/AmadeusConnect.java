package com.travelgo.backend_travelgo.service;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.Location;
import com.amadeus.resources.FlightOfferSearch;
import com.amadeus.resources.Hotel;
import com.amadeus.resources.TransferOffering;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.referencedata.Locations;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Service
public class AmadeusConnect {
    
    private static final Logger logger = LoggerFactory.getLogger(AmadeusConnect.class);
    private final Amadeus amadeus;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Gson gson;
    private final String clientId;
    private final String clientSecret;
    private String accessToken;
    private long tokenExpiration = 0;
    
    private static final String AMADEUS_AUTH_URL = "https://test.api.amadeus.com/v1/security/oauth2/token";
    // ⭐ CORREGIDO: Endpoint v3 de Transfer Search API
    private static final String AMADEUS_TRANSFER_URL = "https://test.api.amadeus.com/v3/shopping/transfer-offers";
    
    public AmadeusConnect(
            @Value("${amadeus.client.id}") String clientId,
            @Value("${amadeus.client.secret}") String clientSecret) {
        
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.gson = new Gson();
        
        logger.info("=== INICIALIZANDO AMADEUS ===");
        logger.info("Client ID: {}", clientId != null ? clientId.substring(0, Math.min(8, clientId.length())) + "..." : "null");
        
        try {
            this.amadeus = Amadeus.builder(clientId.trim(), clientSecret.trim())
                .setHostname("test")
                .build();
                
            logger.info("✅ Amadeus SDK inicializado correctamente en modo TEST");
            getAccessToken();
            logger.info("✅ Token de acceso obtenido para Transfer API");
            
        } catch (Exception e) {
            logger.error("❌ Error al inicializar Amadeus: {}", e.getMessage(), e);
            throw new RuntimeException("Error al inicializar Amadeus", e);
        }
    }
    
    private String getAccessToken() {
        try {
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
                tokenExpiration = System.currentTimeMillis() + ((expiresIn - 300) * 1000L);
                logger.info("✅ Token obtenido exitosamente");
                return accessToken;
            }
            
            throw new RuntimeException("Error al obtener token de acceso");
            
        } catch (Exception e) {
            logger.error("❌ Error al obtener token: {}", e.getMessage());
            throw new RuntimeException("Error de autenticación con Amadeus", e);
        }
    }
    
    public boolean testAuthentication() {
        try {
            logger.info("🔐 Probando autenticación...");
            Location[] locations = amadeus.referenceData.locations.get(
                Params.with("keyword", "LON").and("subType", Locations.AIRPORT)
            );
            logger.info("✅ Autenticación exitosa - {} ubicaciones encontradas", locations.length);
            return true;
        } catch (Exception e) {
            logger.error("❌ Fallo en autenticación: {}", e.getMessage());
            return false;
        }
    }
    
    // VUELOS
    public Location[] location(String keyword) throws ResponseException {
        logger.info("🔍 Buscando ubicaciones para: {}", keyword);
        try {
            Location[] locations = amadeus.referenceData.locations.get(
                Params.with("keyword", keyword).and("subType", Locations.AIRPORT)
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
        
        logger.info("✈️ Búsqueda de vuelo SOLO IDA: {} -> {}, Salida: {}, Adultos: {}, Max: {}", 
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
                e.getResponse() != null ? e.getResponse().getStatusCode() : "N/A", e.getMessage());
            throw e;
        }
    }
    
    public FlightOfferSearch[] searchRoundTripFlights(String originLocationCode, 
                                                     String destinationLocationCode, 
                                                     String departureDate,
                                                     String returnDate,
                                                     int adults, 
                                                     int max) throws ResponseException {
        
        logger.info("🔄 Búsqueda de vuelo IDA Y VUELTA: {} -> {}, Salida: {}, Regreso: {}", 
                   originLocationCode, destinationLocationCode, departureDate, returnDate);
        
        try {
            if (returnDate == null || returnDate.trim().isEmpty()) {
                throw new IllegalArgumentException("La fecha de regreso es requerida");
            }
            
            Params params = Params.with("originLocationCode", originLocationCode.trim())
                .and("destinationLocationCode", destinationLocationCode.trim())
                .and("departureDate", departureDate.trim())
                .and("returnDate", returnDate.trim())
                .and("adults", adults)
                .and("max", max)
                .and("currencyCode", "USD");
            
            FlightOfferSearch[] flightOffers = amadeus.shopping.flightOffersSearch.get(params);
            logger.info("✅ Encontrados {} vuelos", flightOffers.length);
            return flightOffers;
            
        } catch (ResponseException e) {
            logger.error("❌ Error Amadeus API: {}", e.getMessage());
            throw e;
        }
    }
    
    // HOTELES
    public Hotel[] searchHotelsByCity(String cityCode) throws ResponseException {
        logger.info("🏨 Búsqueda de hoteles en ciudad: {}", cityCode);
        try {
            Hotel[] hotels = amadeus.referenceData.locations.hotels.byCity.get(
                Params.with("cityCode", cityCode.trim())
            );
            logger.info("✅ Encontrados {} hoteles", hotels.length);
            return hotels;
        } catch (ResponseException e) {
            logger.error("❌ Error Amadeus API: {}", e.getMessage());
            throw e;
        }
    }
    
    public Hotel[] searchHotelsByGeoCode(double latitude, double longitude, Integer radius) throws ResponseException {
        logger.info("🏨 Búsqueda de hoteles por geocode: Lat: {}, Lon: {}", latitude, longitude);
        try {
            Params params = Params.with("latitude", latitude).and("longitude", longitude);
            if (radius != null && radius > 0) {
                params.and("radius", radius).and("radiusUnit", "KM");
            }
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
            Hotel[] hotels = amadeus.referenceData.locations.hotels.byHotels.get(
                Params.with("hotelIds", hotelIdsStr)
            );
            logger.info("✅ Encontrados {} hoteles", hotels.length);
            return hotels;
        } catch (ResponseException e) {
            logger.error("❌ Error Amadeus API: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * ⭐ NUEVO MÉTODO CORREGIDO - Buscar transfers usando Transfer Search API v3
     * 
     * Documentación: https://developers.amadeus.com/self-service/category/cars-and-transfers/api-doc/transfer-search
     * 
     * @param airportCode Código IATA del aeropuerto (ej: "ATH", "MAD")
     * @param cityName Nombre de la ciudad destino (ej: "Athens", "Madrid")
     * @param countryCode Código ISO del país (ej: "GR", "ES")
     * @param dateTime Fecha y hora ISO 8601 (ej: "2025-12-15T10:00:00")
     * @param passengers Número de pasajeros (1-9)
     * @return Array de TransferOffering
     */
    public TransferOffering[] searchAirportTransfers(
            String airportCode,
            String cityName,
            String countryCode,
            String dateTime,
            int passengers) throws ResponseException {
        
        logger.info("🚗 Búsqueda de transfers: {} -> {} ({}), Fecha: {}, Pasajeros: {}", 
                   airportCode, cityName, countryCode, dateTime, passengers);
        
        try {
            String token = getAccessToken();
            
            // ⭐ PARÁMETROS REQUERIDOS según documentación oficial
            StringBuilder urlBuilder = new StringBuilder(AMADEUS_TRANSFER_URL);
            urlBuilder.append("?startLocationCode=").append(airportCode.trim());
            
            // Endpoint puede usar cityName o addressLine - probamos con addressLine
            urlBuilder.append("&endAddressLine=").append(cityName.trim().replace(" ", "%20"));
            urlBuilder.append("&endCountryCode=").append(countryCode.trim());
            
            // Fecha en formato ISO 8601
            urlBuilder.append("&transferType=PRIVATE");
            urlBuilder.append("&startDateTime=").append(dateTime.trim());
            urlBuilder.append("&passengers=").append(passengers);
            
            String url = urlBuilder.toString();
            logger.info("📡 URL Request: {}", url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/vnd.amadeus+json");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, JsonNode.class
            );
            
            logger.info("📥 Status Code: {}", response.getStatusCode());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode result = response.getBody();
                
                logger.info("📦 Response Body: {}", result.toString());
                
                if (result.has("data")) {
                    JsonNode data = result.get("data");
                    List<TransferOffering> offerings = new ArrayList<>();
                    
                    for (JsonNode item : data) {
                        try {
                            TransferOffering offering = gson.fromJson(item.toString(), TransferOffering.class);
                            offerings.add(offering);
                        } catch (Exception e) {
                            logger.warn("⚠️ Error parseando transfer: {}", e.getMessage());
                        }
                    }
                    
                    logger.info("✅ Encontrados {} transfers", offerings.size());
                    return offerings.toArray(new TransferOffering[0]);
                }
                
                // Si no hay data, verificar si hay meta con warnings
                if (result.has("meta")) {
                    logger.info("ℹ️ Meta info: {}", result.get("meta").toString());
                }
            }
            
            logger.warn("⚠️ No se encontraron transfers - Status: {}", response.getStatusCode());
            return new TransferOffering[0];
            
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            logger.error("❌ Error HTTP {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            
            // Parsear el error de Amadeus
            try {
                JsonNode errorResponse = objectMapper.readTree(e.getResponseBodyAsString());
                if (errorResponse.has("errors")) {
                    JsonNode errors = errorResponse.get("errors");
                    for (JsonNode error : errors) {
                        logger.error("❌ Amadeus Error: Code={}, Title={}, Detail={}", 
                            error.has("code") ? error.get("code").asInt() : "N/A",
                            error.has("title") ? error.get("title").asText() : "N/A",
                            error.has("detail") ? error.get("detail").asText() : "N/A"
                        );
                    }
                }
            } catch (Exception parseError) {
                logger.error("Error parsing Amadeus error response", parseError);
            }
            
            throw new RuntimeException("Error en Amadeus Transfer API: " + e.getMessage(), e);
            
        } catch (Exception e) {
            logger.error("❌ Error inesperado en búsqueda de transfers: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar transfers: " + e.getMessage(), e);
        }
    }
}