package com.travelgo.backend_travelgo.controller;

import com.travelgo.backend_travelgo.model.Alojamiento;
import com.travelgo.backend_travelgo.repository.AlojamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/alojamientos")
@CrossOrigin(origins = "*") // Permite llamadas desde cualquier origen (útil para frontend en desarrollo)
public class AlojamientoController {

    @Autowired
    private AlojamientoRepository alojamientoRepository;

    // Obtener todos los alojamientos
    @GetMapping
    public List<Alojamiento> getAllAlojamientos() {
        return alojamientoRepository.findAll();
    }

    // Obtener alojamiento por ID
    @GetMapping("/{id}")
    public ResponseEntity<Alojamiento> getAlojamientoById(@PathVariable int id) {
        Optional<Alojamiento> alojamiento = alojamientoRepository.findById(id);
        return alojamiento.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }

    // Crear un nuevo alojamiento
    @PostMapping
    public Alojamiento createAlojamiento(@RequestBody Alojamiento alojamiento) {
        return alojamientoRepository.save(alojamiento);
    }

    // Actualizar alojamiento
    @PutMapping("/{id}")
    public ResponseEntity<Alojamiento> updateAlojamiento(@PathVariable int id, @RequestBody Alojamiento alojamientoDetails) {
        Optional<Alojamiento> optionalAlojamiento = alojamientoRepository.findById(id);

        if (optionalAlojamiento.isPresent()) {
            Alojamiento alojamiento = optionalAlojamiento.get();
            alojamiento.setNombre(alojamientoDetails.getNombre());
            alojamiento.setDireccion(alojamientoDetails.getDireccion());
            alojamiento.setCiudad(alojamientoDetails.getCiudad());
            alojamiento.setTipo(alojamientoDetails.getTipo());
            alojamiento.setCapacidad(alojamientoDetails.getCapacidad());
            alojamiento.setPrecio(alojamientoDetails.getPrecio());
            alojamiento.setViaje(alojamientoDetails.getViaje());

            Alojamiento updated = alojamientoRepository.save(alojamiento);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar alojamiento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlojamiento(@PathVariable int id) {
        if (alojamientoRepository.existsById(id)) {
            alojamientoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
