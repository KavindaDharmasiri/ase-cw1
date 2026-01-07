package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.entity.Payment;
import lk.ase.kavinda.islandlink.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<Payment> processPayment(@RequestBody Map<String, Object> paymentRequest) {
        try {
            Payment payment = paymentService.processPayment(paymentRequest);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<Payment>> getPaymentHistory() {
        List<Payment> payments = paymentService.getPaymentHistory();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrderId(@PathVariable Long orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        if (payment != null) {
            return ResponseEntity.ok(payment);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{paymentId}/retry")
    public ResponseEntity<Payment> retryPayment(@PathVariable Long paymentId) {
        try {
            Payment payment = paymentService.retryPayment(paymentId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}