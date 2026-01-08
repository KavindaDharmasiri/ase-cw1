package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.GoodsReceiptNote;
import lk.ase.kavinda.islandlink.entity.RDC;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GoodsReceiptNoteRepository extends JpaRepository<GoodsReceiptNote, Long> {
    List<GoodsReceiptNote> findByRdc(RDC rdc);
}