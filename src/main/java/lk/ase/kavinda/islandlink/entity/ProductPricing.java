package lk.ase.kavinda.islandlink.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_pricing")
public class ProductPricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerUnit;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal taxPercentage;

    @Column(precision = 10, scale = 2)
    private BigDecimal promotionalPrice;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(precision = 10, scale = 2)
    private BigDecimal minimumOrderQuantity;

    private LocalDateTime effectiveDate;
    
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    private PricingStatus status = PricingStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum PricingStatus {
        ACTIVE, INACTIVE, EXPIRED
    }

    public ProductPricing() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public BigDecimal getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(BigDecimal pricePerUnit) { this.pricePerUnit = pricePerUnit; }

    public BigDecimal getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(BigDecimal taxPercentage) { this.taxPercentage = taxPercentage; }

    public BigDecimal getPromotionalPrice() { return promotionalPrice; }
    public void setPromotionalPrice(BigDecimal promotionalPrice) { this.promotionalPrice = promotionalPrice; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }

    public BigDecimal getMinimumOrderQuantity() { return minimumOrderQuantity; }
    public void setMinimumOrderQuantity(BigDecimal minimumOrderQuantity) { this.minimumOrderQuantity = minimumOrderQuantity; }

    public LocalDateTime getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDateTime effectiveDate) { this.effectiveDate = effectiveDate; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public PricingStatus getStatus() { return status; }
    public void setStatus(PricingStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getEffectivePrice() {
        LocalDateTime now = LocalDateTime.now();
        
        // Check if promotional price is active
        if (promotionalPrice != null && 
            (effectiveDate == null || now.isAfter(effectiveDate)) &&
            (expiryDate == null || now.isBefore(expiryDate))) {
            return promotionalPrice;
        }
        
        // Apply discount if available
        if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = pricePerUnit.multiply(discountPercentage).divide(new BigDecimal("100"));
            return pricePerUnit.subtract(discount);
        }
        
        return pricePerUnit;
    }
}