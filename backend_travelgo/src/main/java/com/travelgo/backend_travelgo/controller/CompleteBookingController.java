package com.travelgo.backend_travelgo.controller;

import com.travelgo.backend_travelgo.model.*;
import com.travelgo.backend_travelgo.repository.*;
import com.travelgo.backend_travelgo.service.EmailService;
import com.travelgo.backend_travelgo.service.PDFService;
import com.travelgo.backend_travelgo.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin("*")
public class CompleteBookingController {
    
    private static final Logger logger = LoggerFactory.getLogger(CompleteBookingController.class);
    
    @Autowired
    private ViajeRepository viajeRepository;
    
    @Autowired
    private ReservaRepository reservaRepository;
    
    @Autowired
    private PagoRepository pagoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PDFService pdfService;
    
    /**
     * Procesar reserva completa con pago
     * POST /api/bookings/complete
     */
    @PostMapping("/complete")
    public ResponseEntity<?> completeBooking(
            @RequestBody Map<String, Object> bookingData,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // Verificar token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Token no proporcionado"));
            }
            
            String token = authHeader.substring(7);
            Integer usuarioId = jwtUtil.extractUsuarioId(token);
            
            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token expirado"));
            }
            
            logger.info("Procesando reserva completa para usuario: {}", usuarioId);
            
            // 1. Obtener usuario
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // 2. Guardar viaje
            Map<String, Object> flightData = (Map<String, Object>) bookingData.get("flightData");
            Map<String, Object> searchData = (Map<String, Object>) bookingData.get("searchData");
            
            Viaje viaje = new Viaje();
            viaje.setFlightOfferId((String) flightData.get("id"));
            viaje.setOrigin((String) searchData.get("origin"));
            viaje.setDestinationCode((String) searchData.get("destination"));
            viaje.setDepartureDate(LocalDate.parse((String) searchData.get("departureDate")));
            
            if (searchData.get("returnDate") != null) {
                String returnDateStr = (String) searchData.get("returnDate");
                if (!returnDateStr.isEmpty()) {
                    viaje.setReturnDate(LocalDate.parse(returnDateStr));
                }
            }
            
            // Obtener precio del vuelo
            Map<String, Object> price = (Map<String, Object>) flightData.get("price");
            viaje.setPrecio(new BigDecimal(price.get("total").toString()));
            viaje.setCurrency((String) price.get("currency"));
            
            viaje.setTipoViaje("vuelo");
            viaje.setTitulo(viaje.getOrigin() + " → " + viaje.getDestinationCode());
            
            viajeRepository.save(viaje);
            logger.info("Viaje guardado: {}", viaje.getId());
            
            // 3. Crear reserva
            Reserva reserva = new Reserva();
            reserva.setUsuarioId(usuarioId);
            reserva.setViajeId(viaje.getId());
            
            if (bookingData.get("alojamientoId") != null) {
                reserva.setAlojamientoId(Integer.parseInt(bookingData.get("alojamientoId").toString()));
            }
            
            if (bookingData.get("transporteId") != null) {
                reserva.setTransporteId(Integer.parseInt(bookingData.get("transporteId").toString()));
            }
            
            reserva.setEstado(Reserva.Estado.confirmada);
            reservaRepository.save(reserva);
            logger.info("Reserva creada: {}", reserva.getId());
            
            // 4. Procesar pago
            Map<String, Object> paymentData = (Map<String, Object>) bookingData.get("paymentData");
            
            Pago pago = new Pago();
            pago.setReserva(reserva);
            pago.setMonto(viaje.getPrecio());
            pago.setMetodoPago(Pago.MetodoPago.valueOf((String) paymentData.get("metodoPago")));
            pago.setEstado(Pago.Estado.pagado);
            pago.setFechaPago(LocalDate.now());
            
            pagoRepository.save(pago);
            logger.info("Pago procesado: {}", pago.getId());
            
            // 5. Generar número de confirmación
            String confirmationNumber = "TG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            // 6. Generar PDF
            byte[] pdfBytes = pdfService.generateReservationPDF(reserva, viaje, usuario, pago);
            logger.info("PDF generado: {} bytes", pdfBytes.length);
            
            // 7. Enviar email con PDF adjunto
            String userEmail = usuario.getCredencial().getCorreo();
            emailService.sendReservationConfirmation(
                userEmail,
                usuario.getPrimerNombre(),
                confirmationNumber,
                viaje,
                pago,
                pdfBytes
            );
            logger.info("Email enviado a: {}", userEmail);
            
            // 8. Respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Reserva confirmada exitosamente");
            response.put("confirmationNumber", confirmationNumber);
            response.put("reservaId", reserva.getId());
            response.put("viajeId", viaje.getId());
            response.put("pagoId", pago.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al procesar reserva completa: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al procesar la reserva: " + e.getMessage()));
        }
    }
}