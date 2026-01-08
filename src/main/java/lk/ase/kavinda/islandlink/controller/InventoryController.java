package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.entity.Inventory;
import lk.ase.kavinda.islandlink.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:4200")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public List<Inventory> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    @GetMapping("/rdc/{rdcId}")
    public List<Inventory> getInventoryByRdc(@PathVariable Long rdcId) {
        return inventoryService.getInventoryByRdc(rdcId);
    }

    @GetMapping("/product/{productId}/rdc/{rdcId}")
    public ResponseEntity<Inventory> getInventoryByProductAndRdc(
            @PathVariable Long productId, 
            @PathVariable Long rdcId) {
        return inventoryService.getInventoryByProductAndRdc(productId, rdcId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update")
    public ResponseEntity<Inventory> updateStock(
            @RequestParam Long productId,
            @RequestParam Long rdcId,
            @RequestParam Integer newStock) {
        try {
            Inventory inventory = inventoryService.updateStock(productId, rdcId, newStock);
            return ResponseEntity.ok(inventory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<Inventory> transferStock(
            @RequestParam Long productId,
            @RequestParam Long fromRdcId,
            @RequestParam Long toRdcId,
            @RequestParam Integer quantity) {
        try {
            Inventory inventory = inventoryService.transferStock(productId, fromRdcId, toRdcId, quantity);
            return ResponseEntity.ok(inventory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/low-stock")
    public List<Inventory> getLowStockItems() {
        return inventoryService.getLowStockItems();
    }

    @GetMapping("/low-stock/rdc/{rdcId}")
    public List<Inventory> getLowStockItemsByRdc(@PathVariable Long rdcId) {
        return inventoryService.getLowStockItemsByRdc(rdcId);
    }

    @DeleteMapping("/product/{productId}/rdc/{rdcId}")
    public ResponseEntity<String> deleteInventoryByProductAndRdc(
            @PathVariable Long productId, 
            @PathVariable Long rdcId) {
        try {
            inventoryService.deleteInventoryByProductAndRdc(productId, rdcId);
            return ResponseEntity.ok("Inventory record deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete inventory: " + e.getMessage());
        }
    }
}