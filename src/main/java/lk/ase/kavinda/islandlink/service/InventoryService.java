package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.Inventory;
import lk.ase.kavinda.islandlink.entity.Product;
import lk.ase.kavinda.islandlink.entity.RDC;
import lk.ase.kavinda.islandlink.repository.InventoryRepository;
import lk.ase.kavinda.islandlink.repository.ProductRepository;
import lk.ase.kavinda.islandlink.repository.RDCRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RDCRepository rdcRepository;

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public List<Inventory> getInventoryByRdc(Long rdcId) {
        RDC rdc = rdcRepository.findById(rdcId).orElse(null);
        return rdc != null ? inventoryRepository.findByRdc(rdc) : List.of();
    }

    public Optional<Inventory> getInventoryByProductAndRdc(Long productId, Long rdcId) {
        Product product = productRepository.findById(productId).orElse(null);
        RDC rdc = rdcRepository.findById(rdcId).orElse(null);
        if (product != null && rdc != null) {
            return inventoryRepository.findByProductAndRdc(product, rdc);
        }
        return Optional.empty();
    }

    public Inventory updateStock(Long productId, Long rdcId, Integer newStock) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        RDC rdc = rdcRepository.findById(rdcId)
                .orElseThrow(() -> new RuntimeException("RDC not found"));

        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductAndRdc(product, rdc);
        
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            inventory.setAvailableStock(newStock);
            inventory.setLastUpdated(LocalDateTime.now());
            return inventoryRepository.save(inventory);
        } else {
            Inventory newInventory = new Inventory();
            newInventory.setProduct(product);
            newInventory.setRdc(rdc);
            newInventory.setAvailableStock(newStock);
            return inventoryRepository.save(newInventory);
        }
    }

    public Inventory transferStock(Long productId, Long fromRdcId, Long toRdcId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        RDC fromRdc = rdcRepository.findById(fromRdcId)
                .orElseThrow(() -> new RuntimeException("Source RDC not found"));
        RDC toRdc = rdcRepository.findById(toRdcId)
                .orElseThrow(() -> new RuntimeException("Destination RDC not found"));

        // Reduce stock from source RDC
        Inventory fromInventory = inventoryRepository.findByProductAndRdc(product, fromRdc)
                .orElseThrow(() -> new RuntimeException("Source inventory not found"));
        
        if (fromInventory.getAvailableStock() < quantity) {
            throw new RuntimeException("Insufficient stock for transfer");
        }
        
        fromInventory.setAvailableStock(fromInventory.getAvailableStock() - quantity);
        fromInventory.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(fromInventory);
        
        // Add stock to destination RDC
        Optional<Inventory> toInventoryOpt = inventoryRepository.findByProductAndRdc(product, toRdc);
        Inventory toInventory;
        
        if (toInventoryOpt.isPresent()) {
            toInventory = toInventoryOpt.get();
            toInventory.setAvailableStock(toInventory.getAvailableStock() + quantity);
        } else {
            toInventory = new Inventory();
            toInventory.setProduct(product);
            toInventory.setRdc(toRdc);
            toInventory.setAvailableStock(quantity);
        }
        
        toInventory.setLastUpdated(LocalDateTime.now());
        return inventoryRepository.save(toInventory);
    }

    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findAvailableStock().stream()
                .filter(inv -> inv.getAvailableStock() < 10)
                .toList();
    }

    public List<Inventory> getLowStockItemsByRdc(Long rdcId) {
        return getInventoryByRdc(rdcId).stream()
                .filter(inv -> inv.getAvailableStock() < 10)
                .toList();
    }

    @Transactional
    public void deleteInventoryByProductAndRdc(Long productId, Long rdcId) {
        Product product = productRepository.findById(productId).orElse(null);
        RDC rdc = rdcRepository.findById(rdcId).orElse(null);
        if (product != null && rdc != null) {
            inventoryRepository.findByProductAndRdc(product, rdc)
                    .ifPresent(inventoryRepository::delete);
        }
    }

    public void addStock(Long productId, Long rdcId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        RDC rdc = rdcRepository.findById(rdcId)
                .orElseThrow(() -> new RuntimeException("RDC not found"));

        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductAndRdc(product, rdc);
        
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            inventory.setAvailableStock(inventory.getAvailableStock() + quantity);
            inventory.setLastUpdated(LocalDateTime.now());
            inventoryRepository.save(inventory);
        } else {
            Inventory newInventory = new Inventory();
            newInventory.setProduct(product);
            newInventory.setRdc(rdc);
            newInventory.setAvailableStock(quantity);
            inventoryRepository.save(newInventory);
        }
    }
}