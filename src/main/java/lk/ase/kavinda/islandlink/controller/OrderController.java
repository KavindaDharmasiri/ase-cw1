package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.entity.Order;
import lk.ase.kavinda.islandlink.entity.OrderItem;
import lk.ase.kavinda.islandlink.entity.Product;
import lk.ase.kavinda.islandlink.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            System.out.println("Creating order for customer: " + request.getCustomerId());
            System.out.println("Items count: " + request.getItems().size());
            
            List<OrderService.CreateOrderItemDTO> items = request.getItems().stream()
                    .map(item -> new OrderService.CreateOrderItemDTO(item.getProductId(), item.getQuantity()))
                    .collect(java.util.stream.Collectors.toList());
                    
            Order order = orderService.createOrder(
                request.getCustomerId(),
                request.getRdcLocation(),
                request.getDeliveryAddress(),
                request.getEstimatedDeliveryDays(),
                request.getCustomerPhone(),
                request.getStoreName(),
                items
            );
            
            String orderCode = order.getOrderCode() != null ? order.getOrderCode() : "ORD-" + order.getId();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Order placed successfully! Order Code: " + orderCode,
                "order", orderCode
            ));
        } catch (RuntimeException e) {
            System.out.println("Error creating order: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to create order: " + e.getMessage()
            ));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('HEAD_OFFICE_MANAGER')")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('RETAILER') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<Order> getOrdersByCustomer(@PathVariable Long customerId) {
        return orderService.getOrdersByCustomer(customerId);
    }

    @GetMapping("/rdc/{rdcLocation}")
    @PreAuthorize("hasRole('RDC_STAFF') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<Order> getOrdersByRdc(@PathVariable String rdcLocation) {
        return orderService.getOrdersByRdc(rdcLocation);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('RDC_STAFF') or hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<Order> getOrdersByStatus(@PathVariable Order.OrderStatus status) {
        return orderService.getOrdersByStatus(status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('RDC_STAFF') or hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestParam Order.OrderStatus status) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/modify")
    @PreAuthorize("hasRole('RETAILER') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<?> modifyOrder(@PathVariable Long id, @RequestBody ModifyOrderRequest request) {
        try {
            List<OrderService.CreateOrderItemDTO> items = request.getItems().stream()
                    .map(item -> new OrderService.CreateOrderItemDTO(item.getProductId(), item.getQuantity()))
                    .collect(java.util.stream.Collectors.toList());
            Order order = orderService.modifyOrder(id, items);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/recent")
    public List<Order> getRecentOrders(@RequestParam(defaultValue = "7") int days) {
        return orderService.getRecentOrders(days);
    }

    @DeleteMapping("/{id}/cancel")
    @PreAuthorize("hasRole('RETAILER') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            orderService.cancelOrder(id);
            return ResponseEntity.ok("Order cancelled successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('RDC_STAFF')")
    public ResponseEntity<?> approveOrder(@PathVariable Long id, @RequestBody ApproveOrderRequest request) {
        try {
            Order order = orderService.approveOrder(id, request.getRdcId(), request.getStaffUserId());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Order approved successfully",
                "order", order
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('RDC_STAFF')")
    public ResponseEntity<?> rejectOrder(@PathVariable Long id, @RequestBody RejectOrderRequest request) {
        try {
            Order order = orderService.rejectOrder(id, request.getRejectionReason(), request.getStaffUserId());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Order rejected successfully",
                "order", order
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // DTO classes
    public static class CreateOrderRequest {
        private Long customerId;
        private String rdcLocation;
        private String deliveryAddress;
        private Integer estimatedDeliveryDays;
        private String customerPhone;
        private String storeName;
        private List<OrderItemRequest> items;

        // Getters and setters
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public String getRdcLocation() { return rdcLocation; }
        public void setRdcLocation(String rdcLocation) { this.rdcLocation = rdcLocation; }
        public String getDeliveryAddress() { return deliveryAddress; }
        public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
        public Integer getEstimatedDeliveryDays() { return estimatedDeliveryDays; }
        public void setEstimatedDeliveryDays(Integer estimatedDeliveryDays) { this.estimatedDeliveryDays = estimatedDeliveryDays; }
        public String getCustomerPhone() { return customerPhone; }
        public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
        public String getStoreName() { return storeName; }
        public void setStoreName(String storeName) { this.storeName = storeName; }
        public List<OrderItemRequest> getItems() { return items; }
        public void setItems(List<OrderItemRequest> items) { this.items = items; }
    }

    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;

        // Getters and setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public static class ModifyOrderRequest {
        private List<OrderItemRequest> items;

        public List<OrderItemRequest> getItems() { return items; }
        public void setItems(List<OrderItemRequest> items) { this.items = items; }
    }

    public static class ApproveOrderRequest {
        private Long rdcId;
        private Long staffUserId;

        public Long getRdcId() { return rdcId; }
        public void setRdcId(Long rdcId) { this.rdcId = rdcId; }
        public Long getStaffUserId() { return staffUserId; }
        public void setStaffUserId(Long staffUserId) { this.staffUserId = staffUserId; }
    }

    public static class RejectOrderRequest {
        private String rejectionReason;
        private Long staffUserId;

        public String getRejectionReason() { return rejectionReason; }
        public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
        public Long getStaffUserId() { return staffUserId; }
        public void setStaffUserId(Long staffUserId) { this.staffUserId = staffUserId; }
    }
}
