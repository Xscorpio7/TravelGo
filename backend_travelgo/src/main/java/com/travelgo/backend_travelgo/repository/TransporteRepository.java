package com.travelgo.backend_travelgo.repository;

import com.travelgo.backend_travelgo.model.Transporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Transporte
 * Proporciona métodos de consulta personalizados para transfers
 */
@Repository
public interface TransporteRepository extends JpaRepository<Transporte, Integer> {
    
    // ========================================
    // Búsquedas por tipo
    // ========================================
    
    /**
     * Buscar todos los transportes de un tipo específico
     * @param tipo Tipo de transporte
     * @return Lista de transportes
     */
    List<Transporte> findByTipo(Transporte.Tipo tipo);
    
    /**
     * Contar transportes por tipo
     * @param tipo Tipo de transporte
     * @return Cantidad de transportes
     */
    Long countByTipo(Transporte.Tipo tipo);
    
    // ========================================
    // Búsquedas por estado
    // ========================================
    
    /**
     * Buscar transportes por estado
     * @param estado Estado del transporte
     * @return Lista de transportes
     */
    List<Transporte> findByEstado(Transporte.Estado estado);
    
    /**
     * Contar transportes por estado
     * @param estado Estado del transporte
     * @return Cantidad de transportes
     */
    Long countByEstado(Transporte.Estado estado);
    
    /**
     * Buscar por tipo y estado
     * @param tipo Tipo de transporte
     * @param estado Estado del transporte
     * @return Lista de transportes
     */
    List<Transporte> findByTipoAndEstado(Transporte.Tipo tipo, Transporte.Estado estado);
    
    // ========================================
    // Búsquedas por ubicación
    // ========================================
    
    /**
     * Buscar por origen (case insensitive, permite parcial)
     * @param origen Origen del transporte
     * @return Lista de transportes
     */
    List<Transporte> findByOrigenContainingIgnoreCase(String origen);
    
    /**
     * Buscar por destino (case insensitive, permite parcial)
     * @param destino Destino del transporte
     * @return Lista de transportes
     */
    List<Transporte> findByDestinoContainingIgnoreCase(String destino);
    
    /**
     * Buscar por origen y destino exactos
     * @param origen Origen
     * @param destino Destino
     * @return Lista de transportes
     */
    @Query("SELECT t FROM Transporte t WHERE " +
           "LOWER(t.origen) LIKE LOWER(CONCAT('%', :origen, '%')) AND " +
           "LOWER(t.destino) LIKE LOWER(CONCAT('%', :destino, '%'))")
    List<Transporte> findByOrigenAndDestino(@Param("origen") String origen, 
                                            @Param("destino") String destino);
    
    /**
     * Buscar por origen, destino y tipo
     * @param origen Origen
     * @param destino Destino
     * @param tipo Tipo de transporte
     * @return Lista de transportes
     */
    @Query("SELECT t FROM Transporte t WHERE " +
           "LOWER(t.origen) LIKE LOWER(CONCAT('%', :origen, '%')) AND " +
           "LOWER(t.destino) LIKE LOWER(CONCAT('%', :destino, '%')) AND " +
           "t.tipo = :tipo")
    List<Transporte> findByOrigenDestinoAndTipo(@Param("origen") String origen, 
                                                @Param("destino") String destino,
                                                @Param("tipo") Transporte.Tipo tipo);
    
    // ========================================
    // Búsquedas específicas de Transfers
    // ========================================
    
    /**
     * Buscar transfer por ID de Amadeus
     * @param transferId ID único del transfer en Amadeus
     * @return Optional de Transporte
     */
    Optional<Transporte> findByTransferId(String transferId);
    
    /**
     * Buscar transfers en una ciudad específica
     * Busca en origen o destino
     * @param ciudad Nombre de la ciudad
     * @return Lista de transfers
     */
    @Query("SELECT t FROM Transporte t WHERE " +
           "t.tipo = 'Transfer' AND " +
           "t.estado = 'disponible' AND " +
           "(LOWER(t.origen) LIKE LOWER(CONCAT('%', :ciudad, '%')) OR " +
           "LOWER(t.destino) LIKE LOWER(CONCAT('%', :ciudad, '%')))")
    List<Transporte> findTransfersByCiudad(@Param("ciudad") String ciudad);
    
    /**
     * Buscar transfers desde un aeropuerto específico
     * @param airportCode Código IATA del aeropuerto
     * @return Lista de transfers
     */
    @Query("SELECT t FROM Transporte t WHERE " +
           "t.tipo = 'Transfer' AND " +
           "t.estado = 'disponible' AND " +
           "UPPER(t.origen) = UPPER(:airportCode)")
    List<Transporte> findTransfersByAirport(@Param("airportCode") String airportCode);
    
