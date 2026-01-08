package lk.ase.kavinda.islandlink.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "goods_receipt_items")
public class GoodsReceiptItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "grn_id", nullable = false)
    private GoodsReceiptNote goodsReceiptNote;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer orderedQuantity;

    @Column(nullable = false)
    private Integer receivedQuantity;

    @Column(nullable = false)
    private Integer damagedQuantity = 0;

    @Column(nullable = false)
    private Integer acceptedQuantity;

    private String batchNumber;

    private LocalDate expiryDate;

    @Column(length = 500)
    private String remarks;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public GoodsReceiptNote getGoodsReceiptNote() { return goodsReceiptNote; }
    public void setGoodsReceiptNote(GoodsReceiptNote goodsReceiptNote) { this.goodsReceiptNote = goodsReceiptNote; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getOrderedQuantity() { return orderedQuantity; }
    public void setOrderedQuantity(Integer orderedQuantity) { this.orderedQuantity = orderedQuantity; }

    public Integer getReceivedQuantity() { return receivedQuantity; }
    public void setReceivedQuantity(Integer receivedQuantity) { this.receivedQuantity = receivedQuantity; }

    public Integer getDamagedQuantity() { return damagedQuantity; }
    public void setDamagedQuantity(Integer damagedQuantity) { this.damagedQuantity = damagedQuantity; }

    public Integer getAcceptedQuantity() { return acceptedQuantity; }
    public void setAcceptedQuantity(Integer acceptedQuantity) { this.acceptedQuantity = acceptedQuantity; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}