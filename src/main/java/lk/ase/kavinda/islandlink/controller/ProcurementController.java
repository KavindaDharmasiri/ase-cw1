package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.dto.PurchaseOrderDTO;
import lk.ase.kavinda.islandlink.dto.PurchaseOrderDetailsDTO;
import lk.ase.kavinda.islandlink.dto.GoodsReceiptNoteDTO;
import lk.ase.kavinda.islandlink.dto.GoodsReceiptNoteDetailsDTO;
import lk.ase.kavinda.islandlink.entity.*;
import lk.ase.kavinda.islandlink.service.ProcurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/procurement")
@CrossOrigin(origins = "http://localhost:4200")
public class ProcurementController {

    @Autowired
    private ProcurementService procurementService;

    @PostMapping("/purchase-orders")
    public ResponseEntity<String> createPurchaseOrder(@RequestBody CreatePORequest request) {
        PurchaseOrder po = procurementService.createPurchaseOrder(
                request.getSupplierId(),
                request.getItems(),
                request.getExpectedDeliveryDate()
        );
        return ResponseEntity.ok("done");
    }

    @PostMapping("/grn")
    public ResponseEntity<String> createGRN(@RequestBody CreateGRNRequest request) {
        GoodsReceiptNote grn = procurementService.createGRN(
                request.getPoId(),
                request.getRdcId(),
                request.getItems(),
                request.getDeliveryDate(),
                request.getWarehouseLocation()
        );
        return ResponseEntity.ok("GRN created successfully");
    }

    @GetMapping("/purchase-orders")
    public ResponseEntity<List<PurchaseOrderDTO>> getAllPurchaseOrders() {
        return ResponseEntity.ok(procurementService.getAllPurchaseOrdersDTO());
    }

    @GetMapping("/purchase-orders/{id}")
    public ResponseEntity<PurchaseOrderDetailsDTO> getPurchaseOrderById(@PathVariable Long id) {
        PurchaseOrderDetailsDTO po = procurementService.getPurchaseOrderDetailsDTO(id);
        return ResponseEntity.ok(po);
    }

    @GetMapping("/grn")
    public ResponseEntity<List<GoodsReceiptNoteDTO>> getAllGRNs() {
        return ResponseEntity.ok(procurementService.getAllGRNsDTO());
    }

    @GetMapping("/grn/{id}")
    public ResponseEntity<GoodsReceiptNoteDetailsDTO> getGRNById(@PathVariable Long id) {
        return ResponseEntity.ok(procurementService.getGRNDetailsDTO(id));
    }

    @GetMapping("/inventory/{rdcId}")
    public ResponseEntity<List<Inventory>> getInventoryByRdc(@PathVariable Long rdcId) {
        return ResponseEntity.ok(procurementService.getInventoryByRdc(rdcId));
    }

    @PutMapping("/purchase-orders/{id}/status")
    public ResponseEntity<String> updatePurchaseOrderStatus(@PathVariable Long id, @RequestParam String status) {
        procurementService.updatePurchaseOrderStatus(id, PurchaseOrder.POStatus.valueOf(status));
        return ResponseEntity.ok("Status updated successfully");
    }

    public static class CreatePORequest {
        private Long supplierId;
        private List<ProcurementService.PurchaseOrderItemRequest> items;
        private LocalDate expectedDeliveryDate;

        public Long getSupplierId() { return supplierId; }
        public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
        public List<ProcurementService.PurchaseOrderItemRequest> getItems() { return items; }
        public void setItems(List<ProcurementService.PurchaseOrderItemRequest> items) { this.items = items; }
        public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
        public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }
    }

    public static class CreateGRNRequest {
        private Long poId;
        private Long rdcId;
        private List<ProcurementService.GRNItemRequest> items;
        private LocalDate deliveryDate;
        private String warehouseLocation;

        public Long getPoId() { return poId; }
        public void setPoId(Long poId) { this.poId = poId; }
        public Long getRdcId() { return rdcId; }
        public void setRdcId(Long rdcId) { this.rdcId = rdcId; }
        public List<ProcurementService.GRNItemRequest> getItems() { return items; }
        public void setItems(List<ProcurementService.GRNItemRequest> items) { this.items = items; }
        public LocalDate getDeliveryDate() { return deliveryDate; }
        public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }
        public String getWarehouseLocation() { return warehouseLocation; }
        public void setWarehouseLocation(String warehouseLocation) { this.warehouseLocation = warehouseLocation; }
    }
}
