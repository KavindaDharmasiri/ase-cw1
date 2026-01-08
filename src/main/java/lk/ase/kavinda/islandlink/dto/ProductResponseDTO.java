package lk.ase.kavinda.islandlink.dto;

import lk.ase.kavinda.islandlink.entity.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private String sku;
    private String brand;
    private BigDecimal purchasePrice;
    private BigDecimal price;
    private BigDecimal taxRate;
    private String unit;
    private String imageUrl;
    private Integer minStockLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductResponseDTO() {}

    public ProductResponseDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.category = product.getCategory().getName();
        this.sku = product.getSku();
        this.brand = product.getBrand();
        this.purchasePrice = product.getPurchasePrice();
        this.price = product.getPrice();
        this.taxRate = product.getTaxRate();
        this.unit = product.getUnit();
        this.imageUrl = product.getImageUrl();
        this.minStockLevel = product.getMinStockLevel();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }

    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}