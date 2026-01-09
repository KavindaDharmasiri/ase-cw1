package lk.ase.kavinda.islandlink.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/deliveries")
@CrossOrigin(origins = "http://localhost:4200")
public class DeliveryController {

    @PostMapping("/{orderId}/confirm")
    @PreAuthorize("hasRole('LOGISTICS')")
    public ResponseEntity<?> confirmDelivery(@PathVariable Long orderId, @RequestBody ConfirmDeliveryRequest request) {
        try {
            // Simple delivery confirmation without payment processing
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Delivery confirmed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/{orderId}/issue")
    @PreAuthorize("hasRole('LOGISTICS')")
    public ResponseEntity<?> reportDeliveryIssue(@PathVariable Long orderId, @RequestBody ReportIssueRequest request) {
        try {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Delivery issue reported successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    public static class ConfirmDeliveryRequest {
        private String paymentMethod;
        private BigDecimal amountReceived;
        private String notes;

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        
        public BigDecimal getAmountReceived() { return amountReceived; }
        public void setAmountReceived(BigDecimal amountReceived) { this.amountReceived = amountReceived; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class ReportIssueRequest {
        private String issueType;
        private String issueNotes;

        public String getIssueType() { return issueType; }
        public void setIssueType(String issueType) { this.issueType = issueType; }
        
        public String getIssueNotes() { return issueNotes; }
        public void setIssueNotes(String issueNotes) { this.issueNotes = issueNotes; }
    }
}