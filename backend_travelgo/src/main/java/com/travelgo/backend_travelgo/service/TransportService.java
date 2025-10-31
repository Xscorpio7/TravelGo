package com.travelgo.backend_travelgo.service;

import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.TransferOffering;
import com.google.gson.Gson;
import com.travelgo.backend_travelgo.model.Transporte;
import com.travelgo.backend_travelgo.repository.TransporteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransportService {
    
    private static final Logger logger = LoggerFactory.getLogger(TransportService.class);
    
    @Autowired
    private TransporteRepository transporteRepository;
    
    @Autowired
    private AmadeusConnect amadeusConnect;
    
    private final Gson gson = new Gson();
    
    /**
     * Buscar transfers desde Amadeus y guardarlos en la BD
     */
    @Transactional
    public List<Transporte> buscarYGuardarTransfers(String airportCode, String cityName, 
                                                    String countryCode, String dateTime, 
                                                    int passengers) {
        logger.info("🔍 Buscando transfers desde aeropuerto: {} a {}", airportCode, cityName);
        
        try {
            // Buscar en Amadeus
            TransferOffering[] offerings = amadeusConnect.searchAirportTransfers(
                airportCode, cityName, countryCode, dateTime, passengers
            );
            
            List<Transporte> transportes = new ArrayList<>();
            
            for (TransferOffering offering : offerings) {
                Transporte transporte = convertirAmadeusATransporte(offering, airportCode, cityName);
                
                // Verificar si ya existe
                Optional<Transporte> existente = transporteRepository.findByTransferId(
                    transporte.getTransferId()
                );
                
                if (existente.isEmpty()) {
                    Transporte guardado = transporteRepository.save(transporte);
                    transportes.add(guardado);
                    logger.info("✅ Transfer guardado: {} - {}", guardado.getId(), guardado.getDescripcion());
                } else {
                    transportes.add(existente.get());
                    logger.info("ℹ️ Transfer ya existe: {}", existente.get().getId());
                }
            }
            
            return transportes;
            
        } catch (ResponseException e) {
            logger.error("❌ Error Amadeus: {}", e.getMessage());
            throw new RuntimeException("Error al buscar transfers en Amadeus: " + e.getMessage());
        }
    }
    
    /**
     * Convertir TransferOffering de Amadeus a Transporte
     */
    private Transporte convertirAmadeusATransporte(TransferOffering offering, 
                                                   String origen, String destino) {
        Transporte transporte = new Transporte();
        
        try {
            transporte.setTipo(Transporte.Tipo.Transfer);
            transporte.setTransferId(offering.getId());
            transporte.setOrigen(origen);
            transporte.setDestino(destino);
            
            // Extraer información del offering
            if (offering.getServiceProvider() != null) {
                transporte.setProveedor(offering.getServiceProvider().getName());
            }
            
            // Precio
            if (offering.getQuotation() != null && offering.getQuotation().getMonetaryAmount() != null) {
                transporte.setPrecio(new BigDecimal(offering.getQuotation().getMonetaryAmount()));
                transporte.setCurrency(offering.getQuotation().getCurrencyCode());
            }
            
            // Vehículo
            if (offering.getVehicle() != null) {
                transporte.setVehiculoTipo(offering.getVehicle().getCode());
                transporte.setCapacidad(offering.getVehicle().getSeats());
                
                // Descripción del vehículo
                StringBuilder desc = new StringBuilder();
                if (offering.getVehicle().getDescription() != null) {
                    desc.append(offering.getVehicle().getDescription());
                }
                if (offering.getVehicle().getImageURL() != null) {
                    desc.append(" | Imagen: ").append(offering.getVehicle().getImageURL());
                }
                transporte.setDescripcion(desc.toString());
            }
            
            // Distancia
            if (offering.getDistance() != null && offering.getDistance().getValue() != null) {
                transporte.setDistancia(new BigDecimal(offering.getDistance().getValue()));
            }
            
            // Duración estimada (si está disponible)
            if (offering.getTransferType() != null) {
                transporte.setCategoria(offering.getTransferType());
            }
            
            // Guardar JSON completo
            transporte.setTransferDetails(gson.toJson(offering));
            transporte.setDetallesJson(gson.toJson(offering));
            
            transporte.setEstado(Transporte.Estado.disponible);
            
        } catch (Exception e) {
            logger.error("❌ Error al convertir transfer: {}", e.getMessage());
        }
        
        return transporte;
    }
    
    /**
     * Buscar todos los transportes disponibles
     */
    public List<Transporte> buscarDisponibles() {
        return transporteRepository.findByEstado(Transporte.Estado.disponible);
    }
    
    /**
     * Buscar por tipo y estado disponible
     */
    public List<Transporte> buscarDisponiblesPorTipo(Transporte.Tipo tipo) {
        return transporteRepository.findByTipoAndEstado(tipo, Transporte.Estado.disponible);
    }
    
    /**
     * Buscar por origen
     */
    public List<Transporte> buscarPorOrigen(String origen) {
        return transporteRepository.findByOrigenContainingIgnoreCase(origen);
    }
    
    /**
     * Buscar por destino
     */
    public List<Transporte> buscarPorDestino(String destino) {
        return transporteRepository.findByDestinoContainingIgnoreCase(destino);
    }
    
    /**
     * Buscar por origen y destino
     */
    public List<Transporte> buscarPorOrigenDestino(String origen, String destino) {
        return transporteRepository.findByOrigenAndDestino(origen, destino);
    }
    
    /**
     * Buscar por origen, destino y tipo
     */
    public List<Transporte> buscarPorOrigenDestinoTipo(String origen, String destino, 
                                                       Transporte.Tipo tipo) {
        return transporteRepository.findByOrigenDestinoAndTipo(origen, destino, tipo);
    }
    
    /**
     * Buscar transfers de aeropuerto en una ciudad
     */
    public List<Transporte> buscarTransferAeropuerto(String ciudad) {
        return transporteRepository.findTransfersByCiudad(ciudad);
    }
    
    /**
     * Obtener por ID
     */
    public Optional<Transporte> obtenerPorId(Integer id) {
        return transporteRepository.findById(id);
    }
    
    /**
     * Reservar transporte
     */
    @Transactional
    public Transporte reservarTransporte(Integer id) {
        Transporte transporte = transporteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transporte no encontrado"));
        
        if (transporte.getEstado() != Transporte.Estado.disponible) {
            throw new RuntimeException("Transporte no disponible");
        }
        
        transporte.setEstado(Transporte.Estado.reservado);
        return transporteRepository.save(transporte);
    }
    
    /**
     * Cancelar reserva
     */
    @Transactional
    public Transporte cancelarReserva(Integer id) {
        Transporte transporte = transporteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transporte no encontrado"));
        
        if (transporte.getEstado() != Transporte.Estado.reservado) {
            throw new RuntimeException("El transporte no está reservado");
        }
        
        transporte.setEstado(Transporte.Estado.disponible);
        return transporteRepository.save(transporte);
    }
    
    /**
     * Crear transporte manualmente
     */
    @Transactional
    public Transporte crearTransporte(Transporte transporte) {
        if (transporte.getEstado() == null) {
            transporte.setEstado(Transporte.Estado.disponible);
        }
        return transporteRepository.save(transporte);
    }
    
    /**
     * Actualizar transporte
     */
    @Transactional
    public Transporte actualizarTransporte(Integer id, Transporte transporteDetails) {
        Transporte transporte = transporteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transporte no encontrado"));
        
        if (transporteDetails.getTipo() != null) {
            transporte.setTipo(transporteDetails.getTipo());
        }
        if (transporteDetails.getProveedor() != null) {
            transporte.setProveedor(transporteDetails.getProveedor());
        }
        if (transporteDetails.getOrigen() != null) {
            transporte.setOrigen(transporteDetails.getOrigen());
        }
        if (transporteDetails.getDestino() != null) {
            transporte.setDestino(transporteDetails.getDestino());
        }
        if (transporteDetails.getPrecio() != null) {
            transporte.setPrecio(transporteDetails.getPrecio());
        }
        if (transporteDetails.getEstado() != null) {
            transporte.setEstado(transporteDetails.getEstado());
        }
        if (transporteDetails.getCapacidad() != null) {
            transporte.setCapacidad(transporteDetails.getCapacidad());
        }
        if (transporteDetails.getDescripcion() != null) {
            transporte.setDescripcion(transporteDetails.getDescripcion());
        }
        
        return transporteRepository.save(transporte);
    }
    
    /**
     * Eliminar transporte
     */
    @Transactional
    public void eliminarTransporte(Integer id) {
        if (!transporteRepository.existsById(id)) {
            throw new RuntimeException("Transporte no encontrado");
        }
        transporteRepository.deleteById(id);
    }
    
    /**
     * Calcular precio estimado basado en distancia y tipo
     */
    public BigDecimal calcularPrecioEstimado(String origen, String destino, Transporte.Tipo tipo) {
        // Buscar transportes similares
        List<Transporte> similares = buscarPorOrigenDestinoTipo(origen, destino, tipo);
        
        if (!similares.isEmpty()) {
            // Calcular promedio
            BigDecimal total = BigDecimal.ZERO;
            int count = 0;
            for (Transporte t : similares) {
                if (t.getPrecio() != null) {
                    total = total.add(t.getPrecio());
                    count++;
                }
            }
            if (count > 0) {
                return total.divide(new BigDecimal(count), 2, BigDecimal.ROUND_HALF_UP);
            }
        }
        
        // Precios base por tipo si no hay datos
        switch (tipo) {
            case Transfer:
                return new BigDecimal("50.00");
            case Taxi:
                return new BigDecimal("30.00");
            case Bus:
                return new BigDecimal("15.00");
            case Tren:
                return new BigDecimal("25.00");
            case Auto_Rental:
                return new BigDecimal("45.00");
            default:
                return new BigDecimal("20.00");
        }
    }
}