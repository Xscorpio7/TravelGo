/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.travelgo.backend_travelgo.repository;
import com.travelgo.backend_travelgo.model.Transporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Alejo
 */
public interface TransporteRepository extends JpaRepository<Transporte, Integer> {
    
}
