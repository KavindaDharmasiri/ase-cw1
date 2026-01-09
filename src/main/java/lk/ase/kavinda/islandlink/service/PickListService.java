package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.DeliveryRoute;
import lk.ase.kavinda.islandlink.entity.Order;
import lk.ase.kavinda.islandlink.entity.OrderItem;
import lk.ase.kavinda.islandlink.repository.DeliveryRouteRepository;
import lk.ase.kavinda.islandlink.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PickListService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DeliveryRouteRepository deliveryRouteRepository;

    public PickListDTO generatePickListForOrder(Long orderId) {
        // Get the specific order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getStatus() != Order.OrderStatus.CONFIRMED) {
            throw new RuntimeException("Only confirmed orders can have pick lists generated");
        }
        
        // Change order status to PICK_LIST_CREATED (ready for route assignment)
        order.setStatus(Order.OrderStatus.PICK_LIST_CREATED);
        orderRepository.save(order);
        
        PickListDTO pickList = new PickListDTO();
        // NO ROUTE ID - this will be assigned later by Logistics
        pickList.setRouteId(null);
        
        // Convert single order to DTO
        List<OrderSummaryDTO> orderSummaries = List.of(convertToOrderSummary(order));
        pickList.setOrders(orderSummaries);
        
        // Get items for this order only
        Map<String, Integer> consolidatedItems = order.getOrderItems().stream()
                .collect(Collectors.groupingBy(
                    item -> item.getProduct().getName(),
                    Collectors.summingInt(OrderItem::getQuantity)
                ));
        
        pickList.setConsolidatedItems(consolidatedItems);
        return pickList;
    }

    public PickListDTO generatePickListForRoute(Long routeId) {
        // Check if pick list already generated
        DeliveryRoute route = deliveryRouteRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));
        
        if (route.isPickListGenerated()) {
            throw new RuntimeException("Pick list already generated for this route");
        }
        
        // Get confirmed orders for the specific delivery route
        List<Order> orders = orderRepository.findByDeliveryRouteId(routeId)
                .stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.CONFIRMED)
                .collect(Collectors.toList());
        
        if (orders.isEmpty()) {
            throw new RuntimeException("No confirmed orders found for this route");
        }
        
        PickListDTO pickList = new PickListDTO();
        pickList.setRouteId(routeId);
        
        // Convert to DTOs to avoid serialization issues
        List<OrderSummaryDTO> orderSummaries = orders.stream()
                .map(this::convertToOrderSummary)
                .collect(Collectors.toList());
        pickList.setOrders(orderSummaries);
        
        // Group items by product for efficient picking
        Map<String, Integer> consolidatedItems = orders.stream()
                .filter(order -> order.getOrderItems() != null)
                .flatMap(order -> order.getOrderItems().stream())
                .collect(Collectors.groupingBy(
                    item -> item.getProduct().getName(),
                    Collectors.summingInt(OrderItem::getQuantity)
                ));
        
        pickList.setConsolidatedItems(consolidatedItems);
        
        // Mark route as pick list generated and update order statuses
        route.setPickListGenerated(true);
        deliveryRouteRepository.save(route);
        
        orders.forEach(order -> {
            order.setStatus(Order.OrderStatus.PICK_LIST_CREATED);
        });
        orderRepository.saveAll(orders);
        
        return pickList;
    }

    private OrderSummaryDTO convertToOrderSummary(Order order) {
        OrderSummaryDTO dto = new OrderSummaryDTO();
        dto.setId(order.getId());
        dto.setOrderCode(order.getOrderCode());
        dto.setStatus(order.getStatus().toString());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderDate(order.getOrderDate());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setCustomerPhone(order.getCustomerPhone());
        dto.setStoreName(order.getStoreName());
        return dto;
    }

    public static class PickListDTO {
        private Long routeId;
        private List<OrderSummaryDTO> orders;
        private Map<String, Integer> consolidatedItems;

        // Getters and setters
        public Long getRouteId() { return routeId; }
        public void setRouteId(Long routeId) { this.routeId = routeId; }

        public List<OrderSummaryDTO> getOrders() { return orders; }
        public void setOrders(List<OrderSummaryDTO> orders) { this.orders = orders; }

        public Map<String, Integer> getConsolidatedItems() { return consolidatedItems; }
        public void setConsolidatedItems(Map<String, Integer> consolidatedItems) { this.consolidatedItems = consolidatedItems; }
    }

    public static class OrderSummaryDTO {
        private Long id;
        private String orderCode;
        private String status;
        private BigDecimal totalAmount;
        private LocalDateTime orderDate;
        private String deliveryAddress;
        private String customerPhone;
        private String storeName;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getOrderCode() { return orderCode; }
        public void setOrderCode(String orderCode) { this.orderCode = orderCode; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public LocalDateTime getOrderDate() { return orderDate; }
        public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

        public String getDeliveryAddress() { return deliveryAddress; }
        public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

        public String getCustomerPhone() { return customerPhone; }
        public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

        public String getStoreName() { return storeName; }
        public void setStoreName(String storeName) { this.storeName = storeName; }
    }
}