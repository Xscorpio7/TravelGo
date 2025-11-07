package com.travelgo.backend_travelgo.service;

import com.travelgo.backend_travelgo.model.Credencial;
import com.travelgo.backend_travelgo.repository.CredencialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CredencialService {
    
    @Autowired
    private CredencialRepository credencialRepository;
    
    public List<Credencial> findAll() {
        return credencialRepository.findAll();
    }
    
    public Optional<Credencial> findById(Integer id) {
        return credencialRepository.findById(id);
    }
    
    public Optional<Credencial> findByCorreo(String correo) {
        return credencialRepository.findByCorreo(correo);
    }
    
    public boolean existsByCorreo(String correo) {
        return credencialRepository.existsByCorreo(correo);
    }
    
    public Credencial save(Credencial credencial) {
        return credencialRepository.save(credencial);
    }
    
    public void deleteById(Integer id) {
        credencialRepository.deleteById(id);
    }
}