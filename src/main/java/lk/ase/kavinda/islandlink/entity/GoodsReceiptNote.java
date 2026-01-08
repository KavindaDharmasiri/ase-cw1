package lk.ase.kavinda.islandlink.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "goods_receipt_notes")
public class GoodsReceiptNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String grnNumber;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    @JoinColumn(name = "rdc_id", nullable = false)
    private RDC rdc;

    @Column(nullable = false)
    private LocalDateTime receivedDate;

    @Column(nullable = false)
    private String receivedBy;

    @OneToMany(mappedBy = "goodsReceiptNote", cascade = CascadeType.ALL)
    private List<GoodsReceiptItem> items;

    @Column(length = 1000)
    private String remarks;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public GoodsReceiptNote() {
        this.createdAt = LocalDateTime.now();
        this.receivedDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGrnNumber() { return grnNumber; }
    public void setGrnNumber(String grnNumber) { this.grnNumber = grnNumber; }

    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(PurchaseOrder purchaseOrder) { this.purchaseOrder = purchaseOrder; }

    public RDC getRdc() { return rdc; }
    public void setRdc(RDC rdc) { this.rdc = rdc; }

    public LocalDateTime getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDateTime receivedDate) { this.receivedDate = receivedDate; }

    public String getReceivedBy() { return receivedBy; }
    public void setReceivedBy(String receivedBy) { this.receivedBy = receivedBy; }

    public List<GoodsReceiptItem> getItems() { return items; }
    public void setItems(List<GoodsReceiptItem> items) { this.items = items; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}