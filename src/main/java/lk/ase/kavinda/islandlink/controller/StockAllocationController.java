package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.service.StockAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stock-allocation")
@CrossOrigin(origins = "http://localhost:4200")
public class StockAllocationController {

    @Autowired
    private StockAllocationService stockAllocationService;

    @PostMapping("/allocate/{orderId}")
    @PreAuthorize("hasRole('RDC_STAFF')")
    public ResponseEntity<?> allocateStock(@PathVariable Long orderId) {
        try {
            stockAllocationService.allocateStockForOrder(orderId);
            return ResponseEntity.ok(Map.of("success", true, "message", "Stock allocated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/dispatch/{orderId}")
    @PreAuthorize("hasRole('RDC_STAFF')")
    public ResponseEntity<?> dispatchOrder(@PathVariable Long orderId) {
        try {
            stockAllocationService.dispatchOrder(orderId);
            return ResponseEntity.ok(Map.of("success", true, "message", "Order dispatched successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}