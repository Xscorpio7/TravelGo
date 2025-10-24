package com.travelgo.backend_travelgo.dto;
public class TransferSearchRequest {
    private String startLocationCode;
    private String endLocationCode;
    private String transferDate;
    private String transferTime;
    private int passengers;
    
    // Getters y Setters
    public String getStartLocationCode() { return startLocationCode; }
    public void setStartLocationCode(String startLocationCode) { this.startLocationCode = startLocationCode; }
    
    public String getEndLocationCode() { return endLocationCode; }
    public void setEndLocationCode(String endLocationCode) { this.endLocationCode = endLocationCode; }
    
    public String getTransferDate() { return transferDate; }
    public void setTransferDate(String transferDate) { this.transferDate = transferDate; }
    
    public String getTransferTime() { return transferTime; }
    public void setTransferTime(String transferTime) { this.transferTime = transferTime; }
    
    public int getPassengers() { return passengers; }
    public void setPassengers(int passengers) { this.passengers = passengers; }
}
