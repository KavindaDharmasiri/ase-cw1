package lk.ase.kavinda.islandlink.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pick_lists")
public class PickList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String pickListNumber;

    @ManyToOne
    @JoinColumn(name = "rdc_id", nullable = false)
    private RDC rdc;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PickListStatus status = PickListStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    private LocalDateTime pickedDate;

    private LocalDateTime loadedDate;

    private LocalDateTime dispatchedDate;

    @OneToMany(mappedBy = "pickList", cascade = CascadeType.ALL)
    private List<PickListItem> items;

    @Column(length = 1000)
    private String remarks;

    public enum PickListStatus {
        PENDING, PICKING, PICKED, LOADING, LOADED, DISPATCHED
    }

    public PickList() {
        this.createdDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPickListNumber() { return pickListNumber; }
    public void setPickListNumber(String pickListNumber) { this.pickListNumber = pickListNumber; }

    public RDC getRdc() { return rdc; }
    public void setRdc(RDC rdc) { this.rdc = rdc; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }

    public PickListStatus getStatus() { return status; }
    public void setStatus(PickListStatus status) { this.status = status; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getPickedDate() { return pickedDate; }
    public void setPickedDate(LocalDateTime pickedDate) { this.pickedDate = pickedDate; }

    public LocalDateTime getLoadedDate() { return loadedDate; }
    public void setLoadedDate(LocalDateTime loadedDate) { this.loadedDate = loadedDate; }

    public LocalDateTime getDispatchedDate() { return dispatchedDate; }
    public void setDispatchedDate(LocalDateTime dispatchedDate) { this.dispatchedDate = dispatchedDate; }

    public List<PickListItem> getItems() { return items; }
    public void setItems(List<PickListItem> items) { this.items = items; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}