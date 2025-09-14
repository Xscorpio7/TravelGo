package com.travelgo.backend_travelgo.controller;

import com.travelgo.backend_travelgo.model.Reserva;
import com.travelgo.backend_travelgo.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin("*")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @GetMapping
    public List<Reserva> getAllReservas() {
        return reservaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> getReservaById(@PathVariable int id) {
        return reservaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Reserva createReserva(@RequestBody Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reserva> updateReserva(@PathVariable int id, @RequestBody Reserva reservaDetails) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    reserva.setUsuario(reservaDetails.getUsuario());
                    reserva.setViaje(reservaDetails.getViaje());
                    reserva.setAlojamiento(reservaDetails.getAlojamiento());
                    reserva.setTransporte(reservaDetails.getTransporte());
                    reserva.setFechaReserva(reservaDetails.getFechaReserva());
                    reserva.setEstado(reservaDetails.getEstado());
                    return ResponseEntity.ok(reservaRepository.save(reserva));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable int id) {
        reservaRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
