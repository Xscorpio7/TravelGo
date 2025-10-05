package com.travelgo.backend_travelgo.service;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.Location;
import com.amadeus.resources.FlightOfferSearch;
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
                .setHostname("test")  // Ambiente de TEST
                .build();
                
            logger.info("✅ Amadeus inicializado correctamente en modo TEST");
            
        } catch (Exception e) {
            logger.error("❌ Error al inicializar Amadeus: {}", e.getMessage(), e);
            throw new RuntimeException("Error al inicializar Amadeus", e);
        }
    }
    
    /**
     * Test simple de autenticación
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
    
    /**
     * Buscar ubicaciones
     */
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
        } catch (Exception e) {
            logger.error("❌ Error inesperado en ubicaciones: {}", e.getMessage(), e);
            throw new RuntimeException("Error en búsqueda de ubicaciones", e);
        }
    }
    
    /**
     * Buscar vuelos
     */
    public FlightOfferSearch[] searchFlights(String originLocationCode, 
                                           String destinationLocationCode, 
                                           String departureDate, 
                                           int adults, 
                                           int max) throws ResponseException {
        
        logger.info("🛫 Iniciando búsqueda de vuelos...");
        logger.info("   Origen: {}", originLocationCode);
        logger.info("   Destino: {}", destinationLocationCode); 
        logger.info("   Fecha: {}", departureDate);
        logger.info("   Adultos: {}", adults);
        logger.info("   Max resultados: {}", max);
        
        try {
            // Validaciones básicas
            if (originLocationCode == null || originLocationCode.trim().isEmpty()) {
                throw new IllegalArgumentException("Origin location code no puede estar vacío");
            }
            
            if (destinationLocationCode == null || destinationLocationCode.trim().isEmpty()) {
                throw new IllegalArgumentException("Destination location code no puede estar vacío");
            }
            
            if (departureDate == null || departureDate.trim().isEmpty()) {
                throw new IllegalArgumentException("Departure date no puede estar vacío");
            }
            
            logger.info("📡 Llamando a Amadeus API...");
            
            FlightOfferSearch[] flightOffers = amadeus.shopping.flightOffersSearch.get(
                Params.with("originLocationCode", originLocationCode.trim())
                    .and("destinationLocationCode", destinationLocationCode.trim())
                    .and("departureDate", departureDate.trim())
                    .and("adults", adults)
                    .and("max", max)
            );
            
            logger.info("✅ Amadeus API respondió exitosamente");
            logger.info("✅ Se encontraron {} ofertas de vuelo", flightOffers.length);
            
            return flightOffers;
            
        } catch (ResponseException e) {
            logger.error("❌ Error de Amadeus API:");
            logger.error("   Status Code: {}", e.getResponse() != null ? e.getResponse().getStatusCode() : "N/A");
            logger.error("   Message: {}", e.getMessage());
            logger.error("   Body: {}", e.getResponse() != null ? e.getResponse().getBody() : "N/A");
            throw e;
            
        } catch (IllegalArgumentException e) {
            logger.error("❌ Error de validación: {}", e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
            
        } catch (Exception e) {
            logger.error("❌ Error inesperado en búsqueda de vuelos: {}", e.getMessage(), e);
            throw new RuntimeException("Error interno en búsqueda de vuelos", e);
        }
    }
}