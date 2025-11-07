package com.travelgo.backend_travelgo.service;

import com.travelgo.backend_travelgo.model.Reserva;
import com.travelgo.backend_travelgo.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {
    
    @Autowired
    private ReservaRepository reservaRepository;
    
    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }
    
    public Optional<Reserva> findById(Integer id) {
        return reservaRepository.findById(id);
    }
    
    public List<Reserva> findByUsuarioId(Integer usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }
    
    // ✅ NUEVO: Método que acepta Long y lo convierte a Integer
    public List<Reserva> findByUsuarioId(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId.intValue());
    }
    
    public List<Reserva> findByViajeId(Integer viajeId) {
        return reservaRepository.findByViajeId(viajeId);
    }
    
    public List<Reserva> findByEstado(Reserva.Estado estado) {
        return reservaRepository.findByEstado(estado);
    }
    
    public Reserva save(Reserva reserva) {
        return reservaRepository.save(reserva);
    }
    
    public void deleteById(Integer id) {
        reservaRepository.deleteById(id);
    }
    
    public boolean existsById(Integer id) {
        return reservaRepository.existsById(id);
    }
    
    public long count() {
        return reservaRepository.count();
    }
    
    public Long countByUsuarioId(Integer usuarioId) {
        return reservaRepository.countByUsuarioId(usuarioId);
    }
    
    public Long countByEstado(Reserva.Estado estado) {
        return reservaRepository.countByEstado(estado);
    }
}