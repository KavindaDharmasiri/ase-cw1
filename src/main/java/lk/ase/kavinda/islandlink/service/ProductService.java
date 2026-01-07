package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.dto.ProductDTO;
import lk.ase.kavinda.islandlink.entity.Category;
import lk.ase.kavinda.islandlink.entity.Product;
import lk.ase.kavinda.islandlink.repository.CategoryRepository;
import lk.ase.kavinda.islandlink.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
}