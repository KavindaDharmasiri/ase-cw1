package lk.ase.kavinda.islandlink.dto;

import lk.ase.kavinda.islandlink.entity.Order;
import lk.ase.kavinda.islandlink.entity.OrderItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDTO {
    private Long id;
    private String orderCode;
    private Long customerId;
    private String customerName;
    private String rdcLocation;
    private String deliveryAddress;
    private String customerPhone;
    private String storeName;
    private String status;
    private LocalDateTime orderDate;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime deliveryDate;
    private BigDecimal totalAmount;
    private List<OrderItemDTO> orderItems;
    private String rejectionReason;

    public OrderDTO() {}

    public OrderDTO(Order order) {
        if (order == null) return;
        
        this.id = order.getId();
        this.orderCode = order.getOrderCode();
        
        if (order.getCustomer() != null) {
            this.customerId = order.getCustomer().getId();
            this.customerName = order.getCustomer().getFullName();
        }
        
        this.rdcLocation = order.getRdcLocation();
        this.deliveryAddress = order.getDeliveryAddress();
        this.customerPhone = order.getCustomerPhone();
        this.storeName = order.getStoreName();
        this.status = order.getStatus() != null ? order.getStatus().toString() : "PENDING";
        this.orderDate = order.getOrderDate();
        this.estimatedDeliveryDate = order.getEstimatedDeliveryDate();
        this.deliveryDate = order.getDeliveryDate();
        this.totalAmount = order.getTotalAmount();
        this.rejectionReason = order.getRejectionReason();
        
        if (order.getOrderItems() != null) {
            this.orderItems = order.getOrderItems().stream()
                    .map(OrderItemDTO::new)
                    .collect(Collectors.toList());
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getRdcLocation() { return rdcLocation; }
    public void setRdcLocation(String rdcLocation) { this.rdcLocation = rdcLocation; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public LocalDateTime getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) { this.estimatedDeliveryDate = estimatedDeliveryDate; }

    public LocalDateTime getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDateTime deliveryDate) { this.deliveryDate = deliveryDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public List<OrderItemDTO> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemDTO> orderItems) { this.orderItems = orderItems; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public static class OrderItemDTO {
        private Long id;
        private ProductInfo product;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;

        public OrderItemDTO() {}

        public OrderItemDTO(OrderItem orderItem) {
            if (orderItem == null) return;
            
            this.id = orderItem.getId();
            if (orderItem.getProduct() != null) {
                this.product = new ProductInfo(orderItem.getProduct());
            }
            this.quantity = orderItem.getQuantity();
            this.unitPrice = orderItem.getUnitPrice();
            this.totalPrice = orderItem.getTotalPrice();
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public ProductInfo getProduct() { return product; }
        public void setProduct(ProductInfo product) { this.product = product; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    }

    public static class ProductInfo {
        private Long id;
        private String name;
        private String imageUrl;

        public ProductInfo() {}

        public ProductInfo(lk.ase.kavinda.islandlink.entity.Product product) {
            if (product == null) return;
            
            this.id = product.getId();
            this.name = product.getName();
            this.imageUrl = product.getImageUrl();
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}
