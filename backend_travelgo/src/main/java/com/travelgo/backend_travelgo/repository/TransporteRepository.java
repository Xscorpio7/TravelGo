package com.travelgo.backend_travelgo.repository;

import com.travelgo.backend_travelgo.model.Transporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransporteRepository extends JpaRepository<Transporte, Integer> {
    
    // Buscar por tipo
    List<Transporte> findByTipo(Transporte.Tipo tipo);
    
    // Buscar por estado
    List<Transporte> findByEstado(Transporte.Estado estado);
    
    // Buscar por tipo y estado
    List<Transporte> findByTipoAndEstado(Transporte.Tipo tipo, Transporte.Estado estado);
    
    // Buscar por origen
    List<Transporte> findByOrigenContainingIgnoreCase(String origen);
    
    // Buscar por destino
    List<Transporte> findByDestinoContainingIgnoreCase(String destino);
    
    // Buscar por origen y destino
    @Query("SELECT t FROM Transporte t WHERE " +
           "LOWER(t.origen) LIKE LOWER(CONCAT('%', :origen, '%')) AND " +
           "LOWER(t.destino) LIKE LOWER(CONCAT('%', :destino, '%'))")
    List<Transporte> findByOrigenAndDestino(@Param("origen") String origen, 
                                            @Param("destino") String destino);
    
    // Buscar por origen, destino y tipo
    @Query("SELECT t FROM Transporte t WHERE " +
           "LOWER(t.origen) LIKE LOWER(CONCAT('%', :origen, '%')) AND " +
           "LOWER(t.destino) LIKE LOWER(CONCAT('%', :destino, '%')) AND " +
           "t.tipo = :tipo")
    List<Transporte> findByOrigenDestinoAndTipo(@Param("origen") String origen, 
                                                @Param("destino") String destino,
                                                @Param("tipo") Transporte.Tipo tipo);
    
    // Buscar disponibles por tipo
    List<Transporte> findByTipoAndEstado(Transporte.Tipo tipo, Transporte.Estado disponible);
    
    // Buscar por transfer ID de Amadeus
    Optional<Transporte> findByTransferId(String transferId);
    
    // Buscar transfers de aeropuerto en una ciudad
    @Query("SELECT t FROM Transporte t WHERE " +
           "t.tipo = 'Transfer' AND " +
           "t.estado = 'disponible' AND " +
           "(LOWER(t.origen) LIKE LOWER(CONCAT('%', :ciudad, '%')) OR " +
           "LOWER(t.destino) LIKE LOWER(CONCAT('%', :ciudad, '%')))")
    List<Transporte> findTransfersByCiudad(@Param("ciudad") String ciudad);
    
    // Buscar por rango de fechas
    @Query("SELECT t FROM Transporte t WHERE " +
           "t.salida >= :inicio AND t.salida <= :fin")
    List<Transporte> findByFechaRange(@Param("inicio") LocalDateTime inicio, 
                                      @Param("fin") LocalDateTime fin);
    
    // Buscar por proveedor
    List<Transporte> findByProveedorContainingIgnoreCase(String proveedor);
    
    // Contar por tipo
    Long countByTipo(Transporte.Tipo tipo);
    
    // Contar por estado
    Long countByEstado(Transporte.Estado estado);
}