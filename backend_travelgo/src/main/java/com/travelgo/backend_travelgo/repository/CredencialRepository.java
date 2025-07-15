/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.travelgo.backend_travelgo.repository;

import com.travelgo.backend_travelgo.model.Credencial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredencialRepository extends JpaRepository<Credencial, Integer> {

    boolean existsByCorreo(String correo);
    Optional<Credencial> findByCorreoAndContrasena(String correo, String contrasena);
}