package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.Delivery;
import lk.ase.kavinda.islandlink.entity.Order;
import lk.ase.kavinda.islandlink.repository.DeliveryRepository;
import lk.ase.kavinda.islandlink.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    public List<Delivery> getDeliveriesByStatus(Delivery.DeliveryStatus status) {
        return deliveryRepository.findByStatus(status);
    }

    public Optional<Delivery> getDeliveryById(Long id) {
        return deliveryRepository.findById(id);
    }

    public Optional<Delivery> getDeliveryByOrderId(Long orderId) {
        return deliveryRepository.findByOrderId(orderId);
    }

    @Transactional
    public Delivery scheduleDelivery(Long orderId, String driverName, String vehicleNumber, LocalDateTime scheduledDate) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Check if delivery already exists
        Optional<Delivery> existingDelivery = deliveryRepository.findByOrderId(orderId);
        if (existingDelivery.isPresent()) {
            throw new RuntimeException("Delivery already scheduled for this order");
        }

        Delivery delivery = new Delivery(order, driverName, vehicleNumber, scheduledDate);
        delivery = deliveryRepository.save(delivery);

        // Update order status
        orderService.updateOrderStatus(orderId, Order.OrderStatus.OUT_FOR_DELIVERY);

        return delivery;
    }

    @Transactional
    public Delivery updateDeliveryStatus(Long deliveryId, Delivery.DeliveryStatus status, String currentLocation, String notes) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        delivery.setStatus(status);
        delivery.setCurrentLocation(currentLocation);
        delivery.setNotes(notes);

        if (status == Delivery.DeliveryStatus.DELIVERED) {
            delivery.setActualDeliveryDate(LocalDateTime.now());
            // Update order status
            orderService.updateOrderStatus(delivery.getOrder().getId(), Order.OrderStatus.DELIVERED);
        }

        return deliveryRepository.save(delivery);
    }

    public Delivery updateDeliveryLocation(Long deliveryId, String currentLocation) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        delivery.setCurrentLocation(currentLocation);
        return deliveryRepository.save(delivery);
    }

    public List<Delivery> getPendingDeliveries() {
        return deliveryRepository.findByStatusIn(List.of(
            Delivery.DeliveryStatus.SCHEDULED,
            Delivery.DeliveryStatus.IN_TRANSIT
        ));
    }

    public long countPendingDeliveries() {
        return deliveryRepository.countByStatusIn(List.of(
            Delivery.DeliveryStatus.SCHEDULED,
            Delivery.DeliveryStatus.IN_TRANSIT
        ));
    }
}