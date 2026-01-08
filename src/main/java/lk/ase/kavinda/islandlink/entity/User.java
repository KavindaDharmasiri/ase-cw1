package lk.ase.kavinda.islandlink.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    private String address;

    private String phone;

    // Customer-specific fields
    private String businessName;
    
    private String district;
    
    @ManyToOne
    @JoinColumn(name = "servicing_rdc_id")
    private RDC servicingRdc;
    
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType = PaymentType.CASH;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal creditLimit = BigDecimal.ZERO;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal outstandingBalance = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    private BusinessType businessType;
    
    private String deliveryAddress;
    
    private String contactPerson;
    
    private String gpsCoordinates;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;
    
    private String department; // For RDC/Logistics users

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public User() {}

    public User(String username, String email, String password, String fullName, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public RDC getServicingRdc() { return servicingRdc; }
    public void setServicingRdc(RDC servicingRdc) { this.servicingRdc = servicingRdc; }

    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }

    public BigDecimal getCreditLimit() { return creditLimit; }
    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }

    public BigDecimal getOutstandingBalance() { return outstandingBalance; }
    public void setOutstandingBalance(BigDecimal outstandingBalance) { this.outstandingBalance = outstandingBalance; }
    
    public BusinessType getBusinessType() { return businessType; }
    public void setBusinessType(BusinessType businessType) { this.businessType = businessType; }
    
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    
    public String getGpsCoordinates() { return gpsCoordinates; }
    public void setGpsCoordinates(String gpsCoordinates) { this.gpsCoordinates = gpsCoordinates; }
    
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public enum PaymentType {
        CASH, CREDIT
    }
    
    public enum BusinessType {
        RETAIL, SUPERMARKET, RESELLER
    }
    
    public enum UserStatus {
        ACTIVE, INACTIVE
    }
}