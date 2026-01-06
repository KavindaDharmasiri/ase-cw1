package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.StockTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockTransferRepository extends JpaRepository<StockTransfer, Long> {
    List<StockTransfer> findByStatus(StockTransfer.TransferStatus status);
    List<StockTransfer> findByFromRdc(String fromRdc);
    List<StockTransfer> findByToRdc(String toRdc);
    List<StockTransfer> findByRequestedById(Long userId);
}