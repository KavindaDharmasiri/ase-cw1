package lk.ase.kavinda.islandlink.dto;

import java.time.LocalDate;

public class GoodsReceiptNoteDTO {
    private Long id;
    private String grnNumber;
    private String poNumber;
    private String rdcName;
    private LocalDate deliveryReceiveDate;
    private String warehouseLocation;

    public GoodsReceiptNoteDTO() {}

    public GoodsReceiptNoteDTO(Long id, String grnNumber, String poNumber, String rdcName, 
                              LocalDate deliveryReceiveDate, String warehouseLocation) {
        this.id = id;
        this.grnNumber = grnNumber;
        this.poNumber = poNumber;
        this.rdcName = rdcName;
        this.deliveryReceiveDate = deliveryReceiveDate;
        this.warehouseLocation = warehouseLocation;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getGrnNumber() { return grnNumber; }
    public void setGrnNumber(String grnNumber) { this.grnNumber = grnNumber; }
    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }
    public String getRdcName() { return rdcName; }
    public void setRdcName(String rdcName) { this.rdcName = rdcName; }
    public LocalDate getDeliveryReceiveDate() { return deliveryReceiveDate; }
    public void setDeliveryReceiveDate(LocalDate deliveryReceiveDate) { this.deliveryReceiveDate = deliveryReceiveDate; }
    public String getWarehouseLocation() { return warehouseLocation; }
    public void setWarehouseLocation(String warehouseLocation) { this.warehouseLocation = warehouseLocation; }
}