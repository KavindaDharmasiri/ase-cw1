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

    @GetMapping("/rdc/{rdcLocation}")
    public List<Inventory> getInventoryByRdc(@PathVariable String rdcLocation) {
        return inventoryService.getInventoryByRdc(rdcLocation);
    }

    @GetMapping("/product/{productId}/rdc/{rdcLocation}")
    public ResponseEntity<Inventory> getInventoryByProductAndRdc(
            @PathVariable Long productId, 
            @PathVariable String rdcLocation) {
        return inventoryService.getInventoryByProductAndRdc(productId, rdcLocation)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update")
    public ResponseEntity<Inventory> updateStock(
            @RequestParam Long productId,
            @RequestParam String rdcLocation,
            @RequestParam Integer newStock) {
        try {
            Inventory inventory = inventoryService.updateStock(productId, rdcLocation, newStock);
            return ResponseEntity.ok(inventory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<Inventory> transferStock(
            @RequestParam Long productId,
            @RequestParam String fromRdc,
            @RequestParam String toRdc,
            @RequestParam Integer quantity) {
        try {
            Inventory inventory = inventoryService.transferStock(productId, fromRdc, toRdc, quantity);
            return ResponseEntity.ok(inventory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/low-stock")
    public List<Inventory> getLowStockItems() {
        return inventoryService.getLowStockItems();
    }

    @GetMapping("/low-stock/rdc/{rdcLocation}")
    public List<Inventory> getLowStockItemsByRdc(@PathVariable String rdcLocation) {
        return inventoryService.getLowStockItemsByRdc(rdcLocation);
    }
}