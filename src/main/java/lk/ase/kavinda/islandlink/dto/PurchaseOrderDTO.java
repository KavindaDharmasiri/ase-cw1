package lk.ase.kavinda.islandlink.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PurchaseOrderDTO {
    private Long id;
    private String poNumber;
    private String supplierName;
    private String status;
    private LocalDate expectedDeliveryDate;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;

    public PurchaseOrderDTO() {}

    public PurchaseOrderDTO(Long id, String poNumber, String supplierName, String status, 
                           LocalDate expectedDeliveryDate, LocalDateTime orderDate, BigDecimal totalAmount) {
        this.id = id;
        this.poNumber = poNumber;
        this.supplierName = supplierName;
        this.status = status;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}