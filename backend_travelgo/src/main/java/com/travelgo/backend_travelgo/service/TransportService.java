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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio completo para gestión de Transfers
 * Integración con Amadeus Transfer Search API
 */
@Service
public class TransportService {
    
    private static final Logger logger = LoggerFactory.getLogger(TransportService.class);
    
    @Autowired
    private TransporteRepository transporteRepository;
    
    @Autowired
    private AmadeusConnect amadeusConnect;
    
    private final Gson gson = new GsonBuilder()
    .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> 
        new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
    .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> 
        LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    .create();
    
   /**
 * Buscar transfers desde Amadeus y guardarlos en la BD
 * Si Amadeus no tiene datos, busca en BD local
 * 
 * @param airportCode Código IATA del aeropuerto
 * @param cityName Nombre de la ciudad
 * @param countryCode Código ISO del país
 * @param dateTime Fecha y hora ISO 8601
 * @param passengers Número de pasajeros
 * @return Lista de transfers encontrados y guardados
 */
@Transactional
public List<Transporte> buscarYGuardarTransfers(String airportCode, String cityName, 
                                                String countryCode, String dateTime, 
                                                int passengers) {
    logger.info("🔍 Buscando transfers: {} -> {} ({}) - {} pasajeros", 
               airportCode, cityName, countryCode, passengers);
    
    try {
        // 1. Intentar buscar en Amadeus
        TransferOffering[] offerings = amadeusConnect.searchAirportTransfers(
            airportCode, cityName, countryCode, dateTime, passengers
        );
        
        if (offerings != null && offerings.length > 0) {
            logger.info("✅ Amadeus devolvió {} transfers", offerings.length);
            
            List<Transporte> transportes = new ArrayList<>();
            
            // 2. Convertir y guardar cada transfer
            for (TransferOffering offering : offerings) {
                try {
                    Transporte transporte = convertirAmadeusATransporte(offering, airportCode, cityName);
                    
                    // Verificar si ya existe por transfer_id
                    Optional<Transporte> existente = transporteRepository.findByTransferId(
                        transporte.getTransferId()
                    );
                    
                    if (existente.isEmpty()) {
                        Transporte guardado = transporteRepository.save(transporte);
                        transportes.add(guardado);
                        logger.debug("💾 Transfer guardado: ID={}", guardado.getId());
                    } else {
                        transportes.add(existente.get());
                        logger.debug("ℹ️ Transfer ya existe: ID={}", existente.get().getId());
                    }
                    
                } catch (Exception e) {
                    logger.error("❌ Error al procesar transfer individual: {}", e.getMessage());
                }
            }
            
            logger.info("✅ Total de transfers de Amadeus: {}", transportes.size());
            return transportes;
        }
        
        // 3. Si Amadeus no tiene datos, buscar en BD local
        logger.warn("⚠️ Amadeus no tiene transfers para {} -> {}. Buscando en BD local...", 
                   airportCode, cityName);
        
    } catch (ResponseException e) {
        logger.error("❌ Error Amadeus API: {}", e.getMessage());
        logger.info("🔄 Fallback: Buscando en BD local por error de Amadeus...");
        
    } catch (Exception e) {
        logger.error("❌ Error inesperado: {}", e.getMessage());
        logger.info("🔄 Fallback: Buscando en BD local...");
    }
    
    // 4. Buscar en BD local (fallback)
    List<Transporte> locales = buscarTransfersLocales(airportCode, cityName);
    
    if (!locales.isEmpty()) {
        logger.info("✅ Encontrados {} transfers en BD local", locales.size());
        return locales;
    }
    
    logger.warn("⚠️ No se encontraron transfers ni en Amadeus ni en BD local");
    return new ArrayList<>();
}

/**
 * Buscar transfers en BD local por origen/destino
 */
private List<Transporte> buscarTransfersLocales(String origen, String destino) {
    logger.debug("🔍 Buscando transfers locales: {} -> {}", origen, destino);
    
    // Buscar por origen
    List<Transporte> porOrigen = transporteRepository.findByOrigenContainingIgnoreCase(origen);
    
    if (!porOrigen.isEmpty()) {
        // Filtrar por tipo Transfer y estado disponible
        List<Transporte> filtrados = porOrigen.stream()
            .filter(t -> t.getTipo() == Transporte.Tipo.Transfer)
            .filter(t -> t.getEstado() == Transporte.Estado.disponible)
            .filter(t -> t.getDestino() != null && 
                   t.getDestino().toLowerCase().contains(destino.toLowerCase()))
            .collect(java.util.stream.Collectors.toList());
        
        logger.debug("📦 Encontrados {} transfers locales filtrados", filtrados.size());
        return filtrados;
    }
    
    logger.debug("⚠️ No se encontraron transfers locales");
    return new ArrayList<>();
}

    
    /**
     * Convertir TransferOffering de Amadeus a entidad Transporte
     * 
     * @param offering Oferta de Amadeus
     * @param origen Código del aeropuerto
     * @param destino Nombre de la ciudad
     * @return Entidad Transporte lista para guardar
     */
    private Transporte convertirAmadeusATransporte(TransferOffering offering, 
                                                   String origen, String destino) {
        Transporte transporte = new Transporte();
        
        try {
            // Tipo siempre Transfer
            transporte.setTipo(Transporte.Tipo.Transfer);
            
            // IDs
            transporte.setTransferId(offering.getId());
            transporte.setAmadeusId(offering.getId());
            
            // Ubicaciones
            transporte.setOrigen(origen);
            transporte.setDestino(destino);
            
            // Proveedor
            if (offering.getServiceProvider() != null) {
                transporte.setProveedor(offering.getServiceProvider().getName());
            } else {
                transporte.setProveedor("Amadeus Transfer Service");
            }
            
            // Precio
            if (offering.getQuotation() != null) {
                // Precio total
                if (offering.getQuotation().getTotalPrice() != null) {
                    String precioStr = offering.getQuotation().getTotalPrice().getMonetaryAmount();
                    transporte.setPrecio(new BigDecimal(precioStr));
                    transporte.setCurrency(offering.getQuotation().getTotalPrice().getCurrencyCode());
                } 
                // Si no hay precio total, usar precio base
                else if (offering.getQuotation().getBase() != null) {
                    String precioStr = offering.getQuotation().getBase().getMonetaryAmount();
                    transporte.setPrecio(new BigDecimal(precioStr));
                    transporte.setCurrency(offering.getQuotation().getBase().getCurrencyCode());
                }
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
                
                // Agregar características
                if (offering.getVehicle().getCharacteristics() != null) {
                    TransferOffering.Vehicle.VehicleCharacteristics chars = 
                        offering.getVehicle().getCharacteristics();
                    
                    if (Boolean.TRUE.equals(chars.getAirConditioning())) {
                        desc.append(" | Aire acondicionado");
                    }
                    if (chars.getMaxBaggages() != null) {
                        desc.append(" | Equipaje: ").append(chars.getMaxBaggages()).append(" maletas");
                    }
                }
                
                transporte.setDescripcion(desc.toString());
                
                // Categoría
                if (offering.getVehicle().getCategory() != null) {
                    transporte.setCategoria(offering.getVehicle().getCategory());
                }
            }
            
            // Distancia
            if (offering.getDistance() != null && offering.getDistance().getValue() != null) {
                transporte.setDistancia(new BigDecimal(offering.getDistance().getValue()));
            }
            
            // Duración estimada (si está disponible)
            // Nota: Amadeus no siempre proporciona duración, podemos estimarla
            if (transporte.getDistancia() != null) {
                // Estimación: 40 km/h promedio en ciudad
                double distanciaKm = transporte.getDistancia().doubleValue();
                int duracionMinutos = (int) Math.ceil((distanciaKm / 40.0) * 60);
                transporte.setDuracionMinutos(duracionMinutos);
            }
            
            // Guardar JSON completo para referencia
            transporte.setTransferDetails(gson.toJson(offering));
            transporte.setDetallesJson(gson.toJson(offering));
            
            // Tipo de transfer
            if (offering.getTransferType() != null) {
                transporte.setCategoria(offering.getTransferType());
            }
            
            // Estado inicial
            transporte.setEstado(Transporte.Estado.disponible);
            
            // Fechas (opcional, basado en la búsqueda)
            if (offering.getStart() != null && offering.getStart().getDateTime() != null) {
                try {
                    LocalDateTime salida = LocalDateTime.parse(
                        offering.getStart().getDateTime(),
                        DateTimeFormatter.ISO_DATE_TIME
                    );
                    transporte.setSalida(salida);
                } catch (Exception e) {
                    logger.debug("No se pudo parsear fecha de salida: {}", e.getMessage());
                }
            }
            
        } catch (Exception e) {
            logger.error("❌ Error al convertir transfer: {}", e.getMessage(), e);
        }
        
        return transporte;
    }
    
