package lk.ase.kavinda.islandlink.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "grn_items")
public class GRNItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "grn_id", nullable = false)
    private GoodsReceiptNote grn;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer orderedQuantity;
    private Integer deliveredQuantity;
    private Integer damagedQuantity = 0;
    private String batchNumber;
    private LocalDate expiryDate;
    private String packagingInfo;

    public GRNItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public GoodsReceiptNote getGrn() { return grn; }
    public void setGrn(GoodsReceiptNote grn) { this.grn = grn; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getOrderedQuantity() { return orderedQuantity; }
    public void setOrderedQuantity(Integer orderedQuantity) { this.orderedQuantity = orderedQuantity; }

    public Integer getDeliveredQuantity() { return deliveredQuantity; }
    public void setDeliveredQuantity(Integer deliveredQuantity) { this.deliveredQuantity = deliveredQuantity; }

    public Integer getDamagedQuantity() { return damagedQuantity; }
    public void setDamagedQuantity(Integer damagedQuantity) { this.damagedQuantity = damagedQuantity; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getPackagingInfo() { return packagingInfo; }
    public void setPackagingInfo(String packagingInfo) { this.packagingInfo = packagingInfo; }
}