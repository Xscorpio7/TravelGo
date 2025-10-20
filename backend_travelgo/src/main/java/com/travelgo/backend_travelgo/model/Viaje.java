package com.travelgo.backend_travelgo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "viajes")
public class Viaje {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // Campos originales de TravelGo
    @Column(name = "titulo", length = 100, nullable = true)
    private String titulo;
    
    @Column(name = "descripcion", length = 100, nullable = true)
    private String descripcion;
    
    @Column(name = "destino", length = 100, nullable = true)
    private String destino;
    
    @Column(name = "telefono", length = 20, nullable = true)
    private String telefono;
    
    @Column(name = "fecha_inicio", nullable = true)
    private LocalDate fechaInicio;
    
    @Column(name = "fecha_fin", nullable = true)
    private LocalDate fechaFin;
    
    @Column(name = "precio", precision = 10, scale = 2, nullable = true)
    private BigDecimal precio;
    
    @Column(name = "cupos_disponibles", nullable = true)
    private Integer cuposDisponibles;
    
    // Campos nuevos para vuelos de Amadeus
    @Column(name = "flight_offer_id", length = 50, nullable = true)
    private String flightOfferId;
    
    @Column(name = "origin", length = 10, nullable = true)
    private String origin;
    
    @Column(name = "origin_name", length = 100, nullable = true)
    private String originName;
    
    @Column(name = "destination_code", length = 10, nullable = true)
    private String destinationCode;
    
    @Column(name = "departure_date", nullable = true)
    private LocalDate departureDate;
    
    @Column(name = "return_date", nullable = true)
    private LocalDate returnDate;
    
    @Column(name = "currency", length = 3, nullable = true)
    private String currency;
    
    @Column(name = "airline", length = 50, nullable = true)
    private String airline;
    
    @Column(name = "airline_name", length = 100, nullable = true)
    private String airlineName;
    
    @Column(name = "journey_type", length = 20, nullable = true)
    private String journeyType;
    
    @Column(name = "bookable_seats", nullable = true)
    private Integer bookableSeats;
    
    @Column(name = "flight_details", columnDefinition = "LONGTEXT", nullable = true)
    private String flightDetails;
    
    @Column(name = "tipo_viaje", length = 50, nullable = true)
    private String tipoViaje;
    
    @Column(name = "created_at", updatable = false, nullable = true)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructores
    public Viaje() {
    }
    
    public Viaje(String titulo, String descripcion, String destino, String telefono,
                 LocalDate fechaInicio, LocalDate fechaFin, BigDecimal precio, Integer cuposDisponibles) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.destino = destino;
        this.telefono = telefono;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.precio = precio;
        this.cuposDisponibles = cuposDisponibles;
        this.tipoViaje = "paquete";
    }
    
    // Getters y Setters - Campos originales
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public void setPrecio(Double precio) {
        this.precio = precio != null ? BigDecimal.valueOf(precio) : null;
    }
    
    public Integer getCuposDisponibles() { return cuposDisponibles; }
    public void setCuposDisponibles(Integer cuposDisponibles) { this.cuposDisponibles = cuposDisponibles; }
    
    // Getters y Setters - Campos nuevos para Amadeus
    public String getFlightOfferId() { return flightOfferId; }
    public void setFlightOfferId(String flightOfferId) { this.flightOfferId = flightOfferId; }
    
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    
    public String getOriginName() { return originName; }
    public void setOriginName(String originName) { this.originName = originName; }
    
    public String getDestinationCode() { return destinationCode; }
    public void setDestinationCode(String destinationCode) { this.destinationCode = destinationCode; }
    
    public String getDestination() { return destinationCode; }
    public void setDestination(String destination) { this.destinationCode = destination; }
    
    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }
    
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getAirline() { return airline; }
    public void setAirline(String airline) { this.airline = airline; }
    
    public String getAirlineName() { return airlineName; }
    public void setAirlineName(String airlineName) { this.airlineName = airlineName; }
    
    public String getJourneyType() { return journeyType; }
    public void setJourneyType(String journeyType) { this.journeyType = journeyType; }
    
    public Integer getBookableSeats() { return bookableSeats; }
    public void setBookableSeats(Integer bookableSeats) { this.bookableSeats = bookableSeats; }
    
    public String getFlightDetails() { return flightDetails; }
    public void setFlightDetails(String flightDetails) { this.flightDetails = flightDetails; }
    
    public String getTipoViaje() { return tipoViaje; }
    public void setTipoViaje(String tipoViaje) { this.tipoViaje = tipoViaje; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}