    /**
     * Buscar todos los transportes disponibles
     */
    public List<Transporte> buscarDisponibles() {
        logger.info("📋 Buscando transportes disponibles");
        return transporteRepository.findByEstado(Transporte.Estado.disponible);
    }
    
    /**
     * Buscar por tipo y estado disponible
     */
    public List<Transporte> buscarDisponiblesPorTipo(Transporte.Tipo tipo) {
        logger.info("📋 Buscando transportes tipo {} disponibles", tipo);
        return transporteRepository.findByTipoAndEstado(tipo, Transporte.Estado.disponible);
    }
    
    /**
     * Buscar por origen
     */
    public List<Transporte> buscarPorOrigen(String origen) {
        logger.info("🔍 Buscando transportes desde: {}", origen);
        return transporteRepository.findByOrigenContainingIgnoreCase(origen);
    }
    
    /**
     * Buscar por destino
     */
    public List<Transporte> buscarPorDestino(String destino) {
        logger.info("🔍 Buscando transportes hacia: {}", destino);
        return transporteRepository.findByDestinoContainingIgnoreCase(destino);
    }
    
    /**
     * Buscar por origen y destino
     */
    public List<Transporte> buscarPorOrigenDestino(String origen, String destino) {
        logger.info("🔍 Buscando transportes: {} -> {}", origen, destino);
        return transporteRepository.findByOrigenAndDestino(origen, destino);
    }
    
