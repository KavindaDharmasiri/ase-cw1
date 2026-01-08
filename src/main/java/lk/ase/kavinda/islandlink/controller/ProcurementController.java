package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.dto.CreatePurchaseOrderRequest;
import lk.ase.kavinda.islandlink.entity.PurchaseOrder;
import lk.ase.kavinda.islandlink.service.ProcurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/procurement")
@CrossOrigin(origins = "http://localhost:4200")
public class ProcurementController {

    @Autowired
    private ProcurementService procurementService;

    @GetMapping("/purchase-orders")
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return procurementService.getAllPurchaseOrders();
    }

    @GetMapping("/purchase-orders/{id}")
    public ResponseEntity<PurchaseOrder> getPurchaseOrderById(@PathVariable Long id) {
        return procurementService.getPurchaseOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/purchase-orders")
    public PurchaseOrder createPurchaseOrder(@RequestBody CreatePurchaseOrderRequest request) {
        return procurementService.createPurchaseOrderFromRequest(request);
    }

    @PutMapping("/purchase-orders/{id}/status")
    public ResponseEntity<PurchaseOrder> updatePurchaseOrderStatus(
            @PathVariable Long id, 
            @RequestParam PurchaseOrder.POStatus status) {
        try {
            PurchaseOrder updatedPO = procurementService.updatePurchaseOrderStatus(id, status);
            return ResponseEntity.ok(updatedPO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/purchase-orders/status/{status}")
    public List<PurchaseOrder> getPurchaseOrdersByStatus(@PathVariable PurchaseOrder.POStatus status) {
        return procurementService.getPurchaseOrdersByStatus(status);
    }

    @GetMapping("/purchase-orders/rdc/{rdcId}")
    public List<PurchaseOrder> getPurchaseOrdersByRdc(@PathVariable Long rdcId) {
        return procurementService.getPurchaseOrdersByRdc(rdcId);
    }
}