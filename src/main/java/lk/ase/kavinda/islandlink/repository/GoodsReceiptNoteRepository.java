package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.GoodsReceiptNote;
import lk.ase.kavinda.islandlink.entity.PurchaseOrder;
import lk.ase.kavinda.islandlink.entity.RDC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GoodsReceiptNoteRepository extends JpaRepository<GoodsReceiptNote, Long> {
    List<GoodsReceiptNote> findByPurchaseOrder(PurchaseOrder purchaseOrder);
    List<GoodsReceiptNote> findByRdc(RDC rdc);
    List<GoodsReceiptNote> findByReceivedDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT MAX(g.grnNumber) FROM GoodsReceiptNote g WHERE g.grnNumber LIKE ?1%")
    String findMaxGrnNumberByPrefix(String prefix);
}