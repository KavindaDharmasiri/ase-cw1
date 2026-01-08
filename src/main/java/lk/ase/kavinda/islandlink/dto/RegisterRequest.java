package lk.ase.kavinda.islandlink.dto;

import lk.ase.kavinda.islandlink.entity.Role;
import java.math.BigDecimal;

public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String role;
    
    // Customer-specific fields
    private String businessName;
    private String district;
    private Long servicingRdcId;
    private String paymentType;
    private BigDecimal creditLimit;
    private String businessType;
    private String deliveryAddress;
    private String contactPerson;
    private String phone;
    private String gpsCoordinates;
    private String department;
    private String status;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    
    public Long getServicingRdcId() { return servicingRdcId; }
    public void setServicingRdcId(Long servicingRdcId) { this.servicingRdcId = servicingRdcId; }
    
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    
    public BigDecimal getCreditLimit() { return creditLimit; }
    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }
    
    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getGpsCoordinates() { return gpsCoordinates; }
    public void setGpsCoordinates(String gpsCoordinates) { this.gpsCoordinates = gpsCoordinates; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}