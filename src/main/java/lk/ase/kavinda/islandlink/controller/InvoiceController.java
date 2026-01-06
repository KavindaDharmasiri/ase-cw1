package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.entity.Invoice;
import lk.ase.kavinda.islandlink.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:4200")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    @PreAuthorize("hasRole('HEAD_OFFICE_MANAGER')")
    public List<Invoice> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Invoice> getInvoiceByOrderId(@PathVariable Long orderId) {
        return invoiceService.getInvoiceByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/generate/{orderId}")
    @PreAuthorize("hasRole('RDC_STAFF') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<Invoice> generateInvoice(@PathVariable Long orderId) {
        try {
            Invoice invoice = invoiceService.generateInvoice(orderId);
            return ResponseEntity.ok(invoice);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/pay")
    @PreAuthorize("hasRole('RETAILER') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<Invoice> markAsPaid(@PathVariable Long id, @RequestBody PaymentRequest request) {
        try {
            Invoice invoice = invoiceService.markAsPaid(id, request.getPaymentMethod(), request.getPaymentReference());
            return ResponseEntity.ok(invoice);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DTO class
    public static class PaymentRequest {
        private String paymentMethod;
        private String paymentReference;

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getPaymentReference() { return paymentReference; }
        public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
    }
}