package com.travelgo.backend_travelgo.controller;

import com.travelgo.backend_travelgo.model.Viaje;
import com.travelgo.backend_travelgo.repository.ViajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/viajes")
@CrossOrigin(origins = "*") // Permitir peticiones desde cualquier origen (útil para el frontend)
public class ViajeController {

    @Autowired
    private ViajeRepository viajeRepository;

    // Obtener todos los viajes
    @GetMapping
    public List<Viaje> getAllViajes() {
        return viajeRepository.findAll();
    }

    // Obtener un viaje por ID
    @GetMapping("/{id}")
    public ResponseEntity<Viaje> getViajeById(@PathVariable int id) {
        Optional<Viaje> viaje = viajeRepository.findById(id);
        return viaje.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    // Crear un nuevo viaje
    @PostMapping
    public Viaje createViaje(@RequestBody Viaje viaje) {
        return viajeRepository.save(viaje);
    }

    // Actualizar un viaje existente
    @PutMapping("/{id}")
    public ResponseEntity<Viaje> updateViaje(@PathVariable int id, @RequestBody Viaje viajeDetails) {
        Optional<Viaje> optionalViaje = viajeRepository.findById(id);
        if (optionalViaje.isPresent()) {
            Viaje viaje = optionalViaje.get();
            viaje.setTitulo(viajeDetails.getTitulo());
            viaje.setDescripcion(viajeDetails.getDescripcion());
            viaje.setDestino(viajeDetails.getDestino());
            viaje.setTelefono(viajeDetails.getTelefono());
            viaje.setFechaInicio(viajeDetails.getFechaInicio());
            viaje.setFechaFin(viajeDetails.getFechaFin());
            viaje.setPrecio(viajeDetails.getPrecio());
            viaje.setCuposDisponibles(viajeDetails.getCuposDisponibles());
            return ResponseEntity.ok(viajeRepository.save(viaje));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar un viaje por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteViaje(@PathVariable int id) {
        if (viajeRepository.existsById(id)) {
            viajeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
