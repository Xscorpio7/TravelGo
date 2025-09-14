package com.travelgo.backend_travelgo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelgo.backend_travelgo.model.Usuario;
import com.travelgo.backend_travelgo.model.Credencial;
import com.travelgo.backend_travelgo.repository.UsuarioRepository;
import com.travelgo.backend_travelgo.repository.CredencialRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class) 
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;
    
    @MockBean
    private CredencialRepository credencialRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testObtenerPerfilUsuarioExistente_Retorna200() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setPrimerNombre("Juan ");
        usuario.setPrimerApellido("Pérez");
        usuario.setTelefono("3001234567");
        usuario.setNacionalidad(Usuario.Nacionalidad.Colombia);
        usuario.setFechaNacimiento(LocalDate.of(1995, 5, 20));
        usuario.setGenero(Usuario.Genero.MALE);
        usuario.setCredencial(new Credencial()); 

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombreCompleto").value("Juan Pérez"))
                .andExpect(jsonPath("$.telefono").value("3001234567"))
                .andExpect(jsonPath("$.nacionalidad").value("Colombiana"))
                .andExpect(jsonPath("$.genero").value("M"));
    }

    @Test
    public void testObtenerPerfilUsuarioNoExistente_Retorna404() throws Exception {
        when(usuarioRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isNotFound());
    }
}
