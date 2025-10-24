
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.HotelOfferSearch;
import com.travelgo.backend_travelgo.service.AmadeusConnect;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;

@RestController
@RequestMapping("/alojamiento")
@CrossOrigin(origins = \"*\")
public class HotelController {

    private static final Logger logger = LoggerFactory.getLogger(HotelController.class);
    private final AmadeusConnect amadeusConnect;
    private final Gson gson = new Gson();

    public HotelController(AmadeusConnect amadeusConnect) {
        this.amadeusConnect = amadeusConnect;
        logger.info("HotelController inicializado correctamente");
    }
    
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        logger.info("Hotel test endpoint llamado");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message","Hotel Controller funcionando");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping(value = "/search", produces = "application/json")
    public ResponseEntity<String> search(
            @RequestParam String cityCode,
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate,
            @RequestParam(defaultValue = "1") int adults,
            @RequestParam(defaultValue = "1") int rooms) {

        logger.info("🏨 Búsqueda de hoteles: Ciudad: {}, Check-in: {}, Check-out: {}, Adultos: {}, Habitaciones: {}", 
                   cityCode, checkInDate, checkOutDate, adults, rooms);

        try {
            // Validaciones
            if (cityCode == null || cityCode.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("{"error":"El parámetro 'cityCode' es requerido"}");
            }
            
            if (checkInDate == null || checkInDate.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(\"{\"error\\":\\"El parámetro 'checkInDate' es requerido (formato: YYYY-MM-DD)\\"}\");
            }
            
            if (checkOutDate == null || checkOutDate.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(\"{\\"error\":\\"El parámetro 'checkOutDate' es requerido (formato: YYYY-MM-DD)\\"}\");
            }
            
            // Validar formatos de fecha
            try {
                LocalDate checkIn = LocalDate.parse(checkInDate, DateTimeFormatter.ISO_LOCAL_DATE);
                LocalDate checkOut = LocalDate.parse(checkOutDate, DateTimeFormatter.ISO_LOCAL_DATE);
                
                if (checkIn.isBefore(LocalDate.now())) {
                    return ResponseEntity.badRequest()
                        .body(\"{\\"error\\":\\"La fecha de check-in no puede ser en el pasado\\"}\");
                }
                
                if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
                    return ResponseEntity.badRequest()
                        .body(\"{\\"error\\":\\"La fecha de check-out debe ser posterior a la fecha de check-in\\"}\");
                }
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest()
                    .body(\"{\\"error\\":\\"Formato de fecha incorrecto. Use YYYY-MM-DD\\"}\");
            }
            
            if (adults < 1 || adults > 9) {
                return ResponseEntity.badRequest()
                    .body(\"{\\"error\\":\\"El número de adultos debe estar entre 1 y 9\\"}\");
            }
            
            if (rooms < 1 || rooms > 9) {
                return ResponseEntity.badRequest()
                    .body(\"{\\"error\\":\\"El número de habitaciones debe estar entre 1 y 9\\"}\");
            }
            
            // Buscar hoteles
            HotelOfferSearch[] hotels = amadeusConnect.searchHotels(
                cityCode.toUpperCase().trim(), 
                checkInDate.trim(), 
                checkOutDate.trim(),
                adults,
                rooms
            );

            Map<String, Object> response = new HashMap<>();
            response.put(\"status\", \"SUCCESS\");
            response.put(\"data\", hotels);
            response.put(\"count\", hotels.length);
            response.put(\"search\", Map.of(
                \"cityCode\", cityCode.toUpperCase().trim(),
                \"checkInDate\", checkInDate.trim(),
                \"checkOutDate\", checkOutDate.trim(),
                \"adults\", adults,
                \"rooms\", rooms
            ));

            String json = gson.toJson(response);
            logger.info(\"✅ Búsqueda de hoteles exitosa: {} hoteles encontrados\", hotels.length);

            return ResponseEntity.ok(json);

        } catch (ResponseException e) {
            logger.error(\"❌ Error de Amadeus API en hoteles\", e);
            String errorMsg = e.getMessage() != null ? e.getMessage().replace(\"\\"\", \"'\") : \"Error desconocido\";
            return ResponseEntity.badRequest()
                    .body(\"{\\"error\\":\\"Error Amadeus: \" + errorMsg + \"\\"}\");
                    
        } catch (Exception e) {
            logger.error(\"❌ Error en búsqueda de hoteles\", e);
            String errorMsg = e.getMessage() != null ? e.getMessage().replace(\"\\"\", \"'\") : \"Error interno\";
            return ResponseEntity.internalServerError()
                    .body(\"{\\"error\\":\\"\" + errorMsg + \"\\"}\");
        }
    }
}
