package com.travelgo.backend_travelgo.controller;

import com.travelgo.backend_travelgo.model.Transporte;
import com.travelgo.backend_travelgo.repository.TransporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transporte")
@CrossOrigin("*")
public class TransporteController {

    @Autowired
    private TransporteRepository transporteRepository;

    @GetMapping
    public List<Transporte> getAllTransporte() {
        return transporteRepository.findAll();
    }

    @PostMapping
    public Transporte createTransporte(@RequestBody Transporte transporte) {
        return transporteRepository.save(transporte);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transporte> updateTransporte(@PathVariable int id, @RequestBody Transporte transporteDetails) {
        return transporteRepository.findById(id)
                .map(transporte -> {
                    transporte.setTipo(transporteDetails.getTipo());
                    transporte.setProveedor(transporteDetails.getProveedor());
                    transporte.setNumero_transporte(transporteDetails.getNumero_transporte());
                    transporte.setSalida(transporteDetails.getSalida());
                    transporte.setLlegada(transporteDetails.getLlegada());
                    transporte.setOrigen(transporteDetails.getOrigen());
                    transporte.setDestino(transporteDetails.getDestino());
                    return ResponseEntity.ok(transporteRepository.save(transporte));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransporte(@PathVariable int id) {
        transporteRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
