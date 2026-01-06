package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.entity.Delivery;
import lk.ase.kavinda.islandlink.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@CrossOrigin(origins = "http://localhost:4200")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @GetMapping
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<Delivery> getAllDeliveries() {
        return deliveryService.getAllDeliveries();
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<Delivery> getDeliveriesByStatus(@PathVariable Delivery.DeliveryStatus status) {
        return deliveryService.getDeliveriesByStatus(status);
    }

    @GetMapping("/rdc/{rdcLocation}")
    @PreAuthorize("hasRole('RDC_STAFF') or hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<Delivery> getDeliveriesByRdc(@PathVariable String rdcLocation) {
        return deliveryService.getDeliveriesByRdc(rdcLocation);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('RETAILER') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<Delivery> getDeliveriesByCustomer(@PathVariable Long customerId) {
        return deliveryService.getDeliveriesByCustomer(customerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Delivery> getDeliveryById(@PathVariable Long id) {
        return deliveryService.getDeliveryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Delivery> getDeliveryByOrderId(@PathVariable Long orderId) {
        return deliveryService.getDeliveryByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/schedule")
    @PreAuthorize("hasRole('RDC_STAFF') or hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<Delivery> scheduleDelivery(@RequestBody ScheduleDeliveryRequest request) {
        try {
            Delivery delivery = deliveryService.scheduleDelivery(
                request.getOrderId(),
                request.getDriverName(),
                request.getVehicleNumber(),
                request.getScheduledDate()
            );
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<Delivery> updateDeliveryStatus(
            @PathVariable Long id,
            @RequestBody UpdateDeliveryStatusRequest request) {
        try {
            Delivery delivery = deliveryService.updateDeliveryStatus(
                id,
                request.getStatus(),
                request.getCurrentLocation(),
                request.getNotes()
            );
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/location")
    @PreAuthorize("hasRole('LOGISTICS')")
    public ResponseEntity<Delivery> updateDeliveryLocation(
            @PathVariable Long id,
            @RequestParam String currentLocation) {
        try {
            Delivery delivery = deliveryService.updateDeliveryLocation(id, currentLocation);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DTO classes
    public static class ScheduleDeliveryRequest {
        private Long orderId;
        private String driverName;
        private String vehicleNumber;
        private LocalDateTime scheduledDate;

        // Getters and setters
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getDriverName() { return driverName; }
        public void setDriverName(String driverName) { this.driverName = driverName; }
        public String getVehicleNumber() { return vehicleNumber; }
        public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
        public LocalDateTime getScheduledDate() { return scheduledDate; }
        public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }
    }

    public static class UpdateDeliveryStatusRequest {
        private Delivery.DeliveryStatus status;
        private String currentLocation;
        private String notes;

        // Getters and setters
        public Delivery.DeliveryStatus getStatus() { return status; }
        public void setStatus(Delivery.DeliveryStatus status) { this.status = status; }
        public String getCurrentLocation() { return currentLocation; }
        public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
}