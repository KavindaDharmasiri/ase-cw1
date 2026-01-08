package lk.ase.kavinda.islandlink.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PurchaseOrderDetailsDTO {
    private Long id;
    private String poNumber;
    private String supplierName;
    private String status;
    private LocalDate expectedDeliveryDate;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private List<PurchaseOrderItemDTO> items;

    public PurchaseOrderDetailsDTO() {}

    public PurchaseOrderDetailsDTO(Long id, String poNumber, String supplierName, String status,
                                 LocalDate expectedDeliveryDate, LocalDateTime orderDate, 
                                 BigDecimal totalAmount, List<PurchaseOrderItemDTO> items) {
        this.id = id;
        this.poNumber = poNumber;
        this.supplierName = supplierName;
        this.status = status;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    // Getters and Setters
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

    public List<PurchaseOrderItemDTO> getItems() { return items; }
    public void setItems(List<PurchaseOrderItemDTO> items) { this.items = items; }

    public static class PurchaseOrderItemDTO {
        private Long id;
        private Long productId;
        private String productName;
        private Integer orderedQuantity;
        private BigDecimal purchasePrice;
        private BigDecimal lineTotal;

        public PurchaseOrderItemDTO() {}

        public PurchaseOrderItemDTO(Long id, Long productId, String productName, 
                                  Integer orderedQuantity, BigDecimal purchasePrice, BigDecimal lineTotal) {
            this.id = id;
            this.productId = productId;
            this.productName = productName;
            this.orderedQuantity = orderedQuantity;
            this.purchasePrice = purchasePrice;
            this.lineTotal = lineTotal;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public Integer getOrderedQuantity() { return orderedQuantity; }
        public void setOrderedQuantity(Integer orderedQuantity) { this.orderedQuantity = orderedQuantity; }

        public BigDecimal getPurchasePrice() { return purchasePrice; }
        public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }

        public BigDecimal getLineTotal() { return lineTotal; }
        public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
    }
}