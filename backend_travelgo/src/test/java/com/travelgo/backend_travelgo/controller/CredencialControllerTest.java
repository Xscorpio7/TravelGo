package com.travelgo.backend_travelgo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelgo.backend_travelgo.model.Credencial;
import com.travelgo.backend_travelgo.repository.CredencialRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CredencialController.class)
public class CredencialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CredencialRepository credencialRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testLoginCredencialesInvalidasRetorna401() throws Exception {
        Credencial credencialInvalida = new Credencial();
        credencialInvalida.setCorreo("usuario@ejemplo.com");
        credencialInvalida.setContrasena("contrasenaIncorrecta");

        when(credencialRepository.findByCorreoAndContrasena(
                credencialInvalida.getCorreo(),
                credencialInvalida.getContrasena()
        )).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/credenciales/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credencialInvalida)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogin_CredencialesValidas_Retorna200() throws Exception {
        Credencial credencialValida = new Credencial();
        credencialValida.setCorreo("usuario@ejemplo.com");
        credencialValida.setContrasena("contrasenaCorrecta");

        when(credencialRepository.findByCorreoAndContrasena(
                credencialValida.getCorreo(),
                credencialValida.getContrasena()
        )).thenReturn(Optional.of(credencialValida));

        mockMvc.perform(post("/api/credenciales/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credencialValida)))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateCredencial_CorreoExistente_Retorna400() throws Exception {
        Credencial credencial = new Credencial();
        credencial.setCorreo("existente@correo.com");
        credencial.setContrasena("1234");

        when(credencialRepository.existsByCorreo(credencial.getCorreo())).thenReturn(true);

        mockMvc.perform(post("/api/credenciales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credencial)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateCredencial_CorreoNuevo_Retorna200() throws Exception {
        Credencial credencial = new Credencial();
        credencial.setCorreo("nuevo@correo.com");
        credencial.setContrasena("1234");

        when(credencialRepository.existsByCorreo(credencial.getCorreo())).thenReturn(false);
        when(credencialRepository.save(credencial)).thenReturn(credencial);

        mockMvc.perform(post("/api/credenciales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credencial)))
                .andExpect(status().isOk());
    }
}