    /**
     * Buscar transfers con capacidad mínima
     * @param minCapacity Capacidad mínima requerida
     * @return Lista de transfers
     */
    @Query("SELECT t FROM Transporte t WHERE " +
           "t.tipo = 'Transfer' AND " +
           "t.estado = 'disponible' AND " +
           "t.capacidad >= :minCapacity")
    List<Transporte> findTransfersWithMinCapacity(@Param("minCapacity") Integer minCapacity);
    
    // ========================================
    // Búsquedas por rango de fechas
    // ========================================
    
    /**
     * Buscar transportes en un rango de fechas
     * @param inicio Fecha/hora inicio
     * @param fin Fecha/hora fin
     * @return Lista de transportes
     */
    @Query("SELECT t FROM Transporte t WHERE " +
           "t.salida >= :inicio AND t.salida <= :fin")
    List<Transporte> findByFechaRange(@Param("inicio") LocalDateTime inicio, 
                                      @Param("fin") LocalDateTime fin);
    
    /**
     * Buscar transportes con salida después de una fecha
     * @param fecha Fecha de referencia
     * @return Lista de transportes
     */
    @Query("SELECT t FROM Transporte t WHERE " +
           "t.salida >= :fecha AND t.estado = 'disponible' " +
           "ORDER BY t.salida ASC")
    List<Transporte> findUpcomingTransports(@Param("fecha") LocalDateTime fecha);
    
    // ========================================
    // Búsquedas por proveedor
    // ========================================
    
    /**
     * Buscar por proveedor
     * @param proveedor Nombre del proveedor
     * @return Lista de transportes
     */
    List<Transporte> findByProveedorContainingIgnoreCase(String proveedor);
    
    /**
     * Buscar por proveedor y tipo
     * @param proveedor Nombre del proveedor
     * @param tipo Tipo de transporte
     * @return Lista de transportes
     */
    @Query("SELECT t FROM Transporte t WHERE " +
           "LOWER(t.proveedor) LIKE LOWER(CONCAT('%', :proveedor, '%')) AND " +
           "t.tipo = :tipo")
    List<Transporte> findByProveedorAndTipo(@Param("proveedor") String proveedor,
                                           @Param("tipo") Transporte.Tipo tipo);
    
    // ========================================
    // Búsquedas por precio
    // ========================================
    
    /**
     * Buscar transfers por rango de precio
     * @param minPrecio Precio mínimo
     * @param maxPrecio Precio máximo
     * @return Lista de transfers
     */
    @Query("SELECT t FROM Transporte t WHERE " +
           "t.tipo = 'Transfer' AND " +
           "t.estado = 'disponible' AND " +
           "t.precio >= :minPrecio AND t.precio <= :maxPrecio " +
           "ORDER BY t.precio ASC")
    List<Transporte> findTransfersByPriceRange(@Param("minPrecio") java.math.BigDecimal minPrecio,
                                               @Param("maxPrecio") java.math.BigDecimal maxPrecio);
    
    /**
     * Buscar transfers más baratos
     * @param limite Número máximo de resultados
     * @return Lista de transfers ordenados por precio ascendente
     */
    @Query("SELECT t FROM Transporte t WHERE " +
           "t.tipo = 'Transfer' AND " +
           "t.estado = 'disponible' " +
           "ORDER BY t.precio ASC")
    List<Transporte> findCheapestTransfers();
    
    // ========================================
    // Búsquedas por tipo de vehículo
    // ========================================
    
    /**
     * Buscar por tipo de vehículo
     * @param vehiculoTipo Tipo de vehículo (SEDAN, VAN, etc.)
     * @return Lista de transfers
     */
    @Query("SELECT t FROM Transporte t WHERE " +
           "t.tipo = 'Transfer' AND " +
           "t.estado = 'disponible' AND " +
           "UPPER(t.vehiculoTipo) = UPPER(:vehiculoTipo)")
    List<Transporte> findByVehicleType(@Param("vehiculoTipo") String vehiculoTipo);
    
    // ========================================
    // Búsquedas por booking reference
    // ========================================
    
    /**
     * Buscar por referencia de reserva
     * @param bookingReference Referencia de reserva
     * @return Optional de Transporte
     */
    Optional<Transporte> findByBookingReference(String bookingReference);
    
    // ========================================
    // Búsquedas por Amadeus ID
    // ========================================
    
