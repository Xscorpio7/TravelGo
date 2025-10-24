package com.travelgo.backend_travelgo.service;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.Location;
import com.amadeus.resources.FlightOfferSearch;
import com.amadeus.resources.FlightPrice;
import com.amadeus.resources.HotelOfferSearch;
import com.amadeus.resources.TransferOffering;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.referencedata.Locations;
import com.amadeus.resources.TransferOffersPost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
                
            logger.info("Amadeus inicializado correctamente en modo TEST");
            
        } catch (Exception e) {
            logger.error("Error al inicializar Amadeus: {}", e.getMessage(), e);
            throw new RuntimeException("Error al inicializar Amadeus", e);
        }
    }
    
    /**
     * Test de autenticación
     */
    public boolean testAuthentication() {
        try {
            logger.info("Probando autenticación...");
            
            Location[] locations = amadeus.referenceData.locations.get(
                Params.with("keyword", "LON")
                    .and("subType", Locations.AIRPORT)
            );
            
            logger.info("Autenticación exitosa - {} ubicaciones encontradas", locations.length);
            return true;
            
        } catch (Exception e) {
            logger.error("Fallo en autenticación: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Buscar ubicaciones/aeropuertos
     */
    public Location[] location(String keyword) throws ResponseException {
        logger.info("🔍 Buscando ubicaciones para: {}", keyword);
        
        try {
            Location[] locations = amadeus.referenceData.locations.get(
                Params.with("keyword", keyword)
                    .and("subType", Locations.AIRPORT)
            );
            
            logger.info("Encontradas {} ubicaciones", locations.length);
            return locations;
            
        } catch (ResponseException e) {
            logger.error("Error en búsqueda de ubicaciones: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar vuelos SOLO IDA (sin fecha de regreso)
     */
    public FlightOfferSearch[] searchFlights(String originLocationCode, 
                                           String destinationLocationCode, 
                                           String departureDate, 
                                           int adults, 
                                           int max) throws ResponseException {
        
        logger.info("Búsqueda de vuelo SOLO IDA:");
        logger.info("   {} -> {}, Salida: {}, Adultos: {}, Max: {}", 
                   originLocationCode, destinationLocationCode, departureDate, adults, max);
        
        try {
            Params params = Params.with("originLocationCode", originLocationCode.trim())
                .and("destinationLocationCode", destinationLocationCode.trim())
                .and("departureDate", departureDate.trim())
                .and("adults", adults)
                .and("max", max);
            
            logger.info("Llamando a Amadeus API (solo ida)...");
            
            FlightOfferSearch[] flightOffers = amadeus.shopping.flightOffersSearch.get(params);
            
            logger.info("Encontrados {} vuelos de ida", flightOffers.length);
            return flightOffers;
            
        } catch (ResponseException e) {
            logger.error("Error Amadeus API: Status={}, Message={}", 
                e.getResponse() != null ? e.getResponse().getStatusCode() : "N/A",
                e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            throw new RuntimeException("Error en búsqueda de vuelos", e);
        }
    }
    
    /**
     * Buscar vuelos IDA Y VUELTA (con fecha de regreso)
     */
    public FlightOfferSearch[] searchRoundTripFlights(String originLocationCode, 
                                                     String destinationLocationCode, 
                                                     String departureDate,
                                                     String returnDate,
                                                     int adults, 
                                                     int max) throws ResponseException {
        
        logger.info("Búsqueda de vuelo IDA Y VUELTA:");
        logger.info("   {} -> {}, Salida: {}, Regreso: {}, Adultos: {}, Max: {}", 
                   originLocationCode, destinationLocationCode, departureDate, returnDate, adults, max);
        
        try {
            // Validar que returnDate sea posterior a departureDate
            if (returnDate != null && !returnDate.trim().isEmpty()) {
                if (returnDate.compareTo(departureDate) <= 0) {
                    throw new IllegalArgumentException(
                        "La fecha de regreso debe ser posterior a la fecha de salida");
                }
            }
            
            Params params = Params.with("originLocationCode", originLocationCode.trim())
                .and("destinationLocationCode", destinationLocationCode.trim())
                .and("departureDate", departureDate.trim())
                .and("returnDate", returnDate.trim())  // Fecha de regreso
                .and("adults", adults)
                .and("max", max);
            
            logger.info("Llamando a Amadeus API (ida y vuelta)...");
            
            FlightOfferSearch[] flightOffers = amadeus.shopping.flightOffersSearch.get(params);
            
            logger.info("Encontrados {} vuelos de ida y vuelta", flightOffers.length);
            return flightOffers;
            
        } catch (ResponseException e) {
            logger.error("Error Amadeus API: Status={}, Message={}", 
                e.getResponse() != null ? e.getResponse().getStatusCode() : "N/A",
                e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Error de validación: {}", e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            throw new RuntimeException("Error en búsqueda de vuelos", e);
        }
    }
   public FlightPrice confirmFlightPrice(String flightOfferJson) throws ResponseException {
    logger.info("Confirmando precio de vuelo");

    try {
        // Llamada al endpoint de Amadeus
        FlightPrice priceConfirmation = amadeus.shopping.flightOffersSearch.pricing.post(flightOfferJson);

        if (priceConfirmation != null && priceConfirmation.getResponse() != null) {
            logger.info("Precio confirmado exitosamente");
            return priceConfirmation;
        } else {
            logger.warn("No se pudo confirmar el precio: respuesta vacía o nula");
            throw new RuntimeException("No se pudo confirmar el precio del vuelo");
        }

    } catch (ResponseException e) {
        logger.error("Error al confirmar precio: {}", e.getMessage());
        throw e; // Se relanza para ser manejado en una capa superior
    } catch (Exception e) {
        logger.error("Error inesperado al confirmar precio: {}", e.getMessage());
        throw new RuntimeException("Error inesperado al confirmar precio: " + e.getMessage(), e);
    }
}

    
    /**
     * Buscar hoteles por ciudad
     */
    public HotelOfferSearch[] searchHotels(String cityCode, 
                                          String checkInDate, 
                                          String checkOutDate,
                                          int adults,
                                          int roomQuantity) throws ResponseException {
        logger.info("Búsqueda de hoteles en ciudad: {}", cityCode);
        logger.info("   Check-in: {}, Check-out: {}, Adultos: {}, Habitaciones: {}", 
                   checkInDate, checkOutDate, adults, roomQuantity);
        
        try {
            Params params = Params.with("cityCode", cityCode.trim())
                .and("checkInDate", checkInDate.trim())
                .and("checkOutDate", checkOutDate.trim())
                .and("adults", adults)
                .and("roomQuantity", roomQuantity);
            
            logger.info("Llamando a Amadeus Hotel Search API...");
            
            HotelOfferSearch[] hotelOffers = amadeus.shopping.hotelOffersSearch.get(params);
            
            logger.info("Encontrados {} hoteles", hotelOffers.length);
            return hotelOffers;
            
        } catch (ResponseException e) {
            logger.error("Error en búsqueda de hoteles: Status={}, Message={}", 
                e.getResponse() != null ? e.getResponse().getStatusCode() : "N/A",
                e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar transfers/transporte
     */
    public TransferOffersPost[] searchTransfers(String startLocationCode, 
                                  String endLocationCode,
                                  String transferDate,
                                  String transferTime,
                                  int passengers) throws ResponseException {
        logger.info("Búsqueda de transfers: {} -> {}", startLocationCode, endLocationCode);
        logger.info("   Fecha: {}, Hora: {}, Pasajeros: {}", transferDate, transferTime, passengers);
        
        try {
            // Construir el JSON manualmente para el POST de transfers
            JsonObject startLocation = new JsonObject();
            startLocation.addProperty("locationCode", startLocationCode.trim());
            
            JsonObject endLocation = new JsonObject();
            endLocation.addProperty("locationCode", endLocationCode.trim());
            
            JsonObject body = new JsonObject();
            body.add("startLocationCode", startLocation);
            body.add("endLocationCode", endLocation);
            body.addProperty("transferDate", transferDate.trim());
            body.addProperty("transferTime", transferTime.trim());
            body.addProperty("passengers", passengers);
            
            logger.info("Llamando a Amadeus Transfer Search API...");
            
          
            TransferOffersPost[] result = amadeus.shopping.transferOffers.post(body.toString());
            
            logger.info("Búsqueda de transfers exitosa");
            return result;
            
        } catch (ResponseException e) {
            logger.error("Error en búsqueda de transfers: Status={}, Message={}", 
                e.getResponse() != null ? e.getResponse().getStatusCode() : "N/A",
                e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado en búsqueda de transfers: {}", e.getMessage(), e);
            throw new RuntimeException("Error en búsqueda de transfers", e);
        }
    }
}