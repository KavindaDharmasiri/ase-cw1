package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.StockAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {
    List<StockAlert> findByStatusOrderByCreatedAtDesc(StockAlert.AlertStatus status);
    List<StockAlert> findByProductIdAndStatus(Long productId, StockAlert.AlertStatus status);
}