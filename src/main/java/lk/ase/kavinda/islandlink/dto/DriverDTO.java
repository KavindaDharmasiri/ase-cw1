package lk.ase.kavinda.islandlink.dto;

public class DriverDTO {
    private String name;
    private String licenseNumber;
    private String phoneNumber;
    private String status;
    private Long rdcId;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getRdcId() { return rdcId; }
    public void setRdcId(Long rdcId) { this.rdcId = rdcId; }
}