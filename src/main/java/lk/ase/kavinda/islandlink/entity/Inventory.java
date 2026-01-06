package lk.ase.kavinda.islandlink.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;

    @Column(nullable = false)
    private String rdcLocation;

    @Column(nullable = false)
    private Integer currentStock;

    @Column(nullable = false)
    private Integer reservedStock;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    public Inventory() {
        this.lastUpdated = LocalDateTime.now();
    }

    public Inventory(Product product, String rdcLocation, Integer currentStock) {
        this();
        this.product = product;
        this.rdcLocation = rdcLocation;
        this.currentStock = currentStock;
        this.reservedStock = 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getRdcLocation() { return rdcLocation; }
    public void setRdcLocation(String rdcLocation) { this.rdcLocation = rdcLocation; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

    public Integer getReservedStock() { return reservedStock; }
    public void setReservedStock(Integer reservedStock) { this.reservedStock = reservedStock; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public Integer getAvailableStock() {
        return currentStock - reservedStock;
    }

    public boolean isLowStock() {
        return currentStock <= product.getMinStockLevel();
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}