package com.travelgo.backend_travelgo.repository;

import com.travelgo.backend_travelgo.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    
    /**
     * Buscar pagos por ID de reserva
     * @param reservaId ID de la reserva
     * @return Lista de pagos asociados
     */
    List<Pago> findByReservaId(Integer reservaId);
}