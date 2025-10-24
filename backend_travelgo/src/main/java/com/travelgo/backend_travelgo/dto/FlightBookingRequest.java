

import java.util.List;
import java.util.List;
public class FlightBookingRequest {
    
    private String flightOfferId;
    private String flightOfferJson; // JSON completo del flight offer para enviar a Amadeus
    private List<TravelerInfo> travelers;
    private ContactInfo contact;
    private PaymentInfo payment;
    
    public static class TravelerInfo {
        private String firstName;
        private String lastName;
        private String dateOfBirth;
        private String gender; // MALE, FEMALE
        private String email;
        private String phone;
        private String documentType; // PASSPORT, IDENTITY_CARD
        private String documentNumber;
        private String documentExpiry;
        private String nationality;
        
        // Getters y Setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getDocumentType() { return documentType; }
        public void setDocumentType(String documentType) { this.documentType = documentType; }
        
        public String getDocumentNumber() { return documentNumber; }
        public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
        
        public String getDocumentExpiry() { return documentExpiry; }
        public void setDocumentExpiry(String documentExpiry) { this.documentExpiry = documentExpiry; }
        
        public String getNationality() { return nationality; }
        public void setNationality(String nationality) { this.nationality = nationality; }
    }
    
    public static class ContactInfo {
        private String email;
        private String phone;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
    
    public static class PaymentInfo {
        private String method; // CARD, ACCOUNT
        private String cardNumber;
        private String cardHolder;
        private String expiryDate;
        private String cvv;
        
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        
        public String getCardNumber() { return cardNumber; }
        public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
        
        public String getCardHolder() { return cardHolder; }
        public void setCardHolder(String cardHolder) { this.cardHolder = cardHolder; }
        
        public String getExpiryDate() { return expiryDate; }
        public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
        
        public String getCvv() { return cvv; }
        public void setCvv(String cvv) { this.cvv = cvv; }
    }
    
    // Getters y Setters principales
    public String getFlightOfferId() { return flightOfferId; }
    public void setFlightOfferId(String flightOfferId) { this.flightOfferId = flightOfferId; }
    
    public String getFlightOfferJson() { return flightOfferJson; }
    public void setFlightOfferJson(String flightOfferJson) { this.flightOfferJson = flightOfferJson; }
    
    public List<TravelerInfo> getTravelers() { return travelers; }
    public void setTravelers(List<TravelerInfo> travelers) { this.travelers = travelers; }
    
    public ContactInfo getContact() { return contact; }
    public void setContact(ContactInfo contact) { this.contact = contact; }
    
    public PaymentInfo getPayment() { return payment; }
    public void setPayment(PaymentInfo payment) { this.payment = payment; }
}
