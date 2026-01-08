package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.dto.PurchaseOrderDTO;
import lk.ase.kavinda.islandlink.dto.PurchaseOrderDetailsDTO;
import lk.ase.kavinda.islandlink.dto.GoodsReceiptNoteDTO;
import lk.ase.kavinda.islandlink.dto.GoodsReceiptNoteDetailsDTO;
import lk.ase.kavinda.islandlink.entity.*;
import lk.ase.kavinda.islandlink.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProcurementService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private GoodsReceiptNoteRepository grnRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RDCRepository rdcRepository;

    @Autowired
    private GRNItemRepository grnItemRepository;

    @Transactional
    public PurchaseOrder createPurchaseOrder(Long supplierId, List<PurchaseOrderItemRequest> items, LocalDate expectedDeliveryDate) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        PurchaseOrder po = new PurchaseOrder();
        po.setPoNumber(generatePONumber());
        po.setSupplier(supplier);
        po.setExpectedDeliveryDate(expectedDeliveryDate);
        po.setStatus(PurchaseOrder.POStatus.ISSUED);

        // Save PO first to get ID
        po = purchaseOrderRepository.save(po);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<PurchaseOrderItem> poItems = new ArrayList<>();
        
        for (PurchaseOrderItemRequest itemReq : items) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(po);
            item.setProduct(product);
            item.setOrderedQuantity(itemReq.getQuantity());
            item.setPurchasePrice(itemReq.getPurchasePrice());
            item.setLineTotal(itemReq.getPurchasePrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));

            poItems.add(item);
            totalAmount = totalAmount.add(item.getLineTotal());
        }

        // Set items and total amount
        po.setItems(poItems);
        po.setTotalAmount(totalAmount);
        
        return purchaseOrderRepository.save(po);
    }

    @Transactional
    public GoodsReceiptNote createGRN(Long poId, Long rdcId, List<GRNItemRequest> items, LocalDate deliveryDate, String warehouseLocation) {
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

        RDC rdc = rdcRepository.findById(rdcId)
                .orElseThrow(() -> new RuntimeException("RDC not found"));

        GoodsReceiptNote grn = new GoodsReceiptNote();
        grn.setGrnNumber(generateGRNNumber());
        grn.setPurchaseOrder(po);
        grn.setRdc(rdc);
        grn.setDeliveryReceiveDate(deliveryDate);
        grn.setWarehouseLocation(warehouseLocation);

        // Save GRN first to get ID
        grn = grnRepository.save(grn);

        List<GRNItem> grnItems = new ArrayList<>();
        
        // Process GRN items and update inventory
        for (GRNItemRequest itemReq : items) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            GRNItem grnItem = new GRNItem();
            grnItem.setGrn(grn);
            grnItem.setProduct(product);
            grnItem.setOrderedQuantity(itemReq.getOrderedQuantity());
            grnItem.setDeliveredQuantity(itemReq.getDeliveredQuantity());
            grnItem.setDamagedQuantity(itemReq.getDamagedQuantity());
            grnItem.setBatchNumber(itemReq.getBatchNumber());
            grnItem.setExpiryDate(itemReq.getExpiryDate());
            grnItem.setPackagingInfo(itemReq.getPackagingInfo());

            grnItems.add(grnItem);
            
            // Update inventory
            updateInventory(product, rdc, itemReq);
        }

        // Set items and save again
        grn.setItems(grnItems);
        grn = grnRepository.save(grn);

        // Update PO status
        po.setStatus(PurchaseOrder.POStatus.RECEIVED);
        purchaseOrderRepository.save(po);

        return grn;
    }

    private void updateInventory(Product product, RDC rdc, GRNItemRequest itemReq) {
        Inventory inventory = inventoryRepository.findByProductAndRdc(product, rdc)
                .orElse(new Inventory());

        if (inventory.getId() == null) {
            inventory.setProduct(product);
            inventory.setRdc(rdc);
        }

        // Add good stock to available
        int goodStock = itemReq.getDeliveredQuantity() - itemReq.getDamagedQuantity();
        inventory.setAvailableStock(inventory.getAvailableStock() + goodStock);
        inventory.setDamagedStock(inventory.getDamagedStock() + itemReq.getDamagedQuantity());
        inventory.setBatchNumber(itemReq.getBatchNumber());
        inventory.setExpiryDate(itemReq.getExpiryDate());
        inventory.setLastUpdated(LocalDateTime.now());

        inventoryRepository.save(inventory);
    }

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    public List<PurchaseOrderDTO> getAllPurchaseOrdersDTO() {
        return purchaseOrderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PurchaseOrder getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found with id: " + id));
    }

    public PurchaseOrderDetailsDTO getPurchaseOrderDetailsDTO(Long id) {
        PurchaseOrder po = getPurchaseOrderById(id);
        return convertToDetailsDTO(po);
    }

    private PurchaseOrderDetailsDTO convertToDetailsDTO(PurchaseOrder po) {
        List<PurchaseOrderDetailsDTO.PurchaseOrderItemDTO> itemDTOs = po.getItems() != null ? 
            po.getItems().stream()
                .map(item -> new PurchaseOrderDetailsDTO.PurchaseOrderItemDTO(
                    item.getId(),
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getOrderedQuantity(),
                    item.getPurchasePrice(),
                    item.getLineTotal()
                ))
                .collect(Collectors.toList()) : List.of();

        return new PurchaseOrderDetailsDTO(
            po.getId(),
            po.getPoNumber(),
            po.getSupplier().getName(),
            po.getStatus().toString(),
            po.getExpectedDeliveryDate(),
            po.getCreatedDate(),
            po.getTotalAmount(),
            itemDTOs
        );
    }

    private PurchaseOrderDTO convertToDTO(PurchaseOrder po) {
        return new PurchaseOrderDTO(
                po.getId(),
                po.getPoNumber(),
                po.getSupplier().getName(),
                po.getStatus().toString(),
                po.getExpectedDeliveryDate(),
                po.getCreatedDate(),
                po.getTotalAmount()
        );
    }

    public List<GoodsReceiptNote> getAllGRNs() {
        return grnRepository.findAll();
    }

    public List<GoodsReceiptNoteDTO> getAllGRNsDTO() {
        return grnRepository.findAll().stream()
                .map(grn -> {
                    try {
                        return convertGRNToDTO(grn);
                    } catch (Exception e) {
                        System.err.println("Error converting GRN " + grn.getId() + ": " + e.getMessage());
                        return new GoodsReceiptNoteDTO(
                                grn.getId(),
                                grn.getGrnNumber(),
                                "Error loading PO",
                                "Error loading RDC",
                                grn.getDeliveryReceiveDate(),
                                grn.getWarehouseLocation()
                        );
                    }
                })
                .collect(Collectors.toList());
    }

    public GoodsReceiptNoteDetailsDTO getGRNDetailsDTO(Long id) {
        GoodsReceiptNote grn = grnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GRN not found"));
        return convertGRNToDetailsDTO(grn);
    }

    private GoodsReceiptNoteDetailsDTO convertGRNToDetailsDTO(GoodsReceiptNote grn) {
        List<GoodsReceiptNoteDetailsDTO.GRNItemDTO> itemDTOs = grn.getItems() != null ?
                grn.getItems().stream()
                        .map(item -> new GoodsReceiptNoteDetailsDTO.GRNItemDTO(
                                item.getId(),
                                item.getProduct().getName(),
                                item.getOrderedQuantity(),
                                item.getDeliveredQuantity(),
                                item.getDamagedQuantity(),
                                item.getBatchNumber(),
                                item.getExpiryDate()
                        ))
                        .collect(Collectors.toList()) : List.of();

        return new GoodsReceiptNoteDetailsDTO(
                grn.getId(),
                grn.getGrnNumber(),
                grn.getPurchaseOrder().getPoNumber(),
                grn.getRdc().getName(),
                grn.getDeliveryReceiveDate(),
                grn.getWarehouseLocation(),
                itemDTOs
        );
    }

    private GoodsReceiptNoteDTO convertGRNToDTO(GoodsReceiptNote grn) {
        return new GoodsReceiptNoteDTO(
                grn.getId(),
                grn.getGrnNumber(),
                grn.getPurchaseOrder() != null ? grn.getPurchaseOrder().getPoNumber() : "N/A",
                grn.getRdc() != null ? grn.getRdc().getName() : "N/A",
                grn.getDeliveryReceiveDate(),
                grn.getWarehouseLocation()
        );
    }

    public List<Inventory> getInventoryByRdc(Long rdcId) {
        RDC rdc = rdcRepository.findById(rdcId).orElse(null);
        return rdc != null ? inventoryRepository.findByRdc(rdc) : List.of();
    }

    private String generatePONumber() {
        return "PO" + System.currentTimeMillis();
    }

    private String generateGRNNumber() {
        return "GRN" + System.currentTimeMillis();
    }

    public void updatePurchaseOrderStatus(Long id, PurchaseOrder.POStatus status) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
        po.setStatus(status);
        purchaseOrderRepository.save(po);
    }

    public static class PurchaseOrderItemRequest {
        private Long productId;
        private Integer quantity;
        private BigDecimal purchasePrice;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public BigDecimal getPurchasePrice() { return purchasePrice; }
        public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    }

    public static class GRNItemRequest {
        private Long productId;
        private Integer orderedQuantity;
        private Integer deliveredQuantity;
        private Integer damagedQuantity = 0;
        private String batchNumber;
        private LocalDate expiryDate;
        private String packagingInfo;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
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
}