package com.travelgo.backend_travelgo.service;

import com.travelgo.backend_travelgo.model.Usuario;
import com.travelgo.backend_travelgo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }
    
    public Optional<Usuario> findById(Integer id) {
        return usuarioRepository.findById(id);
    }
    
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
    
    public void deleteById(Integer id) {
        usuarioRepository.deleteById(id);
    }
    
    public Optional<Usuario> findByCredencialId(Integer credencialId) {
        return usuarioRepository.findByCredencialId(credencialId);
    }
}