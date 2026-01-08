package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.*;
import lk.ase.kavinda.islandlink.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class GoodsReceiptService {

    @Autowired
    private GoodsReceiptNoteRepository grnRepository;
    
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    
    @Autowired
    private InventoryService inventoryService;

    public List<GoodsReceiptNote> getAllGoodsReceiptNotes() {
        return grnRepository.findAll();
    }

    public Optional<GoodsReceiptNote> getGoodsReceiptNoteById(Long id) {
        return grnRepository.findById(id);
    }

    @Transactional
    public GoodsReceiptNote createGoodsReceiptNote(GoodsReceiptNote grn) {
        // Generate GRN number
        String grnNumber = generateGrnNumber();
        grn.setGrnNumber(grnNumber);
        
        GoodsReceiptNote savedGrn = grnRepository.save(grn);
        
        // Update inventory for accepted quantities
        if (grn.getItems() != null) {
            for (GRNItem item : grn.getItems()) {
                if (item.getDeliveredQuantity() != null && item.getDeliveredQuantity() > 0) {
                    Integer acceptedQty = item.getDeliveredQuantity() - (item.getDamagedQuantity() != null ? item.getDamagedQuantity() : 0);
                    if (acceptedQty > 0) {
                        inventoryService.addStock(
                            item.getProduct().getId(),
                            grn.getRdc().getId(),
                            acceptedQty
                        );
                    }
                }
            }
        }
        
        // Update purchase order status
        updatePurchaseOrderStatus(grn.getPurchaseOrder());
        
        return savedGrn;
    }

    public List<GoodsReceiptNote> getGrnsByRdc(Long rdcId) {
        RDC rdc = new RDC();
        rdc.setId(rdcId);
        return grnRepository.findByRdc(rdc);
    }

    public List<GoodsReceiptNote> getGrnsByPurchaseOrder(Long poId) {
        return grnRepository.findAll().stream()
                .filter(grn -> grn.getPurchaseOrder().getId().equals(poId))
                .toList();
    }

    private String generateGrnNumber() {
        return "GRN" + System.currentTimeMillis();
    }

    private void updatePurchaseOrderStatus(PurchaseOrder purchaseOrder) {
        // Check if all items are fully received
        boolean fullyReceived = purchaseOrder.getItems().stream()
                .allMatch(item -> item.getReceivedQuantity() >= item.getOrderedQuantity());
        
        boolean partiallyReceived = purchaseOrder.getItems().stream()
                .anyMatch(item -> item.getReceivedQuantity() > 0);
        
        if (fullyReceived) {
            purchaseOrder.setStatus(PurchaseOrder.POStatus.RECEIVED);
        } else if (partiallyReceived) {
            purchaseOrder.setStatus(PurchaseOrder.POStatus.RECEIVED); // Use RECEIVED instead of PARTIALLY_RECEIVED
        }
        
        purchaseOrderRepository.save(purchaseOrder);
    }
}