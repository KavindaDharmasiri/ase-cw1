package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.dto.ProductDTO;
import lk.ase.kavinda.islandlink.dto.ProductResponseDTO;
import lk.ase.kavinda.islandlink.entity.Product;
import lk.ase.kavinda.islandlink.service.BulkUploadService;
import lk.ase.kavinda.islandlink.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private BulkUploadService bulkUploadService;

    @GetMapping
    public List<ProductResponseDTO> getAllProducts() {
        return productService.getAllProducts().stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ProductResponseDTO createProduct(@RequestBody ProductDTO productDTO) {
        Product product = productService.createProductFromDTO(productDTO);
        return new ProductResponseDTO(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        try {
            Product updatedProduct = productService.updateProductFromDTO(id, productDTO);
            return ResponseEntity.ok(new ProductResponseDTO(updatedProduct));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        if (!productService.getProductById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Product deleted successfully");
        } catch (Exception e) {
            System.out.println("Exception caught: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return ResponseEntity.badRequest().body("Cannot delete product: it is referenced in inventory");
        }
    }

    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category);
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String name) {
        return productService.searchProducts(name);
    }

    @GetMapping("/categories")
    public List<String> getAllCategories() {
        return productService.getAllCategories();
    }

    @PostMapping("/bulk-upload")
    public ResponseEntity<?> bulkUploadProducts(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }
        
        if (!file.getOriginalFilename().endsWith(".csv")) {
            return ResponseEntity.badRequest().body("Only CSV files are supported");
        }
        
        try {
            BulkUploadService.BulkUploadResult result = bulkUploadService.uploadProducts(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }
}
