package com.travelgo.backend_travelgo.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.travelgo.backend_travelgo.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para generar PDFs de confirmación de reserva
 */
@Service
public class PDFService {
    
    private static final Logger logger = LoggerFactory.getLogger(PDFService.class);
    
    // Colores del tema TravelGo
    private static final BaseColor COLOR_COSMIC = new BaseColor(185, 124, 185); // #b97cb9
    private static final BaseColor COLOR_ASTRONAUT = new BaseColor(33, 43, 74); // #212b4a
    private static final BaseColor COLOR_FLAME = new BaseColor(221, 92, 73); // #dd5c49
    
    /**
     * Generar PDF de confirmación de reserva
     */
    public byte[] generateReservationPDF(Reserva reserva, Viaje viaje, Usuario usuario, Pago pago) {
        logger.info("Generando PDF para reserva: {}", reserva.getId());
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            
            // Agregar evento de pie de página
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    try {
                        PdfContentByte canvas = writer.getDirectContent();
                        ColumnText.showTextAligned(
                            canvas,
                            Element.ALIGN_CENTER,
                            new Phrase("TravelGo - Tu aventura comienza aquí", 
                                      FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY)),
                            document.getPageSize().getWidth() / 2,
                            30,
                            0
                        );
                    } catch (Exception e) {
                        logger.error("Error en pie de página", e);
                    }
                }
            });
            
            document.open();
            
            // === ENCABEZADO ===
            addHeader(document);
            
            // === INFORMACIÓN DEL PASAJERO ===
            addPassengerInfo(document, usuario, reserva);
            
            // === DETALLES DEL VUELO ===
            addFlightDetails(document, viaje);
            
            // === INFORMACIÓN DE PAGO ===
            addPaymentInfo(document, pago);
            
            // === INFORMACIÓN IMPORTANTE ===
            addImportantInfo(document);
            
            // === PIE DE PÁGINA ===
            addFooter(document);
            
            document.close();
            
            logger.info("PDF generado exitosamente: {} bytes", baos.size());
            return baos.toByteArray();
            
        } catch (Exception e) {
            logger.error("Error al generar PDF", e);
            throw new RuntimeException("Error al generar PDF de confirmación", e);
        }
    }
    
    /**
     * Agregar encabezado del documento
     */
    private void addHeader(Document document) throws DocumentException {
        // Logo y título (simulado con texto)
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, COLOR_COSMIC);
        Paragraph title = new Paragraph("TravelGo", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5);
        document.add(title);
        
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12, COLOR_ASTRONAUT);
        Paragraph subtitle = new Paragraph("Confirmación de Reserva", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        document.add(subtitle);
        
        // Línea separadora
        LineSeparator line = new LineSeparator();
        line.setLineColor(COLOR_COSMIC);
        document.add(new Chunk(line));
        document.add(Chunk.NEWLINE);
    }
    
    /**
     * Agregar información del pasajero
     */
    private void addPassengerInfo(Document document, Usuario usuario, Reserva reserva) 
            throws DocumentException {
        
        // Título de sección
        addSectionTitle(document, "Información del Pasajero");
        
        // Tabla de información
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);
        
        addTableRow(table, "Nombre Completo:", 
                   usuario.getPrimerNombre() + " " + usuario.getPrimerApellido());
        addTableRow(table, "Email:", usuario.getCredencial().getCorreo());
        addTableRow(table, "Teléfono:", usuario.getTelefono());
        addTableRow(table, "Número de Reserva:", "TG-" + reserva.getId());
        addTableRow(table, "Fecha de Reserva:", 
                   reserva.getFechaReserva().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        addTableRow(table, "Estado:", reserva.getEstado().toString().toUpperCase());
        
        document.add(table);
    }
    
    /**
     * Agregar detalles del vuelo
     */
    private void addFlightDetails(Document document, Viaje viaje) throws DocumentException {
        addSectionTitle(document, "Detalles del Vuelo");
        
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);
        
        addTableRow(table, "Origen:", viaje.getOrigin());
        addTableRow(table, "Destino:", viaje.getDestinationCode());
        addTableRow(table, "Fecha de Salida:", 
                   viaje.getDepartureDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        if (viaje.getReturnDate() != null) {
            addTableRow(table, "Fecha de Regreso:", 
                       viaje.getReturnDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            addTableRow(table, "Tipo de Viaje:", "Ida y Vuelta");
        } else {
            addTableRow(table, "Tipo de Viaje:", "Solo Ida");
        }
        
        if (viaje.getAirline() != null) {
            addTableRow(table, "Aerolínea:", viaje.getAirline());
        }
        
        if (viaje.getFlightOfferId() != null) {
            addTableRow(table, "ID de Vuelo:", viaje.getFlightOfferId());
        }
        
        document.add(table);
    }
    
    /**
     * Agregar información de pago
     */
    private void addPaymentInfo(Document document, Pago pago) throws DocumentException {
        addSectionTitle(document, "Información de Pago");
        
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);
        
        addTableRow(table, "Método de Pago:", pago.getMetodoPago().toString());
        addTableRow(table, "Monto Total:", 
                   pago.getMonto().toString() + " USD");
        addTableRow(table, "Estado del Pago:", pago.getEstado().toString().toUpperCase());
        addTableRow(table, "Fecha de Pago:", 
                   pago.getFechaPago().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        document.add(table);
        
        // Resaltar total
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, COLOR_FLAME);
        Paragraph total = new Paragraph("Total Pagado: " + pago.getMonto() + " USD", totalFont);
        total.setAlignment(Element.ALIGN_RIGHT);
        total.setSpacingAfter(20);
        document.add(total);
    }
    
    /**
     * Agregar información importante
     */
    private void addImportantInfo(Document document) throws DocumentException {
        addSectionTitle(document, "Información Importante");
        
        Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        
        Paragraph info = new Paragraph();
        info.setFont(infoFont);
        info.setAlignment(Element.ALIGN_JUSTIFIED);
        info.setSpacingAfter(10);
        
        info.add(new Chunk("• ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        info.add("Presenta este documento en el mostrador de check-in del aeropuerto.\n");
        
        info.add(new Chunk("• ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        info.add("Te recomendamos llegar al aeropuerto 3 horas antes para vuelos internacionales.\n");
        
        info.add(new Chunk("• ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        info.add("Verifica los requisitos de documentación y visado para tu destino.\n");
        
        info.add(new Chunk("• ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        info.add("Revisa las políticas de equipaje de tu aerolínea.\n");
        
        info.add(new Chunk("• ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        info.add("Para cambios o cancelaciones, contacta a nuestro servicio al cliente.\n");
        
        document.add(info);
        
        // Cuadro de contacto
        PdfPTable contactTable = new PdfPTable(1);
        contactTable.setWidthPercentage(100);
        contactTable.setSpacingBefore(10);
        contactTable.setSpacingAfter(20);
        
        PdfPCell contactCell = new PdfPCell();
        contactCell.setBackgroundColor(new BaseColor(238, 223, 239)); // cosmic-light
        contactCell.setPadding(10);
        contactCell.setBorder(Rectangle.NO_BORDER);
        
        Font contactFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        Paragraph contactInfo = new Paragraph();
        contactInfo.setFont(contactFont);
        contactInfo.add(new Chunk("Servicio al Cliente\n", 
                                 FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        contactInfo.add("Email: soporte@travelgo.com\n");
        contactInfo.add("Teléfono: +57 300 123 4567\n");
        contactInfo.add("WhatsApp: +57 300 123 4567\n");
        contactInfo.add("Horario: Lunes a Domingo 24/7");
        
        contactCell.addElement(contactInfo);
        contactTable.addCell(contactCell);
        
        document.add(contactTable);
    }
    
    /**
     * Agregar pie de documento
     */
    private void addFooter(Document document) throws DocumentException {
        // Línea separadora
        LineSeparator line = new LineSeparator();
        line.setLineColor(COLOR_COSMIC);
        document.add(new Chunk(line));
        document.add(Chunk.NEWLINE);
        
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.GRAY);
        Paragraph footer = new Paragraph();
        footer.setFont(footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.add("¡Gracias por confiar en TravelGo!\n");
        footer.add("Síguenos en redes sociales: @TravelGo\n");
        footer.add("www.travelgo.com");
        
        document.add(footer);
    }
    
    /**
     * Agregar título de sección
     */
    private void addSectionTitle(Document document, String title) throws DocumentException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, COLOR_ASTRONAUT);
        Paragraph section = new Paragraph(title, sectionFont);
        section.setSpacingBefore(10);
        section.setSpacingAfter(10);
        document.add(section);
    }
    
    /**
     * Agregar fila a tabla
     */
    private void addTableRow(PdfPTable table, String label, String value) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        labelCell.setBackgroundColor(new BaseColor(238, 223, 239)); // cosmic-light
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}