package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByOrderId(Long orderId);
    List<Invoice> findByPaymentStatus(Invoice.PaymentStatus status);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}