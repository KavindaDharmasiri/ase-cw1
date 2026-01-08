package lk.ase.kavinda.islandlink.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pick_list_items")
public class PickListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pick_list_id", nullable = false)
    private PickList pickList;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer requiredQuantity;

    @Column(nullable = false)
    private Integer pickedQuantity = 0;

    private String batchNumber;

    @Column(length = 500)
    private String remarks;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PickList getPickList() { return pickList; }
    public void setPickList(PickList pickList) { this.pickList = pickList; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(Integer requiredQuantity) { this.requiredQuantity = requiredQuantity; }

    public Integer getPickedQuantity() { return pickedQuantity; }
    public void setPickedQuantity(Integer pickedQuantity) { this.pickedQuantity = pickedQuantity; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}