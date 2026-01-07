package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    List<StockTransaction> findByInventoryIdOrderByTransactionDateDesc(Long inventoryId);
    List<StockTransaction> findByTypeOrderByTransactionDateDesc(StockTransaction.TransactionType type);
}