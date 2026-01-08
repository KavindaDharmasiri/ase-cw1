package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByStatus(PurchaseOrder.POStatus status);
}