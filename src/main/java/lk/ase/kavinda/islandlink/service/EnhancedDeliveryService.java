package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.Delivery;
import lk.ase.kavinda.islandlink.entity.Order;
import lk.ase.kavinda.islandlink.repository.DeliveryRepository;
import lk.ase.kavinda.islandlink.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EnhancedDeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public void startDelivery(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        
        // Update delivery status
        delivery.setStatus(Delivery.DeliveryStatus.IN_TRANSIT);
        deliveryRepository.save(delivery);
        
        // Update order status to OUT_FOR_DELIVERY
        Order order = delivery.getOrder();
        order.setStatus(Order.OrderStatus.OUT_FOR_DELIVERY);
        orderRepository.save(order);
    }

    @Transactional
    public void updateLocation(Long deliveryId, String latitude, String longitude) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        
        String location = latitude + "," + longitude;
        delivery.setCurrentLocation(location);
        deliveryRepository.save(delivery);
    }

    @Transactional
    public void completeDelivery(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        
        // Update delivery status
        delivery.setStatus(Delivery.DeliveryStatus.DELIVERED);
        delivery.setActualDeliveryDate(LocalDateTime.now());
        deliveryRepository.save(delivery);
        
        // Update order status
        Order order = delivery.getOrder();
        order.setStatus(Order.OrderStatus.DELIVERED);
        order.setDeliveryDate(LocalDateTime.now());
        orderRepository.save(order);
    }

    public TrackingInfoDTO getTrackingInfo(Long orderId) {
        Optional<Delivery> deliveryOpt = deliveryRepository.findByOrderId(orderId);
        if (deliveryOpt.isEmpty()) {
            return null;
        }
        
        Delivery delivery = deliveryOpt.get();
        TrackingInfoDTO tracking = new TrackingInfoDTO();
        tracking.setOrderId(orderId);
        tracking.setStatus(delivery.getStatus().toString());
        tracking.setDriverName(delivery.getDriverName());
        tracking.setVehicleNumber(delivery.getVehicleNumber());
        tracking.setCurrentLocation(delivery.getCurrentLocation());
        tracking.setScheduledDate(delivery.getScheduledDate());
        tracking.setActualDeliveryDate(delivery.getActualDeliveryDate());
        
        return tracking;
    }

    public static class TrackingInfoDTO {
        private Long orderId;
        private String status;
        private String driverName;
        private String vehicleNumber;
        private String currentLocation;
        private LocalDateTime scheduledDate;
        private LocalDateTime actualDeliveryDate;

        // Getters and setters
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getDriverName() { return driverName; }
        public void setDriverName(String driverName) { this.driverName = driverName; }

        public String getVehicleNumber() { return vehicleNumber; }
        public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

        public String getCurrentLocation() { return currentLocation; }
        public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

        public LocalDateTime getScheduledDate() { return scheduledDate; }
        public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }

        public LocalDateTime getActualDeliveryDate() { return actualDeliveryDate; }
        public void setActualDeliveryDate(LocalDateTime actualDeliveryDate) { this.actualDeliveryDate = actualDeliveryDate; }
    }
}