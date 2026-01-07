package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.Payment;
import lk.ase.kavinda.islandlink.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment processPayment(Map<String, Object> paymentRequest) {
        Payment payment = new Payment();
        payment.setOrderId(Long.valueOf(paymentRequest.get("orderId").toString()));
        payment.setAmount(new BigDecimal(paymentRequest.get("amount").toString()));
        payment.setMethod(Payment.PaymentMethod.valueOf(paymentRequest.get("method").toString()));
        
        // Simulate payment processing
        if (payment.getMethod() == Payment.PaymentMethod.ONLINE) {
            // Simulate online payment processing
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setTransactionId("TXN_" + UUID.randomUUID().toString().substring(0, 8));
        } else {
            // Cash on delivery
            payment.setStatus(Payment.PaymentStatus.PENDING);
        }
        
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentHistory() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public Payment retryPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment != null && payment.getStatus() == Payment.PaymentStatus.FAILED) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setTransactionId("TXN_" + UUID.randomUUID().toString().substring(0, 8));
            return paymentRepository.save(payment);
        }
        return payment;
    }
}