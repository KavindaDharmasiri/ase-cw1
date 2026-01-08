package lk.ase.kavinda.islandlink.dto;

import java.time.LocalDate;
import java.util.List;

public class GoodsReceiptNoteDetailsDTO {
    private Long id;
    private String grnNumber;
    private String poNumber;
    private String rdcName;
    private LocalDate deliveryReceiveDate;
    private String warehouseLocation;
    private List<GRNItemDTO> items;

    public GoodsReceiptNoteDetailsDTO() {}

    public GoodsReceiptNoteDetailsDTO(Long id, String grnNumber, String poNumber, String rdcName, 
                                     LocalDate deliveryReceiveDate, String warehouseLocation, List<GRNItemDTO> items) {
        this.id = id;
        this.grnNumber = grnNumber;
        this.poNumber = poNumber;
        this.rdcName = rdcName;
        this.deliveryReceiveDate = deliveryReceiveDate;
        this.warehouseLocation = warehouseLocation;
        this.items = items;
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
    public List<GRNItemDTO> getItems() { return items; }
    public void setItems(List<GRNItemDTO> items) { this.items = items; }

    public static class GRNItemDTO {
        private Long id;
        private String productName;
        private Integer orderedQuantity;
        private Integer deliveredQuantity;
        private Integer damagedQuantity;
        private String batchNumber;
        private LocalDate expiryDate;

        public GRNItemDTO() {}

        public GRNItemDTO(Long id, String productName, Integer orderedQuantity, Integer deliveredQuantity, 
                         Integer damagedQuantity, String batchNumber, LocalDate expiryDate) {
            this.id = id;
            this.productName = productName;
            this.orderedQuantity = orderedQuantity;
            this.deliveredQuantity = deliveredQuantity;
            this.damagedQuantity = damagedQuantity;
            this.batchNumber = batchNumber;
            this.expiryDate = expiryDate;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
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
    }
}