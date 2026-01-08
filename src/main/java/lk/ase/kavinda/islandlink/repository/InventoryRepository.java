package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.Inventory;
import lk.ase.kavinda.islandlink.entity.Product;
import lk.ase.kavinda.islandlink.entity.RDC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductAndRdc(Product product, RDC rdc);
    @Query("SELECT i FROM Inventory i WHERE i.rdc = :rdc")
    List<Inventory> findByRdc(@Param("rdc") RDC rdc);
    
    @Query("SELECT i FROM Inventory i WHERE i.availableStock > 0")
    List<Inventory> findAvailableStock();
}