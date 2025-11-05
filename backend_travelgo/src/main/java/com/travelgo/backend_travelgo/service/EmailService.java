package com.travelgo.backend_travelgo.service;

import com.travelgo.backend_travelgo.model.Pago;
import com.travelgo.backend_travelgo.model.Viaje;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para envío de emails
 * ✅ ACTUALIZADO para Spring Boot 3.x con Jakarta Mail
 */
@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:noreply@travelgo.com}")
    private String fromEmail;
    
    /**
     * Enviar email de confirmación de reserva con PDF adjunto
     */
    public void sendReservationConfirmation(
            String toEmail,
            String userName,
            String confirmationNumber,
            Viaje viaje,
            Pago pago,
            byte[] pdfBytes) {
        
        logger.info("Enviando email de confirmación a: {}", toEmail);
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, "TravelGo");
            helper.setTo(toEmail);
            helper.setSubject("✈️ Confirmación de Reserva - " + confirmationNumber);
            
            // Contenido HTML del email
            String htmlContent = buildEmailHtml(userName, confirmationNumber, viaje, pago);
            helper.setText(htmlContent, true);
            
            // ✅ CORRECCIÓN: Usar ByteArrayResource en lugar de ByteArrayDataSource
            helper.addAttachment(
                "Confirmacion_Reserva_" + confirmationNumber + ".pdf",
                new ByteArrayResource(pdfBytes)
            );
            
            mailSender.send(message);
            logger.info("Email enviado exitosamente a: {}", toEmail);
            
        } catch (Exception e) {
            logger.error("Error al enviar email a: {}", toEmail, e);
            // No lanzar excepción para no bloquear la reserva
            // En producción, podrías reintentar o guardar en cola
        }
    }
    
    /**
     * Construir HTML del email
     */
    private String buildEmailHtml(String userName, String confirmationNumber, 
                                  Viaje viaje, Pago pago) {
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        return "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "    <meta charset='UTF-8'>" +
            "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
            "    <style>" +
            "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
            "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
            "        .header { background: linear-gradient(135deg, #b97cb9 0%, #212b4a 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
            "        .header h1 { margin: 0; font-size: 28px; }" +
            "        .content { background: #ffffff; padding: 30px; border: 1px solid #e0e0e0; }" +
            "        .confirmation-box { background: #eedfef; padding: 20px; border-radius: 8px; text-align: center; margin: 20px 0; }" +
            "        .confirmation-number { font-size: 24px; font-weight: bold; color: #b97cb9; margin: 10px 0; }" +
            "        .info-section { margin: 20px 0; padding: 15px; background: #f8f9fa; border-left: 4px solid #b97cb9; }" +
            "        .info-section h3 { margin-top: 0; color: #212b4a; }" +
            "        .info-row { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #e0e0e0; }" +
            "        .info-label { font-weight: bold; color: #666; }" +
            "        .info-value { color: #333; }" +
            "        .total-box { background: #dd5c49; color: white; padding: 15px; border-radius: 8px; text-align: center; margin: 20px 0; }" +
            "        .total-amount { font-size: 32px; font-weight: bold; }" +
            "        .button { display: inline-block; background: #b97cb9; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }" +
            "        .footer { background: #212b4a; color: white; padding: 20px; text-align: center; border-radius: 0 0 10px 10px; font-size: 12px; }" +
            "        .social-icons { margin: 15px 0; }" +
            "        .important-note { background: #fff3cd; border: 1px solid #ffc107; padding: 15px; border-radius: 5px; margin: 20px 0; }" +
            "    </style>" +
            "</head>" +
            "<body>" +
            "    <div class='container'>" +
            "        <div class='header'>" +
            "            <h1>✈️ TravelGo</h1>" +
            "            <p>Tu aventura comienza aquí</p>" +
            "        </div>" +
            "        " +
            "        <div class='content'>" +
            "            <h2>¡Hola " + userName + "!</h2>" +
            "            <p>Tu reserva ha sido confirmada exitosamente. Estamos emocionados de ser parte de tu próximo viaje.</p>" +
            "            " +
            "            <div class='confirmation-box'>" +
            "                <p style='margin: 0; font-size: 14px;'>Número de Confirmación</p>" +
            "                <div class='confirmation-number'>" + confirmationNumber + "</div>" +
            "                <p style='margin: 0; font-size: 12px; color: #666;'>Guarda este número para futuras referencias</p>" +
            "            </div>" +
            "            " +
            "            <div class='info-section'>" +
            "                <h3>📍 Detalles del Vuelo</h3>" +
            "                <div class='info-row'>" +
            "                    <span class='info-label'>Origen:</span>" +
            "                    <span class='info-value'>" + viaje.getOrigin() + "</span>" +
            "                </div>" +
            "                <div class='info-row'>" +
            "                    <span class='info-label'>Destino:</span>" +
            "                    <span class='info-value'>" + viaje.getDestinationCode() + "</span>" +
            "                </div>" +
            "                <div class='info-row'>" +
            "                    <span class='info-label'>Fecha de Salida:</span>" +
            "                    <span class='info-value'>" + viaje.getDepartureDate().format(dateFormatter) + "</span>" +
            "                </div>" +
            (viaje.getReturnDate() != null ? 
            "                <div class='info-row'>" +
            "                    <span class='info-label'>Fecha de Regreso:</span>" +
            "                    <span class='info-value'>" + viaje.getReturnDate().format(dateFormatter) + "</span>" +
            "                </div>" : "") +
            "            </div>" +
            "            " +
            "            <div class='total-box'>" +
            "                <p style='margin: 0; font-size: 14px;'>Total Pagado</p>" +
            "                <div class='total-amount'>" + pago.getMonto() + " USD</div>" +
            "                <p style='margin: 0; font-size: 12px;'>Método: " + pago.getMetodoPago() + "</p>" +
            "            </div>" +
            "            " +
            "            <div class='important-note'>" +
            "                <strong>⚠️ Información Importante:</strong>" +
            "                <ul style='margin: 10px 0; padding-left: 20px;'>" +
            "                    <li>Presenta tu documento de confirmación en el check-in</li>" +
            "                    <li>Llega 3 horas antes para vuelos internacionales</li>" +
            "                    <li>Verifica los requisitos de visado para tu destino</li>" +
            "                    <li>Revisa las políticas de equipaje de tu aerolínea</li>" +
            "                </ul>" +
            "            </div>" +
            "            " +
            "            <p style='text-align: center;'>" +
            "                <strong>📎 Documento Adjunto:</strong> Tu confirmación en PDF está adjunta a este email." +
            "            </p>" +
            "            " +
            "            <div style='text-align: center;'>" +
            "                <a href='https://travelgo.com/reservas' class='button'>Ver Mi Reserva Online</a>" +
            "            </div>" +
            "            " +
            "            <div style='margin-top: 30px; padding-top: 20px; border-top: 2px solid #e0e0e0;'>" +
            "                <h3 style='color: #212b4a;'>📞 ¿Necesitas Ayuda?</h3>" +
            "                <p>Nuestro equipo está disponible 24/7 para asistirte:</p>" +
            "                <p>" +
            "                    <strong>Email:</strong> soporte@travelgo.com<br>" +
            "                    <strong>Teléfono:</strong> +57 300 123 4567<br>" +
            "                    <strong>WhatsApp:</strong> +57 300 123 4567" +
            "                </p>" +
            "            </div>" +
            "        </div>" +
            "        " +
            "        <div class='footer'>" +
            "            <p style='margin: 10px 0;'><strong>¡Gracias por confiar en TravelGo!</strong></p>" +
            "            <div class='social-icons'>" +
            "                <p>Síguenos en redes sociales:</p>" +
            "                <p>Facebook | Instagram | Twitter | LinkedIn</p>" +
            "            </div>" +
            "            <p style='margin: 10px 0; font-size: 11px;'>" +
            "                TravelGo © 2025. Todos los derechos reservados.<br>" +
            "                Este es un email automático, por favor no respondas a este mensaje." +
            "            </p>" +
            "        </div>" +
            "    </div>" +
            "</body>" +
            "</html>";
    }
}