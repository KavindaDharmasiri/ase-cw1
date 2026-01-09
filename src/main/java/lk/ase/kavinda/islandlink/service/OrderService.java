package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.dto.OrderVerificationDTO;
import lk.ase.kavinda.islandlink.dto.OrderVerificationRequest;
import lk.ase.kavinda.islandlink.entity.Order;
import lk.ase.kavinda.islandlink.entity.OrderItem;
import lk.ase.kavinda.islandlink.entity.Product;
import lk.ase.kavinda.islandlink.entity.User;
import lk.ase.kavinda.islandlink.entity.Inventory;
import lk.ase.kavinda.islandlink.repository.OrderRepository;
import lk.ase.kavinda.islandlink.repository.OrderItemRepository;
import lk.ase.kavinda.islandlink.repository.ProductRepository;
import lk.ase.kavinda.islandlink.repository.UserRepository;
import lk.ase.kavinda.islandlink.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FinancialService financialService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private InventoryRepository inventoryRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> getOrdersByRdc(String rdcLocation) {
        return orderRepository.findByRdcLocation(rdcLocation);
    }

    public List<Order> getOrdersByRdcId(Long rdcId) {
        return orderRepository.findByRdcId(rdcId);
    }

    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public Order createOrder(Long customerId, String rdcLocation, String deliveryAddress, Integer estimatedDeliveryDays, String customerPhone, String storeName, List<CreateOrderItemDTO> items) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Use customer's assigned RDC if not specified
        String assignedRdcLocation = rdcLocation;
        if (customer.getServicingRdc() != null) {
            assignedRdcLocation = customer.getServicingRdc().getName();
        }

        // Validate stock availability before creating order
        for (CreateOrderItemDTO item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));
            
            // Check if sufficient stock exists (assuming RDC ID 1 for now)
            Long rdcId = customer.getServicingRdc() != null ? customer.getServicingRdc().getId() : 1L;
            Integer availableStock = inventoryService.getInventoryByProductAndRdc(product.getId(), rdcId)
                    .map(inventory -> inventory.getAvailableStock())
                    .orElse(0);
            
            if (availableStock < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName() + 
                                         ". Available: " + availableStock + ", Requested: " + item.getQuantity());
            }
        }

        // Validate credit limit for credit customers
        BigDecimal orderTotal = calculateOrderTotal(items);
        if (customer.getPaymentType() == User.PaymentType.CREDIT) {
            BigDecimal availableCredit = customer.getCreditLimit().subtract(customer.getOutstandingBalance());
            if (orderTotal.compareTo(availableCredit) > 0) {
                throw new RuntimeException("Order amount exceeds available credit limit");
            }
        }

        Order order = new Order();
        order.setOrderCode(generateOrderCode());
        order.setCustomer(customer);
        order.setRdcLocation(assignedRdcLocation);
        order.setDeliveryAddress(deliveryAddress != null && !deliveryAddress.trim().isEmpty() && !"No address on file".equals(deliveryAddress) 
                ? deliveryAddress 
                : customer.getAddress() != null ? customer.getAddress() : "123 Main Street, Colombo 01, Sri Lanka");
        order.setCustomerPhone(customerPhone != null ? customerPhone : customer.getPhone());
        order.setStoreName(storeName != null ? storeName : customer.getFullName());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        int deliveryDays = estimatedDeliveryDays != null ? estimatedDeliveryDays : 5;
        order.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(deliveryDays));
        order.setTotalAmount(orderTotal);

        order = orderRepository.save(order);

        // Create order items
        for (CreateOrderItemDTO itemDTO : items) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemDTO.getProductId()));
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            
            orderItemRepository.save(orderItem);
        }

        // Reload order with items for financial service
        order = orderRepository.findById(order.getId()).orElse(order);

        // Update customer outstanding balance for credit customers
        if (customer.getPaymentType() == User.PaymentType.CREDIT) {
            customer.setOutstandingBalance(customer.getOutstandingBalance().add(orderTotal));
            userRepository.save(customer);
        }

        // Record financial transaction
        financialService.recordSaleTransaction(order);

        return order;
    }

    public Order updateOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(status);
        if (status == Order.OrderStatus.DELIVERED) {
            order.setDeliveryDate(LocalDateTime.now());
        }
        
        return orderRepository.save(order);
    }

    @Transactional
    public Order modifyOrder(Long orderId, List<CreateOrderItemDTO> newItems) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Cannot modify order that is already being processed");
        }
        
        // Remove existing order items
        orderItemRepository.deleteByOrderId(orderId);
        
        // Add new items
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CreateOrderItemDTO itemDTO : newItems) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemDTO.getProductId()));
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            
            orderItemRepository.save(orderItem);
            totalAmount = totalAmount.add(orderItem.getTotalPrice());
        }
        
        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel delivered order");
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public List<Order> getRecentOrders(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return orderRepository.findByOrderDateAfter(cutoffDate);
    }

    public long countRecentOrders(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return orderRepository.countByOrderDateAfter(cutoffDate);
    }

    private String generateOrderCode() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        long orderCount = orderRepository.count() + 1;
        return String.format("ORD-%s-%04d", year, orderCount);
    }

    private BigDecimal calculateOrderTotal(List<CreateOrderItemDTO> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderItemDTO item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }

    @Transactional
    public Order approveOrder(Long orderId, Long rdcId, Long staffUserId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Order is not in pending status");
        }
        
        // Check stock availability and allocate
        for (OrderItem item : order.getOrderItems()) {
            boolean allocated = inventoryService.allocateStock(
                item.getProduct().getId(), 
                rdcId, 
                item.getQuantity(),
                orderId,
                staffUserId
            );
            
            if (!allocated) {
                throw new RuntimeException("Insufficient stock for product: " + item.getProduct().getName());
            }
        }
        
        // Update order status
        order.setStatus(Order.OrderStatus.CONFIRMED);
        order = orderRepository.save(order);
        
        // Notify customer
        notificationService.notifyOrderApproved(order);
        
        return order;
    }

    @Transactional
    public Order rejectOrder(Long orderId, String rejectionReason, Long staffUserId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        // Could add rejection reason field to Order entity
        order = orderRepository.save(order);
        
        // Notify customer
        notificationService.notifyOrderRejected(order, rejectionReason);
        
        return order;
    }

    public List<OrderVerificationDTO> getPendingOrdersForVerification(Long rdcId) {
        return orderRepository.findByStatusAndRdcId(Order.OrderStatus.PENDING,rdcId)
                .stream()
                .map(this::convertToVerificationDTO)
                .collect(Collectors.toList());
    }

    public String processOrderVerification(OrderVerificationRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        switch (request.getDecision()) {
            case "APPROVE":
                return approveOrderFully(order);
            case "PARTIAL_APPROVE":
                return approveOrderPartially(order, request.getItemAdjustments());
            case "REJECT":
                return rejectOrderWithReason(order, request.getRejectionReason());
            default:
                throw new RuntimeException("Invalid decision: " + request.getDecision());
        }
    }

    public OrderVerificationDTO getOrderVerificationDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToVerificationDTO(order);
    }

    private OrderVerificationDTO convertToVerificationDTO(Order order) {
        OrderVerificationDTO dto = new OrderVerificationDTO();
        dto.setOrderId(order.getId());
        dto.setOrderNumber(order.getOrderCode());
        dto.setCustomerName(order.getCustomer().getFullName());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPaymentType(order.getCustomer().getPaymentType().toString());
        dto.setCreditLimit(order.getCustomer().getCreditLimit());
        dto.setAvailableCredit(calculateAvailableCredit(order.getCustomer()));
        
        List<OrderVerificationDTO.OrderItemVerificationDTO> itemDTOs = order.getOrderItems().stream()
                .map(this::convertToItemVerificationDTO)
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);
        
        return dto;
    }

    private String approveOrderFully(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            allocateStock(item);
        }
        order.setStatus(Order.OrderStatus.CONFIRMED);
        orderRepository.save(order);
        return "Order approved and stock allocated";
    }

    private String approveOrderPartially(Order order, List<OrderVerificationRequest.ItemAdjustment> adjustments) {
        for (OrderVerificationRequest.ItemAdjustment adj : adjustments) {
            OrderItem item = order.getOrderItems().stream()
                    .filter(i -> i.getId().equals(adj.getItemId()))
                    .findFirst().orElse(null);
            if (item != null) {
                item.setQuantity(adj.getAdjustedQuantity());
                item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(adj.getAdjustedQuantity())));
                allocateStock(item);
            }
        }
        
        BigDecimal newTotal = order.getOrderItems().stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(newTotal);
        order.setStatus(Order.OrderStatus.CONFIRMED);
        orderRepository.save(order);
        
        return "Order partially approved with quantity adjustments";
    }

    private String rejectOrderWithReason(Order order, String reason) {
        order.setStatus(Order.OrderStatus.REJECTED);
        order.setRejectionReason(reason);
        orderRepository.save(order);
        return "Order rejected: " + reason;
    }

    private void allocateStock(OrderItem item) {
        // Get RDC ID from customer's servicing RDC
        Long rdcId = item.getOrder().getCustomer().getServicingRdc() != null ? 
                    item.getOrder().getCustomer().getServicingRdc().getId() : 1L;
        
        // Allocate stock using inventory service
        boolean allocated = inventoryService.allocateStock(
            item.getProduct().getId(), 
            rdcId, 
            item.getQuantity(),
            item.getOrder().getId(),
            1L // Default staff user ID
        );
        
        if (!allocated) {
            throw new RuntimeException("Failed to allocate stock for product: " + item.getProduct().getName());
        }
    }

    private BigDecimal calculateAvailableCredit(User customer) {
        return orderRepository.findByCustomerAndStatus(customer, Order.OrderStatus.CONFIRMED)
                .stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .subtract(customer.getCreditLimit());
    }

    private OrderVerificationDTO.OrderItemVerificationDTO convertToItemVerificationDTO(OrderItem item) {
        OrderVerificationDTO.OrderItemVerificationDTO dto = new OrderVerificationDTO.OrderItemVerificationDTO();
        dto.setItemId(item.getId());
        dto.setProductName(item.getProduct().getName());
        dto.setRequestedQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        
        // Get actual stock from inventory
        Long rdcId = item.getOrder().getCustomer().getServicingRdc() != null ? 
                    item.getOrder().getCustomer().getServicingRdc().getId() : 1L;
        Integer actualStock = inventoryService.getInventoryByProductAndRdc(item.getProduct().getId(), rdcId)
                .map(inventory -> inventory.getAvailableStock())
                .orElse(0);
        
        dto.setAvailableStock(actualStock);
        dto.setAvailabilityStatus(actualStock >= item.getQuantity() ? "AVAILABLE" : "UNAVAILABLE");
        dto.setSuggestedQuantity(Math.min(actualStock, item.getQuantity()));
        
        return dto;
    }

    // DTO class for order creation
    public static class CreateOrderItemDTO {
        private Long productId;
        private Integer quantity;

        public CreateOrderItemDTO() {}

        public CreateOrderItemDTO(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
