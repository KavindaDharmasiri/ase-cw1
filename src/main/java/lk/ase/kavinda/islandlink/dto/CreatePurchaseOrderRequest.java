package lk.ase.kavinda.islandlink.dto;

import java.time.LocalDateTime;

public class CreatePurchaseOrderRequest {
    private Long supplierId;
    private Long rdcId;
    private LocalDateTime expectedDeliveryDate;

    public CreatePurchaseOrderRequest() {}

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public Long getRdcId() { return rdcId; }
    public void setRdcId(Long rdcId) { this.rdcId = rdcId; }

    public LocalDateTime getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(LocalDateTime expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }
}