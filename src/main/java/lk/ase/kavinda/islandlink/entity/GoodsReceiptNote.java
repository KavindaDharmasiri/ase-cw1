package lk.ase.kavinda.islandlink.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "goods_receipt_notes")
public class GoodsReceiptNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String grnNumber;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    @JoinColumn(name = "rdc_id", nullable = false)
    private RDC rdc;

    private LocalDate deliveryReceiveDate;
    private LocalDateTime grnDate = LocalDateTime.now();
    private String warehouseLocation;

    @OneToMany(mappedBy = "grn", cascade = CascadeType.ALL)
    private List<GRNItem> items;

    public GoodsReceiptNote() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGrnNumber() { return grnNumber; }
    public void setGrnNumber(String grnNumber) { this.grnNumber = grnNumber; }

    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(PurchaseOrder purchaseOrder) { this.purchaseOrder = purchaseOrder; }

    public RDC getRdc() { return rdc; }
    public void setRdc(RDC rdc) { this.rdc = rdc; }

    public LocalDate getDeliveryReceiveDate() { return deliveryReceiveDate; }
    public void setDeliveryReceiveDate(LocalDate deliveryReceiveDate) { this.deliveryReceiveDate = deliveryReceiveDate; }

    public LocalDateTime getGrnDate() { return grnDate; }
    public void setGrnDate(LocalDateTime grnDate) { this.grnDate = grnDate; }

    public String getWarehouseLocation() { return warehouseLocation; }
    public void setWarehouseLocation(String warehouseLocation) { this.warehouseLocation = warehouseLocation; }

    public List<GRNItem> getItems() { return items; }
    public void setItems(List<GRNItem> items) { this.items = items; }
}