    /**
     * Buscar por origen, destino y tipo
     */
    public List<Transporte> buscarPorOrigenDestinoTipo(String origen, String destino, 
                                                       Transporte.Tipo tipo) {
        logger.info("🔍 Buscando transportes tipo {}: {} -> {}", tipo, origen, destino);
        return transporteRepository.findByOrigenDestinoAndTipo(origen, destino, tipo);
    }
    
    /**
     * Buscar transfers de aeropuerto en una ciudad
     */
    public List<Transporte> buscarTransferAeropuerto(String ciudad) {
        logger.info("🔍 Buscando transfers de aeropuerto en: {}", ciudad);
        return transporteRepository.findTransfersByCiudad(ciudad);
    }
    
    /**
     * Obtener transporte por ID
     */
    public Optional<Transporte> obtenerPorId(Integer id) {
        logger.info("📄 Obteniendo transporte ID: {}", id);
        return transporteRepository.findById(id);
    }
    
    /**
     * Reservar un transporte
     * 
     * @param id ID del transporte
     * @return Transporte reservado
     */
    @Transactional
    public Transporte reservarTransporte(Integer id) {
        logger.info("🎫 Reservando transporte ID: {}", id);
        
        Transporte transporte = transporteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transporte no encontrado con ID: " + id));
        
        if (transporte.getEstado() != Transporte.Estado.disponible) {
            throw new RuntimeException("Transporte no está disponible. Estado actual: " + transporte.getEstado());
        }
        
        transporte.setEstado(Transporte.Estado.reservado);
        Transporte reservado = transporteRepository.save(transporte);
        
        logger.info("✅ Transporte {} reservado exitosamente", id);
        return reservado;
    }
    
    /**
     * Cancelar reserva de transporte
     * 
     * @param id ID del transporte
     * @return Transporte con reserva cancelada
     */
    @Transactional
    public Transporte cancelarReserva(Integer id) {
        logger.info("❌ Cancelando reserva de transporte ID: {}", id);
        
        Transporte transporte = transporteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transporte no encontrado con ID: " + id));
        
        if (transporte.getEstado() != Transporte.Estado.reservado) {
            throw new RuntimeException("El transporte no está reservado. Estado actual: " + transporte.getEstado());
        }
        
        transporte.setEstado(Transporte.Estado.disponible);
        Transporte cancelado = transporteRepository.save(transporte);
        
        logger.info("✅ Reserva cancelada para transporte {}", id);
        return cancelado;
    }
    
    /**
     * Crear transporte manualmente (sin Amadeus)
     * 
     * @param transporte Datos del transporte
     * @return Transporte guardado
     */
    @Transactional
    public Transporte crearTransporte(Transporte transporte) {
        logger.info("➕ Creando transporte manual: {} -> {}", 
                   transporte.getOrigen(), transporte.getDestino());
        
        if (transporte.getEstado() == null) {
            transporte.setEstado(Transporte.Estado.disponible);
        }
        
        Transporte guardado = transporteRepository.save(transporte);
        logger.info("✅ Transporte creado con ID: {}", guardado.getId());
        
        return guardado;
    }
    
