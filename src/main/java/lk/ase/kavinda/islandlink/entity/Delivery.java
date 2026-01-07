package lk.ase.kavinda.islandlink.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "delivery_route_id")
    private DeliveryRoute deliveryRoute;

    @Column(nullable = false)
    private String driverName;

    @Column(nullable = false)
    private String vehicleNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Column(nullable = false)
    private LocalDateTime scheduledDate;

    private LocalDateTime actualDeliveryDate;

    @Column(length = 500)
    private String notes;

    @Column(length = 200)
    private String currentLocation;

    public enum DeliveryStatus {
        SCHEDULED, IN_TRANSIT, DELIVERED, FAILED, CANCELLED
    }

    // Constructors
    public Delivery() {
        this.status = DeliveryStatus.SCHEDULED;
    }

    public Delivery(Order order, String driverName, String vehicleNumber, LocalDateTime scheduledDate) {
        this();
        this.order = order;
        this.driverName = driverName;
        this.vehicleNumber = vehicleNumber;
        this.scheduledDate = scheduledDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public DeliveryRoute getDeliveryRoute() { return deliveryRoute; }
    public void setDeliveryRoute(DeliveryRoute deliveryRoute) { this.deliveryRoute = deliveryRoute; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public DeliveryStatus getStatus() { return status; }
    public void setStatus(DeliveryStatus status) { this.status = status; }

    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }

    public LocalDateTime getActualDeliveryDate() { return actualDeliveryDate; }
    public void setActualDeliveryDate(LocalDateTime actualDeliveryDate) { this.actualDeliveryDate = actualDeliveryDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
}