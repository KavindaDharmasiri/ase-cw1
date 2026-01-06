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

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            List<OrderService.CreateOrderItemDTO> items = request.getItems().stream()
                    .map(item -> new OrderService.CreateOrderItemDTO(item.getProductId(), item.getQuantity()))
                    .collect(java.util.stream.Collectors.toList());
                    
            Order order = orderService.createOrder(
                request.getCustomerId(),
                request.getRdcLocation(),
                request.getDeliveryAddress(),
                items
            );
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
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

    // DTO classes
    public static class CreateOrderRequest {
        private Long customerId;
        private String rdcLocation;
        private String deliveryAddress;
        private List<OrderItemRequest> items;

        // Getters and setters
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public String getRdcLocation() { return rdcLocation; }
        public void setRdcLocation(String rdcLocation) { this.rdcLocation = rdcLocation; }
        public String getDeliveryAddress() { return deliveryAddress; }
        public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
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
}