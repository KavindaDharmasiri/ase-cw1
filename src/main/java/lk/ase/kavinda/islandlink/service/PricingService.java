package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.Product;
import lk.ase.kavinda.islandlink.entity.ProductPricing;
import lk.ase.kavinda.islandlink.repository.ProductPricingRepository;
import lk.ase.kavinda.islandlink.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PricingService {

    @Autowired
    private ProductPricingRepository pricingRepository;
    
    @Autowired
    private ProductRepository productRepository;

    public List<ProductPricing> getAllPricing() {
        return pricingRepository.findAll();
    }

    public Optional<ProductPricing> getPricingById(Long id) {
        return pricingRepository.findById(id);
    }

    @Transactional
    public ProductPricing createPricing(ProductPricing pricing) {
        return pricingRepository.save(pricing);
    }

    @Transactional
    public ProductPricing updatePricing(Long id, ProductPricing pricing) {
        ProductPricing existing = pricingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pricing not found"));
        
        existing.setPricePerUnit(pricing.getPricePerUnit());
        existing.setTaxPercentage(pricing.getTaxPercentage());
        existing.setPromotionalPrice(pricing.getPromotionalPrice());
        existing.setDiscountPercentage(pricing.getDiscountPercentage());
        existing.setMinimumOrderQuantity(pricing.getMinimumOrderQuantity());
        existing.setEffectiveDate(pricing.getEffectiveDate());
        existing.setExpiryDate(pricing.getExpiryDate());
        existing.setStatus(pricing.getStatus());
        
        return pricingRepository.save(existing);
    }

    public void deletePricing(Long id) {
        pricingRepository.deleteById(id);
    }

    public List<ProductPricing> getPricingByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return pricingRepository.findByProduct(product);
    }

    public BigDecimal getEffectivePrice(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        Optional<ProductPricing> activePricing = pricingRepository
                .findActiveByProductAndDate(product, LocalDateTime.now());
        
        if (activePricing.isPresent()) {
            return activePricing.get().getEffectivePrice();
        }
        
        // Fallback to product's base price
        return product.getPrice();
    }

    public List<ProductPricing> getActivePromotions() {
        return pricingRepository.findActivePromotions(LocalDateTime.now());
    }

    @Transactional
    public void expireOldPricing() {
        List<ProductPricing> expiredPricing = pricingRepository.findExpiredPricing(LocalDateTime.now());
        for (ProductPricing pricing : expiredPricing) {
            pricing.setStatus(ProductPricing.PricingStatus.EXPIRED);
            pricingRepository.save(pricing);
        }
    }

    public boolean isProductOrderable(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Check if product is active
        if (product.getStatus() != Product.ProductStatus.ACTIVE) {
            return false;
        }
        
        // Check if there's active pricing
        Optional<ProductPricing> activePricing = pricingRepository
                .findActiveByProductAndDate(product, LocalDateTime.now());
        
        return activePricing.isPresent() || product.getPrice() != null;
    }
}