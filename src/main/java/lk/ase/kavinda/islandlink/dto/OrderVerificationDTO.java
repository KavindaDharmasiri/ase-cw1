package lk.ase.kavinda.islandlink.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OrderVerificationDTO {
    private Long orderId;
    private String orderNumber;
    private String customerName;
    private BigDecimal totalAmount;
    private String paymentType;
    private BigDecimal creditLimit;
    private BigDecimal availableCredit;
    private List<OrderItemVerificationDTO> items;
    private String verificationStatus;
    private String rejectionReason;

    public OrderVerificationDTO() {}

    // Getters and setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public BigDecimal getCreditLimit() { return creditLimit; }
    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }
    public BigDecimal getAvailableCredit() { return availableCredit; }
    public void setAvailableCredit(BigDecimal availableCredit) { this.availableCredit = availableCredit; }
    public List<OrderItemVerificationDTO> getItems() { return items; }
    public void setItems(List<OrderItemVerificationDTO> items) { this.items = items; }
    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public static class OrderItemVerificationDTO {
        private Long itemId;
        private String productName;
        private Integer requestedQuantity;
        private Integer availableStock;
        private Integer allocatedStock;
        private String batchNumber;
        private LocalDate expiryDate;
        private BigDecimal unitPrice;
        private String availabilityStatus; // AVAILABLE, PARTIAL, UNAVAILABLE
        private Integer suggestedQuantity;
        private String alternateRdc;

        public OrderItemVerificationDTO() {}

        // Getters and setters
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public Integer getRequestedQuantity() { return requestedQuantity; }
        public void setRequestedQuantity(Integer requestedQuantity) { this.requestedQuantity = requestedQuantity; }
        public Integer getAvailableStock() { return availableStock; }
        public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }
        public Integer getAllocatedStock() { return allocatedStock; }
        public void setAllocatedStock(Integer allocatedStock) { this.allocatedStock = allocatedStock; }
        public String getBatchNumber() { return batchNumber; }
        public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
        public LocalDate getExpiryDate() { return expiryDate; }
        public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        public String getAvailabilityStatus() { return availabilityStatus; }
        public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }
        public Integer getSuggestedQuantity() { return suggestedQuantity; }
        public void setSuggestedQuantity(Integer suggestedQuantity) { this.suggestedQuantity = suggestedQuantity; }
        public String getAlternateRdc() { return alternateRdc; }
        public void setAlternateRdc(String alternateRdc) { this.alternateRdc = alternateRdc; }
    }
}