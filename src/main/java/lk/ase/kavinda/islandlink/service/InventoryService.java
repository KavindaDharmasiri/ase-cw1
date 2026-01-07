package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.Inventory;
import lk.ase.kavinda.islandlink.entity.Product;
import lk.ase.kavinda.islandlink.repository.InventoryRepository;
import lk.ase.kavinda.islandlink.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public List<Inventory> getInventoryByRdc(String rdcLocation) {
        return inventoryRepository.findByRdcLocation(rdcLocation);
    }

    public Optional<Inventory> getInventoryByProductAndRdc(Long productId, String rdcLocation) {
        return inventoryRepository.findByProductIdAndRdcLocation(productId, rdcLocation);
    }

    public Inventory updateStock(Long productId, String rdcLocation, Integer newStock) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductIdAndRdcLocation(productId, rdcLocation);
        
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            inventory.setCurrentStock(newStock);
            return inventoryRepository.save(inventory);
        } else {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            Inventory newInventory = new Inventory(product, rdcLocation, newStock);
            return inventoryRepository.save(newInventory);
        }
    }

    public Inventory transferStock(Long productId, String fromRdc, String toRdc, Integer quantity) {
        // Reduce stock from source RDC
        Inventory fromInventory = inventoryRepository.findByProductIdAndRdcLocation(productId, fromRdc)
                .orElseThrow(() -> new RuntimeException("Source inventory not found"));
        
        if (fromInventory.getAvailableStock() < quantity) {
            throw new RuntimeException("Insufficient stock for transfer");
        }
        
        fromInventory.setCurrentStock(fromInventory.getCurrentStock() - quantity);
        inventoryRepository.save(fromInventory);
        
        // Add stock to destination RDC
        Optional<Inventory> toInventoryOpt = inventoryRepository.findByProductIdAndRdcLocation(productId, toRdc);
        Inventory toInventory;
        
        if (toInventoryOpt.isPresent()) {
            toInventory = toInventoryOpt.get();
            toInventory.setCurrentStock(toInventory.getCurrentStock() + quantity);
        } else {
            Product product = fromInventory.getProduct();
            toInventory = new Inventory(product, toRdc, quantity);
        }
        
        return inventoryRepository.save(toInventory);
    }

    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }

    public List<Inventory> getLowStockItemsByRdc(String rdcLocation) {
        return inventoryRepository.findLowStockItemsByRdc(rdcLocation);
    }

    public void deleteInventoryByProduct(Long productId) {
        inventoryRepository.deleteByProductId(productId);
    }

    @Transactional
    public void deleteInventoryByProductAndRdc(Long productId, String rdcLocation) {
        inventoryRepository.deleteByProductIdAndRdcLocation(productId, rdcLocation);
    }
}
