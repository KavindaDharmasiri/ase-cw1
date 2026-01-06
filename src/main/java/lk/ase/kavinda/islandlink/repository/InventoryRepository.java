package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByRdcLocation(String rdcLocation);
    
    Optional<Inventory> findByProductIdAndRdcLocation(Long productId, String rdcLocation);
    
    @Query("SELECT i FROM Inventory i WHERE i.currentStock <= i.product.minStockLevel")
    List<Inventory> findLowStockItems();
    
    @Query("SELECT i FROM Inventory i WHERE i.rdcLocation = ?1 AND i.currentStock <= i.product.minStockLevel")
    List<Inventory> findLowStockItemsByRdc(String rdcLocation);
}