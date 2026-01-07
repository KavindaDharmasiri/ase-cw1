package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.service.OrderService;
import lk.ase.kavinda.islandlink.service.ProductService;
import lk.ase.kavinda.islandlink.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private DeliveryService deliveryService;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            long recentOrders = orderService.countRecentOrders(7);
            long pendingDeliveries = deliveryService.countPendingDeliveries();
            long availableProducts = productService.countAvailableProducts();

            return ResponseEntity.ok(Map.of(
                "recentOrders", recentOrders,
                "pendingDeliveries", pendingDeliveries,
                "availableProducts", availableProducts
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching dashboard stats");
        }
    }
}