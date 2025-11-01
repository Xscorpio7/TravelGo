package com.amadeus.resources;

/**
 * TransferOffering - Recurso de Amadeus para Transfers
 * Representa una oferta de transfer disponible desde la API de Amadeus
 * VERSIÓN SIN LOMBOK - Todos los getters escritos manualmente
 */
public class TransferOffering extends Resource {
    
    private String id;
    private String type;
    private String transferType; // PRIVATE, SHARED, TAXI, etc.
    
    // Información del proveedor
    private ServiceProvider serviceProvider;
    
    // Información del vehículo
    private Vehicle vehicle;
    
    // Información de precios
    private Quotation quotation;
    
    // Información de distancia
    private Distance distance;
    
    // Tiempos
    private TransferTime start;
    private TransferTime end;
    
    // Descripción
    private String description;
    
    // Ubicaciones
    private TransferLocation startLocation;
    private TransferLocation endLocation;
    
    // Constructor protegido
    protected TransferOffering() {}
    
    // ========================================
    // GETTERS PRINCIPALES
    // ========================================
    
    public String getId() {
        return id;
    }
    
    public String getType() {
        return type;
    }
    
    public String getTransferType() {
        return transferType;
    }
    
    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public Quotation getQuotation() {
        return quotation;
    }
    
    public Distance getDistance() {
        return distance;
    }
    
    public TransferTime getStart() {
        return start;
    }
    
    public TransferTime getEnd() {
        return end;
    }
    
    public String getDescription() {
        return description;
    }
    
    public TransferLocation getStartLocation() {
        return startLocation;
    }
    
    public TransferLocation getEndLocation() {
        return endLocation;
    }
    
    // ========================================
    // CLASE: ServiceProvider
    // ========================================
    
    public static class ServiceProvider {
        private String code;
        private String name;
        private String logoUrl;
        private ContactInfo contactInfo;
        private Settings settings;
        
        public String getCode() { return code; }
        public String getName() { return name; }
        public String getLogoUrl() { return logoUrl; }
        public ContactInfo getContactInfo() { return contactInfo; }
        public Settings getSettings() { return settings; }
        
        public static class ContactInfo {
            private String phoneNumber;
            private String email;
            
            public String getPhoneNumber() { return phoneNumber; }
            public String getEmail() { return email; }
        }
        
        public static class Settings {
            private String businessIdentity;
            private Boolean resellerTransfer;
            
            public String getBusinessIdentity() { return businessIdentity; }
            public Boolean getResellerTransfer() { return resellerTransfer; }
        }
    }
    
    // ========================================
    // CLASE: Vehicle
    // ========================================
    
    public static class Vehicle {
        private String code;
        private String category;
        private String description;
        private Integer seats;
        private Integer baggages;
        private String imageURL;
        private VehicleCharacteristics characteristics;
        
        public String getCode() { return code; }
        public String getCategory() { return category; }
        public String getDescription() { return description; }
        public Integer getSeats() { return seats; }
        public Integer getBaggages() { return baggages; }
        public String getImageURL() { return imageURL; }
        public VehicleCharacteristics getCharacteristics() { return characteristics; }
        
        public static class VehicleCharacteristics {
            private Boolean airConditioning;
            private Integer maxBaggages;
            private Integer maxPassengers;
            private String vehicleType;
            
            public Boolean getAirConditioning() { return airConditioning; }
            public Integer getMaxBaggages() { return maxBaggages; }
            public Integer getMaxPassengers() { return maxPassengers; }
            public String getVehicleType() { return vehicleType; }
        }
    }
    
    // ========================================
    // CLASE: Quotation
    // ========================================
    
    public static class Quotation {
        private String monetaryAmount;
        private String currencyCode;
        private Boolean isEstimated;
        private Breakdown base;
        private Breakdown discount;
        private Breakdown fees;
        private Breakdown taxes;
        private Breakdown totalPrice;
        
        public String getMonetaryAmount() { return monetaryAmount; }
        public String getCurrencyCode() { return currencyCode; }
        public Boolean getIsEstimated() { return isEstimated; }
        public Breakdown getBase() { return base; }
        public Breakdown getDiscount() { return discount; }
        public Breakdown getFees() { return fees; }
        public Breakdown getTaxes() { return taxes; }
        public Breakdown getTotalPrice() { return totalPrice; }
        
        public static class Breakdown {
            private String monetaryAmount;
            private String currencyCode;
            
            public String getMonetaryAmount() { return monetaryAmount; }
            public String getCurrencyCode() { return currencyCode; }
        }
    }
    
    // ========================================
    // CLASE: Distance
    // ========================================
    
    public static class Distance {
        private Double value;
        private String unit; // KM, MI
        
        public Double getValue() { return value; }
        public String getUnit() { return unit; }
    }
    
    // ========================================
    // CLASE: TransferTime
    // ========================================
    
    public static class TransferTime {
        private String dateTime; // ISO 8601 format
        
        public String getDateTime() { return dateTime; }
    }
    
    // ========================================
    // CLASE: TransferLocation
    // ========================================
    
    public static class TransferLocation {
        private String locationCode; // IATA code
        private String locationType; // AIRPORT, CITY, etc.
        private Address address;
        private GeoCode geoCode;
        
        public String getLocationCode() { return locationCode; }
        public String getLocationType() { return locationType; }
        public Address getAddress() { return address; }
        public GeoCode getGeoCode() { return geoCode; }
        
        public static class Address {
            private String line;
            private String zip;
            private String countryCode;
            private String cityName;
            
            public String getLine() { return line; }
            public String getZip() { return zip; }
            public String getCountryCode() { return countryCode; }
            public String getCityName() { return cityName; }
        }
        
        public static class GeoCode {
            private Double latitude;
            private Double longitude;
            
            public Double getLatitude() { return latitude; }
            public Double getLongitude() { return longitude; }
        }
    }
    
    // ========================================
    // MÉTODOS AUXILIARES
    // ========================================
    
    public String getFormattedPrice() {
        if (quotation != null && quotation.getTotalPrice() != null) {
            return quotation.getTotalPrice().getCurrencyCode() + " " + 
                   quotation.getTotalPrice().getMonetaryAmount();
        }
        return "N/A";
    }
    
    public String getVehicleDescription() {
        if (vehicle != null && vehicle.getDescription() != null) {
            return String.format("%s - %d asientos", 
                vehicle.getDescription(), 
                vehicle.getSeats() != null ? vehicle.getSeats() : 0);
        }
        return "Vehículo estándar";
    }
    
    public String getDistanceFormatted() {
        if (distance != null && distance.getValue() != null) {
            return String.format("%.2f %s", 
                distance.getValue(), 
                distance.getUnit() != null ? distance.getUnit() : "KM");
        }
        return "N/A";
    }
    
    public boolean hasAirConditioning() {
        return vehicle != null && 
               vehicle.getCharacteristics() != null && 
               Boolean.TRUE.equals(vehicle.getCharacteristics().getAirConditioning());
    }
    
    @Override
    public String toString() {
        return "TransferOffering{" +
                "id='" + id + '\'' +
                ", transferType='" + transferType + '\'' +
                ", vehicle=" + (vehicle != null ? vehicle.getCode() : "null") +
                ", price=" + getFormattedPrice() +
                '}';
    }
}