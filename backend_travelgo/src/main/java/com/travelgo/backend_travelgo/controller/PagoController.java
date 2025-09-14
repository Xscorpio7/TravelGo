package com.travelgo.backend_travelgo.controller;


import com.travelgo.backend_travelgo.model.Pago;
import com.travelgo.backend_travelgo.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pago")
@CrossOrigin("*")
public class PagoController {

    @Autowired
    private PagoRepository pagoRepository;
    

    @GetMapping
    public List<Pago> getAllPagos() {
        return pagoRepository.findAll();
    }

    @PostMapping
    public Pago creatorPago(@RequestBody Pago pago) {
        return pagoRepository.save(pago);
    }

    @PutMapping("/{id}")
public ResponseEntity<Pago> updatePago(@PathVariable int id, @RequestBody Pago pagoDetails) {
    return pagoRepository.findById(id)
            .map(pago -> {
                pago.setMetodoPago(pagoDetails.getMetodoPago());
                pago.setMonto(pagoDetails.getMonto());
                pago.setEstado(pagoDetails.getEstado());
                pago.setFechaPago(pagoDetails.getFechaPago());
                return ResponseEntity.ok(pagoRepository.save(pago));
            }).orElse(ResponseEntity.notFound().build());
}


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePago(@PathVariable int id) {
        pagoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}