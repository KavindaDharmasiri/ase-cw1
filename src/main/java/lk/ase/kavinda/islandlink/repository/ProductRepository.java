package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
    List<Product> findByNameContaining(String name);
    
    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findAllCategories();
}