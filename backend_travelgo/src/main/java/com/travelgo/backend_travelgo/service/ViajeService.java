package com.travelgo.backend_travelgo.service;

import com.travelgo.backend_travelgo.model.Viaje;
import com.travelgo.backend_travelgo.repository.ViajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de viajes
 */
@Service
public class ViajeService {
    
    private static final Logger logger = LoggerFactory.getLogger(ViajeService.class);
    
    @Autowired
    private ViajeRepository viajeRepository;
    
    /**
     * Obtener todos los viajes
     * @return Lista de viajes
     */
    public List<Viaje> obtenerTodos() {
        logger.info("📋 Obteniendo todos los viajes");
        return viajeRepository.findAll();
    }
    
    /**
     * Obtener viaje por ID
     * @param id ID del viaje
     * @return Viaje encontrado o null
     */
    public Viaje obtenerPorId(Long id) {
        logger.info("📄 Obteniendo viaje por ID: {}", id);
        Optional<Viaje> viaje = viajeRepository.findById(id.intValue());
        return viaje.orElse(null);
    }
    
    /**
     * Guardar un nuevo viaje
     * @param viaje Viaje a guardar
     * @return Viaje guardado
     */
    public Viaje guardarViaje(Viaje viaje) {
        logger.info("💾 Guardando viaje: {} -> {}", viaje.getOrigin(), viaje.getDestinationCode());
        
        try {
            Viaje guardado = viajeRepository.save(viaje);
            logger.info("✅ Viaje guardado con ID: {}", guardado.getId());
            return guardado;
            
        } catch (Exception e) {
            logger.error("❌ Error al guardar viaje: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar viaje: " + e.getMessage(), e);
        }
    }
    
    /**
     * Actualizar viaje existente
     * @param viaje Viaje con datos actualizados
     * @return Viaje actualizado
     */
    public Viaje actualizarViaje(Viaje viaje) {
        logger.info("✏️ Actualizando viaje ID: {}", viaje.getId());
        
        if (viaje.getId() == null) {
            throw new IllegalArgumentException("El ID del viaje es requerido para actualizar");
        }
        
        Optional<Viaje> existente = viajeRepository.findById(viaje.getId());
        
        if (existente.isEmpty()) {
            throw new RuntimeException("Viaje no encontrado con ID: " + viaje.getId());
        }
        
        try {
            Viaje actualizado = viajeRepository.save(viaje);
            logger.info("✅ Viaje actualizado: {}", actualizado.getId());
            return actualizado;
            
        } catch (Exception e) {
            logger.error("❌ Error al actualizar viaje: {}", e.getMessage(), e);
            throw new RuntimeException("Error al actualizar viaje: " + e.getMessage(), e);
        }
    }
    
    /**
     * Eliminar viaje por ID
     * @param id ID del viaje a eliminar
     */
    public void eliminarViaje(Long id) {
        logger.info("🗑️ Eliminando viaje ID: {}", id);
        
        if (!viajeRepository.existsById(id.intValue())) {
            throw new RuntimeException("Viaje no encontrado con ID: " + id);
        }
        
        try {
            viajeRepository.deleteById(id.intValue());
            logger.info("✅ Viaje eliminado: {}", id);
            
        } catch (Exception e) {
            logger.error("❌ Error al eliminar viaje: {}", e.getMessage(), e);
            throw new RuntimeException("Error al eliminar viaje: " + e.getMessage(), e);
        }
    }
    
    /**
     * Buscar viajes por origen
     * @param origin Código de origen
     * @return Lista de viajes
     */
    public List<Viaje> buscarPorOrigen(String origin) {
        logger.info("🔍 Buscando viajes desde: {}", origin);
        return viajeRepository.findByOrigin(origin);
    }
    
    /**
     * Buscar viajes por destino
     * @param destino Código de destino
     * @return Lista de viajes
     */
    public List<Viaje> buscarPorDestino(String destino) {
        logger.info("🔍 Buscando viajes hacia: {}", destino);
        return viajeRepository.findByDestino(destino);
    }
    
    /**
     * Buscar viajes por tipo
     * @param tipoViaje Tipo de viaje
     * @return Lista de viajes
     */
    public List<Viaje> buscarPorTipo(String tipoViaje) {
        logger.info("🔍 Buscando viajes tipo: {}", tipoViaje);
        return viajeRepository.findByTipoViaje(tipoViaje);
    }
    
    /**
     * Buscar viaje por flight offer ID
     * @param flightOfferId ID del flight offer
     * @return Viaje encontrado
     */
    public Optional<Viaje> buscarPorFlightOfferId(String flightOfferId) {
        logger.info("🔍 Buscando viaje con flight offer ID: {}", flightOfferId);
        return viajeRepository.findByFlightOfferId(flightOfferId);
    }
    
    /**
     * Contar total de viajes
     * @return Cantidad de viajes
     */
    public long contarViajes() {
        long count = viajeRepository.count();
        logger.info("📊 Total de viajes: {}", count);
        return count;
    }
}