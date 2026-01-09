package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.Order;
import lk.ase.kavinda.islandlink.entity.OrderItem;
import lk.ase.kavinda.islandlink.entity.Inventory;
import lk.ase.kavinda.islandlink.repository.OrderRepository;
import lk.ase.kavinda.islandlink.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockAllocationService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Transactional
    public void allocateStockForOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != Order.OrderStatus.CONFIRMED) {
            throw new RuntimeException("Only confirmed orders can have stock allocated");
        }

        // Allocate stock for each item
        for (OrderItem item : order.getOrderItems()) {
            Inventory inventory = inventoryRepository.findByProductIdAndRdcLocation(
                    item.getProduct().getId(), order.getRdcLocation())
                    .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + item.getProduct().getName()));

            if (inventory.getAvailableQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + item.getProduct().getName());
            }

            // Move from Available to Allocated
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - item.getQuantity());
            inventory.setAllocatedQuantity(inventory.getAllocatedQuantity() + item.getQuantity());
            inventoryRepository.save(inventory);
        }

        // Update order status
        order.setStatus(Order.OrderStatus.PICK_LIST_CREATED);
        orderRepository.save(order);
    }

    @Transactional
    public void dispatchOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != Order.OrderStatus.ASSIGNED_TO_ROUTE) {
            throw new RuntimeException("Only route-assigned orders can be dispatched");
        }

        // Update order status to out for delivery
        order.setStatus(Order.OrderStatus.OUT_FOR_DELIVERY);
        orderRepository.save(order);

        // Reduce allocated stock (items are now physically dispatched)
        for (OrderItem item : order.getOrderItems()) {
            Inventory inventory = inventoryRepository.findByProductIdAndRdcLocation(
                    item.getProduct().getId(), order.getRdcLocation())
                    .orElseThrow(() -> new RuntimeException("Inventory not found"));

            inventory.setAllocatedQuantity(inventory.getAllocatedQuantity() - item.getQuantity());
            inventoryRepository.save(inventory);
        }
    }
}