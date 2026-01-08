package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.PurchaseOrder;
import lk.ase.kavinda.islandlink.entity.RDC;
import lk.ase.kavinda.islandlink.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findBySupplier(Supplier supplier);
    List<PurchaseOrder> findByRdc(RDC rdc);
    List<PurchaseOrder> findByStatus(PurchaseOrder.POStatus status);
    List<PurchaseOrder> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT MAX(p.poNumber) FROM PurchaseOrder p WHERE p.poNumber LIKE ?1%")
    String findMaxPoNumberByPrefix(String prefix);
}