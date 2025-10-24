package com.travelgo.backend_travelgo.dto;

public class HotelSearchRequest {
    private String cityCode;
    private String checkInDate;
    private String checkOutDate;
    private int adults;
    private int rooms;
    
    // Getters y Setters
    public String getCityCode() { return cityCode; }
    public void setCityCode(String cityCode) { this.cityCode = cityCode; }
    
    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }
    
    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }
    
    public int getAdults() { return adults; }
    public void setAdults(int adults) { this.adults = adults; }
    
    public int getRooms() { return rooms; }
    public void setRooms(int rooms) { this.rooms = rooms; }
}
