package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.Order;
import lk.ase.kavinda.islandlink.entity.OrderItem;
import lk.ase.kavinda.islandlink.entity.Product;
import lk.ase.kavinda.islandlink.entity.User;
import lk.ase.kavinda.islandlink.repository.OrderRepository;
import lk.ase.kavinda.islandlink.repository.OrderItemRepository;
import lk.ase.kavinda.islandlink.repository.ProductRepository;
import lk.ase.kavinda.islandlink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> getOrdersByRdc(String rdcLocation) {
        return orderRepository.findByRdcLocation(rdcLocation);
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
        order.setDeliveryAddress(deliveryAddress);
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