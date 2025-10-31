package com.travelgo.backend_travelgo.service;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.Location;
import com.amadeus.resources.FlightOfferSearch;
import com.amadeus.resources.Hotel;
import com.amadeus.resources.TransferOffering;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.referencedata.Locations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AmadeusConnect {
    
    private static final Logger logger = LoggerFactory.getLogger(AmadeusConnect.class);
    private final Amadeus amadeus;
    
    public AmadeusConnect(
            @Value("${amadeus.client.id}") String clientId,
            @Value("${amadeus.client.secret}") String clientSecret) {
        
        logger.info("=== INICIALIZANDO AMADEUS ===");
        logger.info("Client ID: {}", clientId != null ? clientId.substring(0, Math.min(8, clientId.length())) + "..." : "null");
        
        try {
            this.amadeus = Amadeus.builder(clientId.trim(), clientSecret.trim())
                .setHostname("test")
                .build();
                
            logger.info("✅ Amadeus inicializado correctamente en modo TEST");
            
        } catch (Exception e) {
            logger.error("❌ Error al inicializar Amadeus: {}", e.getMessage(), e);
            throw new RuntimeException("Error al inicializar Amadeus", e);
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
    // VUELOS (Ya implementado)
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
    // HOTELES (Ya implementado)
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
    
}