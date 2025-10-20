package com.travelgo.backend_travelgo.repository;

import com.travelgo.backend_travelgo.model.Reserva;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    public List<Reserva> findByUsuario_id(Integer usuarioId);
    
}
