package lk.ase.kavinda.islandlink.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "rdc_id", nullable = false)
    private RDC rdc;

    private Integer availableStock = 0;
    private Integer allocatedStock = 0;
    private Integer inTransitStock = 0;
    private Integer soldStock = 0;
    private Integer damagedStock = 0;
    private Integer expiredStock = 0;

    private String batchNumber;
    private LocalDate expiryDate;
    private String warehouseLocation;
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private InventoryStatus status = InventoryStatus.AVAILABLE;

    public Inventory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public RDC getRdc() { return rdc; }
    public void setRdc(RDC rdc) { this.rdc = rdc; }

    public Integer getAvailableStock() { return availableStock; }
    public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }

    public Integer getAllocatedStock() { return allocatedStock; }
    public void setAllocatedStock(Integer allocatedStock) { this.allocatedStock = allocatedStock; }

    public Integer getInTransitStock() { return inTransitStock; }
    public void setInTransitStock(Integer inTransitStock) { this.inTransitStock = inTransitStock; }

    public Integer getSoldStock() { return soldStock; }
    public void setSoldStock(Integer soldStock) { this.soldStock = soldStock; }

    public Integer getDamagedStock() { return damagedStock; }
    public void setDamagedStock(Integer damagedStock) { this.damagedStock = damagedStock; }

    public Integer getExpiredStock() { return expiredStock; }
    public void setExpiredStock(Integer expiredStock) { this.expiredStock = expiredStock; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getWarehouseLocation() { return warehouseLocation; }
    public void setWarehouseLocation(String warehouseLocation) { this.warehouseLocation = warehouseLocation; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public InventoryStatus getStatus() { return status; }
    public void setStatus(InventoryStatus status) { this.status = status; }

    public enum InventoryStatus {
        AVAILABLE, ALLOCATED, IN_TRANSIT, SOLD, DAMAGED, EXPIRED
    }
}