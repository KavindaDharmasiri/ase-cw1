package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.dto.ProductDTO;
import lk.ase.kavinda.islandlink.entity.Category;
import lk.ase.kavinda.islandlink.entity.Product;
import lk.ase.kavinda.islandlink.repository.CategoryRepository;
import lk.ase.kavinda.islandlink.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProductFromDTO(ProductDTO productDTO) {
        System.out.println("Looking for category: " + productDTO.getCategory());
        Category category = categoryRepository.findByName(productDTO.getCategory());
        System.out.println("Found category: " + category);
        
        if (category == null) {
            throw new RuntimeException("Category not found: " + productDTO.getCategory());
        }
        
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setCategory(category);
        product.setPrice(productDTO.getPrice());
        product.setUnit(productDTO.getUnit());
        product.setImageUrl(productDTO.getImageUrl());
        product.setMinStockLevel(productDTO.getMinStockLevel());
        
        // Auto-generate SKU if not provided
        if (productDTO.getSku() == null || productDTO.getSku().trim().isEmpty()) {
            String sku = generateSKU(productDTO.getName(), category.getName());
            product.setSku(sku);
        } else {
            product.setSku(productDTO.getSku());
        }
        
        // Set brand or default
        product.setBrand(productDTO.getBrand() != null && !productDTO.getBrand().trim().isEmpty() ? productDTO.getBrand() : "Generic");
        
        // Auto-calculate purchase price if not provided
        if (productDTO.getPurchasePrice() == null || productDTO.getPurchasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            product.setPurchasePrice(productDTO.getPrice().multiply(new BigDecimal("0.8")));
        } else {
            product.setPurchasePrice(productDTO.getPurchasePrice());
        }
        
        // Set tax rate or default to 0
        product.setTaxRate(productDTO.getTaxRate() != null ? productDTO.getTaxRate() : BigDecimal.ZERO);
        
        return productRepository.save(product);
    }

    public Product updateProductFromDTO(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        Category category = categoryRepository.findByName(productDTO.getCategory());
        if (category == null) {
            throw new RuntimeException("Category not found: " + productDTO.getCategory());
        }
        
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setCategory(category);
        product.setPrice(productDTO.getPrice());
        product.setUnit(productDTO.getUnit());
        product.setImageUrl(productDTO.getImageUrl());
        product.setMinStockLevel(productDTO.getMinStockLevel());
        product.setSku(productDTO.getSku());
        product.setBrand(productDTO.getBrand());
        product.setPurchasePrice(productDTO.getPurchasePrice());
        product.setTaxRate(productDTO.getTaxRate());
        
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        try {
            productRepository.deleteById(id);
        } catch (Exception e) {
            throw new org.springframework.dao.DataIntegrityViolationException("Cannot delete product: it is referenced in inventory", e);
        }
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContaining(name);
    }

    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }

    public long countAvailableProducts() {
        return productRepository.count();
    }
    
    private String generateSKU(String productName, String categoryName) {
        // Generate SKU: First 3 letters of category + First 3 letters of product + timestamp
        String categoryPrefix = categoryName.length() >= 3 ? categoryName.substring(0, 3).toUpperCase() : categoryName.toUpperCase();
        String productPrefix = productName.replaceAll("\\s+", "").length() >= 3 ? 
            productName.replaceAll("\\s+", "").substring(0, 3).toUpperCase() : 
            productName.replaceAll("\\s+", "").toUpperCase();
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8); // Last 5 digits
        return categoryPrefix + "-" + productPrefix + "-" + timestamp;
    }
}