    /**
     * Actualizar transporte existente
     * 
     * @param id ID del transporte
     * @param transporteDetails Datos actualizados
     * @return Transporte actualizado
     */
    @Transactional
    public Transporte actualizarTransporte(Integer id, Transporte transporteDetails) {
        logger.info("✏️ Actualizando transporte ID: {}", id);
        
        Transporte transporte = transporteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transporte no encontrado con ID: " + id));
        
        // Actualizar campos si están presentes
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
        if (transporteDetails.getCurrency() != null) {
            transporte.setCurrency(transporteDetails.getCurrency());
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
        if (transporteDetails.getVehiculoTipo() != null) {
            transporte.setVehiculoTipo(transporteDetails.getVehiculoTipo());
        }
        if (transporteDetails.getSalida() != null) {
            transporte.setSalida(transporteDetails.getSalida());
        }
        if (transporteDetails.getLlegada() != null) {
            transporte.setLlegada(transporteDetails.getLlegada());
        }
        
        Transporte actualizado = transporteRepository.save(transporte);
        logger.info("✅ Transporte {} actualizado", id);
        
        return actualizado;
    }
    
    /**
     * Eliminar transporte
     * 
     * @param id ID del transporte
     */
    @Transactional
    public void eliminarTransporte(Integer id) {
        logger.info("🗑️ Eliminando transporte ID: {}", id);
        
        if (!transporteRepository.existsById(id)) {
            throw new RuntimeException("Transporte no encontrado con ID: " + id);
        }
        
        transporteRepository.deleteById(id);
        logger.info("✅ Transporte {} eliminado", id);
    }
    
    /**
     * Calcular precio estimado basado en distancia y tipo
     * Útil para dar estimaciones antes de buscar en Amadeus
     * 
     * @param origen Código de origen
     * @param destino Código de destino
     * @param tipo Tipo de transporte
     * @return Precio estimado
     */
    public BigDecimal calcularPrecioEstimado(String origen, String destino, Transporte.Tipo tipo) {
        logger.info("💰 Calculando precio estimado para {} -> {} ({})", origen, destino, tipo);
        
        // Buscar transportes similares
        List<Transporte> similares = buscarPorOrigenDestinoTipo(origen, destino, tipo);
        
        if (!similares.isEmpty()) {
            // Calcular promedio de precios existentes
            BigDecimal total = BigDecimal.ZERO;
            int count = 0;
            
            for (Transporte t : similares) {
                if (t.getPrecio() != null && t.getPrecio().compareTo(BigDecimal.ZERO) > 0) {
                    total = total.add(t.getPrecio());
                    count++;
                }
            }
            
            if (count > 0) {
                BigDecimal promedio = total.divide(new BigDecimal(count), 2, BigDecimal.ROUND_HALF_UP);
                logger.info("✅ Precio estimado (promedio de {} transfers): {}", count, promedio);
                return promedio;
            }
        }
        
        // Si no hay datos, usar precios base por tipo
        BigDecimal precioBase;
        switch (tipo) {
            case Transfer:
                precioBase = new BigDecimal("50.00");
                break;
            case Taxi:
                precioBase = new BigDecimal("30.00");
                break;
            case Bus:
                precioBase = new BigDecimal("15.00");
                break;
            case Tren:
                precioBase = new BigDecimal("25.00");
                break;
            case Auto_Rental:
                precioBase = new BigDecimal("45.00");
                break;
            case Barco:
                precioBase = new BigDecimal("35.00");
                break;
            case Avion:
                precioBase = new BigDecimal("100.00");
                break;
            default:
                precioBase = new BigDecimal("20.00");
        }
        
        logger.info("✅ Precio estimado (base): {}", precioBase);
        return precioBase;
    }
    
    /**
     * Obtener estadísticas de transfers
     * Útil para dashboard admin
     */
    public Map<String, Object> obtenerEstadisticas() {
        logger.info("📊 Obteniendo estadísticas de transfers");
        
        Map<String, Object> stats = new HashMap<>();
        
        // Total de transfers
        long total = transporteRepository.count();
        stats.put("total", total);
        
        // Por estado
        long disponibles = transporteRepository.countByEstado(Transporte.Estado.disponible);
        long reservados = transporteRepository.countByEstado(Transporte.Estado.reservado);
        long cancelados = transporteRepository.countByEstado(Transporte.Estado.cancelado);
        
        stats.put("disponibles", disponibles);
        stats.put("reservados", reservados);
        stats.put("cancelados", cancelados);
        
        // Por tipo
        long transfers = transporteRepository.countByTipo(Transporte.Tipo.Transfer);
        stats.put("transfers", transfers);
        
        logger.info("✅ Estadísticas: Total={}, Disponibles={}, Reservados={}", 
                   total, disponibles, reservados);
        
        return stats;
    }
}