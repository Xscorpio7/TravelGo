package com.travelgo.backend_travelgo.service;

import com.travelgo.backend_travelgo.model.Reserva;
import com.travelgo.backend_travelgo.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReservaService {
    
    @Autowired
    private ReservaRepository reservaRepository;
    
    /**
     * Obtener todas las reservas (para admin)
     */
    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }
    
    /**
     * Buscar reservas por ID de usuario
     */
    public List<Reserva> findByUsuarioId(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }
}