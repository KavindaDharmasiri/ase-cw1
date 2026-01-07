package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.Promotion;
import lk.ase.kavinda.islandlink.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    public List<Promotion> getActivePromotions() {
        return promotionRepository.findActivePromotions(LocalDateTime.now());
    }

    public List<Promotion> getPromotionsForProduct(Long productId) {
        return promotionRepository.findActivePromotionsForProduct(productId, LocalDateTime.now());
    }

    public Promotion createPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    public Promotion updatePromotion(Long id, Promotion promotion) {
        Promotion existing = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
        
        existing.setName(promotion.getName());
        existing.setDescription(promotion.getDescription());
        existing.setType(promotion.getType());
        existing.setValue(promotion.getValue());
        existing.setStartDate(promotion.getStartDate());
        existing.setEndDate(promotion.getEndDate());
        existing.setActive(promotion.getActive());
        existing.setApplicableProducts(promotion.getApplicableProducts());
        
        return promotionRepository.save(existing);
    }

    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }

    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, Long productId) {
        List<Promotion> promotions = getPromotionsForProduct(productId);
        
        BigDecimal bestPrice = originalPrice;
        for (Promotion promotion : promotions) {
            BigDecimal discountedPrice = applyPromotion(originalPrice, promotion);
            if (discountedPrice.compareTo(bestPrice) < 0) {
                bestPrice = discountedPrice;
            }
        }
        
        return bestPrice;
    }

    private BigDecimal applyPromotion(BigDecimal originalPrice, Promotion promotion) {
        switch (promotion.getType()) {
            case PERCENTAGE_DISCOUNT:
                BigDecimal discount = originalPrice.multiply(promotion.getValue()).divide(BigDecimal.valueOf(100));
                return originalPrice.subtract(discount);
            case FIXED_AMOUNT_DISCOUNT:
                return originalPrice.subtract(promotion.getValue());
            default:
                return originalPrice;
        }
    }
}