package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByActiveTrue();
    Supplier findByName(String name);
    List<Supplier> findByNameContainingIgnoreCase(String name);
}