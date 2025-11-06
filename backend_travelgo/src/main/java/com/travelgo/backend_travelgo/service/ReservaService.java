package com.travelgo.backend_travelgo.service;

import com.travelgo.backend_travelgo.model.Reserva;
import com.travelgo.backend_travelgo.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service  // ← Importante: marca la clase como servicio de Spring
public class ReservaService {
    
    @Autowired  // ← Inyecta el repositorio
    private ReservaRepository reservaRepository;
    
    public List<Reserva> findByUsuarioId(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);  // ← Usa la instancia
    }
}