    /**
     * Buscar por Amadeus ID
     * @param amadeusId ID de Amadeus
     * @return Optional de Transporte
     */
    Optional<Transporte> findByAmadeusId(String amadeusId);
    
    /**
     * Verificar si existe un transfer con un ID de Amadeus
     * @param transferId ID del transfer
     * @return true si existe, false si no
     */
    boolean existsByTransferId(String transferId);
    
    // ========================================
    // Estadísticas y agregaciones
    // ========================================
    
    /**
     * Contar transfers disponibles
     * @return Cantidad de transfers disponibles
     */
    @Query("SELECT COUNT(t) FROM Transporte t WHERE " +
           "t.tipo = 'Transfer' AND t.estado = 'disponible'")
    Long countAvailableTransfers();
    
    /**
     * Contar transfers reservados hoy
     * @param inicio Inicio del día
     * @param fin Fin del día
     * @return Cantidad de transfers reservados
     */
    @Query("SELECT COUNT(t) FROM Transporte t WHERE " +
           "t.tipo = 'Transfer' AND " +
           "t.estado = 'reservado' AND " +
           "t.createdAt >= :inicio AND t.createdAt <= :fin")
    Long countTransfersReservedToday(@Param("inicio") LocalDateTime inicio,
                                     @Param("fin") LocalDateTime fin);
    
    /**
     * Obtener precio promedio de transfers
     * @return Precio promedio
     */
    @Query("SELECT AVG(t.precio) FROM Transporte t WHERE " +
           "t.tipo = 'Transfer' AND t.precio IS NOT NULL")
    java.math.BigDecimal getAveragePriceTransfers();
    
    /**
     * Obtener transfers más populares (más reservados)
     * @return Lista de rutas populares
     */
    @Query("SELECT t.origen, t.destino, COUNT(t) as total FROM Transporte t WHERE " +
           "t.tipo = 'Transfer' AND t.estado = 'reservado' " +
           "GROUP BY t.origen, t.destino " +
           "ORDER BY total DESC")
    List<Object[]> getMostPopularRoutes();
    
    // ========================================
    // Búsquedas relacionadas con viajes
    // ========================================
    
    /**
     * Buscar transportes asociados a un viaje
     * @param viajeId ID del viaje
     * @return Lista de transportes
     */
    @Query("SELECT t FROM Transporte t WHERE t.viaje.id = :viajeId")
    List<Transporte> findByViajeId(@Param("viajeId") Integer viajeId);
    
    /**
     * Buscar transfers sin viaje asociado
     * @return Lista de transfers
     */
    @Query("SELECT t FROM Transporte t WHERE " +
           "t.tipo = 'Transfer' AND t.viaje IS NULL")
    List<Transporte> findTransfersWithoutViaje();
    
    // ========================================
    // Búsquedas complejas
    // ========================================
    
    /**
     * Buscar transfers con filtros múltiples
     * @param origen Origen (opcional)
     * @param destino Destino (opcional)
     * @param minCapacity Capacidad mínima (opcional)
     * @param maxPrice Precio máximo (opcional)
     * @return Lista de transfers
     */
    @Query("SELECT t FROM Transporte t WHERE " +
           "t.tipo = 'Transfer' AND " +
           "t.estado = 'disponible' AND " +
           "(:origen IS NULL OR LOWER(t.origen) LIKE LOWER(CONCAT('%', :origen, '%'))) AND " +
           "(:destino IS NULL OR LOWER(t.destino) LIKE LOWER(CONCAT('%', :destino, '%'))) AND " +
           "(:minCapacity IS NULL OR t.capacidad >= :minCapacity) AND " +
           "(:maxPrice IS NULL OR t.precio <= :maxPrice) " +
           "ORDER BY t.precio ASC")
    List<Transporte> findTransfersWithFilters(@Param("origen") String origen,
                                              @Param("destino") String destino,
                                              @Param("minCapacity") Integer minCapacity,
                                              @Param("maxPrice") java.math.BigDecimal maxPrice);
    
    /**
     * Buscar transfers similares (mismo origen y destino)
     * @param transporteId ID del transporte de referencia
     * @return Lista de transfers similares
     */
    @Query("SELECT t2 FROM Transporte t1, Transporte t2 WHERE " +
           "t1.id = :transporteId AND " +
           "t2.id != :transporteId AND " +
           "t2.tipo = 'Transfer' AND " +
           "t2.estado = 'disponible' AND " +
           "t2.origen = t1.origen AND " +
           "t2.destino = t1.destino " +
           "ORDER BY t2.precio ASC")
    List<Transporte> findSimilarTransfers(@Param("transporteId") Integer transporteId);
}