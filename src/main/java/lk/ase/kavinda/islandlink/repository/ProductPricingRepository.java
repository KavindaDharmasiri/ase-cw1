package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.Product;
import lk.ase.kavinda.islandlink.entity.ProductPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPricingRepository extends JpaRepository<ProductPricing, Long> {
    
    List<ProductPricing> findByProduct(Product product);
    
    @Query("SELECT pp FROM ProductPricing pp WHERE pp.product = ?1 AND pp.status = 'ACTIVE' " +
           "AND (pp.effectiveDate IS NULL OR pp.effectiveDate <= ?2) " +
           "AND (pp.expiryDate IS NULL OR pp.expiryDate > ?2)")
    Optional<ProductPricing> findActiveByProductAndDate(Product product, LocalDateTime date);
    
    @Query("SELECT pp FROM ProductPricing pp WHERE pp.status = 'ACTIVE' " +
           "AND pp.expiryDate IS NOT NULL AND pp.expiryDate <= ?1")
    List<ProductPricing> findExpiredPricing(LocalDateTime date);
    
    List<ProductPricing> findByStatus(ProductPricing.PricingStatus status);
    
    @Query("SELECT pp FROM ProductPricing pp WHERE pp.promotionalPrice IS NOT NULL " +
           "AND pp.status = 'ACTIVE' " +
           "AND (pp.effectiveDate IS NULL OR pp.effectiveDate <= ?1) " +
           "AND (pp.expiryDate IS NULL OR pp.expiryDate > ?1)")
    List<ProductPricing> findActivePromotions(LocalDateTime date);
}