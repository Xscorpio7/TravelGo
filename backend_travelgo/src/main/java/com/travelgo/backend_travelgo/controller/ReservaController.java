package com.travelgo.backend_travelgo.controller;

import com.travelgo.backend_travelgo.model.*;
import com.travelgo.backend_travelgo.repository.*;
import com.travelgo.backend_travelgo.service.PDFService;
import com.travelgo.backend_travelgo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin("*")
public class ReservaController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReservaController.class);
    
    @Autowired
    private ReservaRepository reservaRepository;
    
    @Autowired
    private ViajeRepository viajeRepository;
    
    @Autowired
    private PagoRepository pagoRepository;
    
    @Autowired
    private AlojamientoRepository alojamientoRepository;
    
    @Autowired
    private TransporteRepository transporteRepository;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PDFService pdfService;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    /**
     * ✅ NUEVO: Obtener reservas con TODOS los detalles relacionados
     * GET /api/reservas/usuario/{usuarioId}/completas
     */
    @GetMapping("/usuario/{usuarioId}/completas")
    public ResponseEntity<?> getReservasCompletasUsuario(
            @PathVariable Integer usuarioId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // Verificar autenticación
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Token no proporcionado"));
            }
            
            String token = authHeader.substring(7);
            
            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token expirado"));
            }
            
            // Verificar permisos
            Integer tokenUsuarioId = jwtUtil.extractUsuarioId(token);
            String tipoUsuario = jwtUtil.extractTipoUsuario(token);
            
            if (!tokenUsuarioId.equals(usuarioId) && !"admin".equalsIgnoreCase(tipoUsuario)) {
                return ResponseEntity.status(403).body(Map.of("error", "No tienes permiso"));
            }
            
            logger.info("📋 Obteniendo reservas completas para usuario: {}", usuarioId);
            
            // Obtener reservas
            List<Reserva> reservas = reservaRepository.findByUsuarioId(usuarioId);
            
            // Construir respuesta con todos los detalles
            List<Map<String, Object>> reservasCompletas = reservas.stream()
                .map(reserva -> {
                    Map<String, Object> detalle = new HashMap<>();
                    
                    // Datos básicos de reserva
                    detalle.put("id", reserva.getId());
                    detalle.put("fechaReserva", reserva.getFechaReserva());
                    detalle.put("estado", reserva.getEstado().name());
                    
                    // Datos del viaje (vuelo)
                    if (reserva.getViajeId() != null) {
                        viajeRepository.findById(reserva.getViajeId()).ifPresent(viaje -> {
                            Map<String, Object> viajeData = new HashMap<>();
                            viajeData.put("id", viaje.getId());
                            viajeData.put("origen", viaje.getOrigin());
                            viajeData.put("destino", viaje.getDestinationCode());
                            viajeData.put("fechaSalida", viaje.getDepartureDate());
                            viajeData.put("fechaRegreso", viaje.getReturnDate());
                            viajeData.put("aerolinea", viaje.getAirlineName());
                            viajeData.put("precio", viaje.getPrecio());
                            viajeData.put("moneda", viaje.getCurrency());
                            viajeData.put("tipoViaje", viaje.getJourneyType());
                            detalle.put("viaje", viajeData);
                        });
                    }
                    
                    // Datos del alojamiento (hotel)
                    if (reserva.getAlojamientoId() != null) {
                        alojamientoRepository.findById(reserva.getAlojamientoId()).ifPresent(hotel -> {
                            Map<String, Object> hotelData = new HashMap<>();
                            hotelData.put("id", hotel.getId());
                            hotelData.put("nombre", hotel.getNombre());
                            hotelData.put("ciudad", hotel.getCiudad());
                            hotelData.put("tipo", hotel.getTipo());
                            hotelData.put("precio", hotel.getPrecio());
                            detalle.put("hotel", hotelData);
                        });
                    }
                    
                    // Datos del transporte
                    if (reserva.getTransporteId() != null) {
                        transporteRepository.findById(reserva.getTransporteId()).ifPresent(transporte -> {
                            Map<String, Object> transporteData = new HashMap<>();
                            transporteData.put("id", transporte.getId());
                            transporteData.put("tipo", transporte.getTipo());
                            transporteData.put("origen", transporte.getOrigen());
                            transporteData.put("destino", transporte.getDestino());
                            transporteData.put("vehiculoTipo", transporte.getVehiculoTipo());
                            transporteData.put("precio", transporte.getPrecio());
                            transporteData.put("moneda", transporte.getCurrency());
                            detalle.put("transporte", transporteData);
                        });
                    }
                    
                    // Datos del pago
                    pagoRepository.findByReservaId(reserva.getId()).stream().findFirst().ifPresent(pago -> {
                        Map<String, Object> pagoData = new HashMap<>();
                        pagoData.put("id", pago.getId());
                        pagoData.put("metodoPago", pago.getMetodoPago().name());
                        pagoData.put("monto", pago.getMonto());
                        pagoData.put("estado", pago.getEstado().name());
                        pagoData.put("fechaPago", pago.getFechaPago());
                        detalle.put("pago", pagoData);
                    });
                    
                    // Número de confirmación
                    detalle.put("numeroConfirmacion", "TG-" + String.format("%08d", reserva.getId()));
                    
                    return detalle;
                })
                .collect(Collectors.toList());
            
            logger.info("✅ Encontradas {} reservas completas", reservasCompletas.size());
            
            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "data", reservasCompletas,
                "count", reservasCompletas.size()
            ));
            
        } catch (Exception e) {
            logger.error("❌ Error al obtener reservas completas: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Error al obtener reservas: " + e.getMessage()
            ));
        }
    }
    
    /**
     * ✅ Cancelar una reserva
     * PUT /api/reservas/{id}/cancelar
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarReserva(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Token no proporcionado"));
            }
            
            String token = authHeader.substring(7);
            Integer usuarioId = jwtUtil.extractUsuarioId(token);
            
            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token expirado"));
            }
            
            logger.info("❌ Cancelando reserva {} por usuario {}", id, usuarioId);
            
            Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
            
            // Verificar permisos
            if (!reserva.getUsuarioId().equals(usuarioId)) {
                return ResponseEntity.status(403).body(Map.of(
                    "error", "No tienes permiso para cancelar esta reserva"
                ));
            }
            
            // No permitir cancelar si ya está cancelada
            if (reserva.getEstado() == Reserva.Estado.cancelada) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Esta reserva ya está cancelada"
                ));
            }
            
            reserva.setEstado(Reserva.Estado.cancelada);
            reservaRepository.save(reserva);
            
            logger.info("✅ Reserva {} cancelada exitosamente", id);
            
            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Reserva cancelada correctamente",
                "reservaId", id
            ));
            
        } catch (Exception e) {
            logger.error("❌ Error al cancelar reserva: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Error al cancelar reserva: " + e.getMessage()
            ));
        }
    }
    @GetMapping("/{id}/pdf")
public ResponseEntity<?> descargarPDFReserva(
        @PathVariable Integer id,
        @RequestHeader(value = "Authorization", required = false) String authHeader) {
    
    try {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "Token no proporcionado"));
        }
        
        String token = authHeader.substring(7);
        Integer usuarioId = jwtUtil.extractUsuarioId(token);
        
        if (jwtUtil.isTokenExpired(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Token expirado"));
        }
        
        logger.info("📄 Generando PDF para reserva: {}", id);
        
        // Obtener reserva
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        
        // Verificar permisos
        if (!reserva.getUsuarioId().equals(usuarioId)) {
            return ResponseEntity.status(403).body(Map.of(
                "error", "No tienes permiso para ver esta reserva"
            ));
        }
        
        // Obtener datos relacionados
        Viaje viaje = viajeRepository.findById(reserva.getViajeId())
            .orElseThrow(() -> new RuntimeException("Viaje no encontrado"));
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Pago pago = pagoRepository.findByReservaId(id).stream().findFirst()
            .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        
        // Generar PDF
        byte[] pdfBytes = pdfService.generateReservationPDF(reserva, viaje, usuario, pago);
        
        logger.info("✅ PDF generado: {} bytes", pdfBytes.length);
        
        // Devolver PDF
        return ResponseEntity.ok()
            .header("Content-Type", "application/pdf")
            .header("Content-Disposition", "attachment; filename=Reserva_TG-" + 
                    String.format("%08d", id) + ".pdf")
            .body(pdfBytes);
        
    } catch (Exception e) {
        logger.error("❌ Error al generar PDF: {}", e.getMessage(), e);
        return ResponseEntity.status(500).body(Map.of(
            "error", "Error al generar PDF: " + e.getMessage()
        ));
    }
}
}