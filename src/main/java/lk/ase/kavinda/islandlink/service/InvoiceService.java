package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.Invoice;
import lk.ase.kavinda.islandlink.entity.Order;
import lk.ase.kavinda.islandlink.repository.InvoiceRepository;
import lk.ase.kavinda.islandlink.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    public Optional<Invoice> getInvoiceByOrderId(Long orderId) {
        return invoiceRepository.findByOrderId(orderId);
    }

    public List<Invoice> getInvoicesByPaymentStatus(Invoice.PaymentStatus status) {
        return invoiceRepository.findByPaymentStatus(status);
    }

    @Transactional
    public Invoice generateInvoice(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Check if invoice already exists
        Optional<Invoice> existingInvoice = invoiceRepository.findByOrderId(orderId);
        if (existingInvoice.isPresent()) {
            return existingInvoice.get();
        }

        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        
        BigDecimal subtotal = order.getTotalAmount();
        BigDecimal taxRate = new BigDecimal("0.10"); // 10% tax
        BigDecimal taxAmount = subtotal.multiply(taxRate);
        BigDecimal totalAmount = subtotal.add(taxAmount);

        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(taxAmount);
        invoice.setTotalAmount(totalAmount);

        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice markAsPaid(Long invoiceId, String paymentMethod, String paymentReference) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setPaymentStatus(Invoice.PaymentStatus.PAID);
        invoice.setPaidDate(LocalDateTime.now());
        invoice.setPaymentMethod(paymentMethod);
        invoice.setPaymentReference(paymentReference);

        return invoiceRepository.save(invoice);
    }

    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis();
    }
}