package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.entity.StockTransfer;
import lk.ase.kavinda.islandlink.service.StockTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-transfers")
@CrossOrigin(origins = "http://localhost:4200")
public class StockTransferController {

    @Autowired
    private StockTransferService stockTransferService;

    @GetMapping
    @PreAuthorize("hasRole('HEAD_OFFICE_MANAGER')")
    public List<StockTransfer> getAllTransfers() {
        return stockTransferService.getAllTransfers();
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('RDC_STAFF') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<StockTransfer> getTransfersByStatus(@PathVariable StockTransfer.TransferStatus status) {
        return stockTransferService.getTransfersByStatus(status);
    }

    @GetMapping("/from/{fromRdc}")
    @PreAuthorize("hasRole('RDC_STAFF') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<StockTransfer> getTransfersByFromRdc(@PathVariable String fromRdc) {
        return stockTransferService.getTransfersByFromRdc(fromRdc);
    }

    @PostMapping("/request")
    @PreAuthorize("hasRole('RDC_STAFF')")
    public ResponseEntity<StockTransfer> requestTransfer(@RequestBody TransferRequest request) {
        try {
            StockTransfer transfer = stockTransferService.requestTransfer(
                request.getProductId(),
                request.getFromRdc(),
                request.getToRdc(),
                request.getQuantity(),
                request.getReason(),
                request.getRequestedById()
            );
            return ResponseEntity.ok(transfer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<StockTransfer> approveTransfer(@PathVariable Long id, @RequestBody ApprovalRequest request) {
        try {
            StockTransfer transfer = stockTransferService.approveTransfer(id, request.getApprovedById(), request.getNotes());
            return ResponseEntity.ok(transfer);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('RDC_STAFF') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<StockTransfer> completeTransfer(@PathVariable Long id) {
        try {
            StockTransfer transfer = stockTransferService.completeTransfer(id);
            return ResponseEntity.ok(transfer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DTO classes
    public static class TransferRequest {
        private Long productId;
        private String fromRdc;
        private String toRdc;
        private Integer quantity;
        private String reason;
        private Long requestedById;

        // Getters and setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getFromRdc() { return fromRdc; }
        public void setFromRdc(String fromRdc) { this.fromRdc = fromRdc; }
        public String getToRdc() { return toRdc; }
        public void setToRdc(String toRdc) { this.toRdc = toRdc; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public Long getRequestedById() { return requestedById; }
        public void setRequestedById(Long requestedById) { this.requestedById = requestedById; }
    }

    public static class ApprovalRequest {
        private Long approvedById;
        private String notes;

        public Long getApprovedById() { return approvedById; }
        public void setApprovedById(Long approvedById) { this.approvedById = approvedById; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
}