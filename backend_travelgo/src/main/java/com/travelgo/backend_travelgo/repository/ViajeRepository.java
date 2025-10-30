/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.travelgo.backend_travelgo.repository;

import com.travelgo.backend_travelgo.model.Viaje;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, Integer> {
    
   
    List<Viaje> findByOriginAndDestino(String origin, String destino);
    
    
    List<Viaje> findByOrigin(String origin);
    
   
    List<Viaje> findByDestino(String destino);
    
    
    List<Viaje> findByDestinationCode(String destinationCode);
    
    
    Optional<Viaje> findByFlightOfferId(String flightOfferId);
    
    
    List<Viaje> findByTipoViaje(String tipoViaje);
    
  
    List<Viaje> findByAirline(String airline);
    
   
    List<Viaje> findByJourneyType(String journeyType);
    
  
    List<Viaje> findByBookableSeatsGreaterThan(Integer seats);
}