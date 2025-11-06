package com.travelgo.backend_travelgo.repository;

import com.travelgo.backend_travelgo.model.Reserva;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    // ✅ CORREGIDO: Usa el nombre del campo correcto
    // El campo en el modelo se llama "usuario_id", no "usuario"
    List<Reserva> findByUsuarioId(Integer usuarioId);
    
    // Buscar por viaje
    List<Reserva> findByViajeId(Integer viajeId);
    
    // Buscar por estado
    List<Reserva> findByEstado(Reserva.Estado estado);
    
    // Buscar por usuario y estado
    List<Reserva> findByUsuarioIdAndEstado(Integer usuarioId, Reserva.Estado estado);
    
    // Buscar por viaje y estado
    List<Reserva> findByViajeIdAndEstado(Integer viajeId, Reserva.Estado estado);
    
    // Contar reservas por usuario
    Long countByUsuarioId(Integer usuarioId);
    
    // Contar reservas por estado
    Long countByEstado(Reserva.Estado estado);
    
    List<Reserva> findByUsuarioId(Long usuarioId);
}