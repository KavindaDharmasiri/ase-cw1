package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.Category;
import lk.ase.kavinda.islandlink.entity.Product;
import lk.ase.kavinda.islandlink.repository.CategoryRepository;
import lk.ase.kavinda.islandlink.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class BulkUploadService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public BulkUploadResult uploadProducts(MultipartFile file) {
        BulkUploadResult result = new BulkUploadResult();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }
                
                try {
                    Product product = parseProductFromCsv(line);
                    productRepository.save(product);
                    result.successCount++;
                } catch (Exception e) {
                    result.errors.add("Line " + (result.successCount + result.errorCount + 1) + ": " + e.getMessage());
                    result.errorCount++;
                }
            }
        } catch (Exception e) {
            result.errors.add("File processing error: " + e.getMessage());
            result.errorCount++;
        }
        
        return result;
    }

    private Product parseProductFromCsv(String line) {
        String[] fields = line.split(",");
        
        if (fields.length < 6) {
            throw new RuntimeException("Invalid CSV format. Expected: name,description,category,price,unit,minStockLevel");
        }
        
        Product product = new Product();
        product.setName(fields[0].trim());
        product.setDescription(fields[1].trim());
        
        // Find or create category
        String categoryName = fields[2].trim();
        Category category = categoryRepository.findByName(categoryName);
        if (category == null) {
            category = new Category(categoryName, "Auto-created category");
            category = categoryRepository.save(category);
        }
        product.setCategory(category);
        
        product.setPrice(new BigDecimal(fields[3].trim()));
        product.setUnit(fields[4].trim());
        product.setMinStockLevel(Integer.parseInt(fields[5].trim()));
        
        if (fields.length > 6) {
            product.setImageUrl(fields[6].trim());
        }
        
        return product;
    }

    public static class BulkUploadResult {
        public int successCount = 0;
        public int errorCount = 0;
        public List<String> errors = new ArrayList<>();
        
        public String getSummary() {
            return String.format("Success: %d, Errors: %d", successCount, errorCount);
        }
    }
}