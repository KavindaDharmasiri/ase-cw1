package lk.ase.kavinda.islandlink.dto;

import java.math.BigDecimal;

public class VehicleDTO {
    private String vehicleNumber;
    private String vehicleType;
    private BigDecimal capacity;
    private String status;
    private Long rdcId;

    // Getters and Setters
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public BigDecimal getCapacity() { return capacity; }
    public void setCapacity(BigDecimal capacity) { this.capacity = capacity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getRdcId() { return rdcId; }
    public void setRdcId(Long rdcId) { this.rdcId = rdcId; }
}