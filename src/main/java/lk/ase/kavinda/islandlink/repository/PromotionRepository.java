package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByActiveTrue();
    
    @Query("SELECT p FROM Promotion p WHERE p.active = true AND p.startDate <= :now AND p.endDate >= :now")
    List<Promotion> findActivePromotions(LocalDateTime now);
    
    @Query("SELECT p FROM Promotion p JOIN p.applicableProducts prod WHERE prod.id = :productId AND p.active = true AND p.startDate <= :now AND p.endDate >= :now")
    List<Promotion> findActivePromotionsForProduct(Long productId, LocalDateTime now);
}