package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.service.OrderService;
import lk.ase.kavinda.islandlink.service.ProductService;
import lk.ase.kavinda.islandlink.service.InventoryService;
import lk.ase.kavinda.islandlink.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('HEAD_OFFICE_MANAGER')")
public class ReportController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private DeliveryService deliveryService;

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardReport() {
        Map<String, Object> report = new HashMap<>();
        
        report.put("totalOrders", orderService.getAllOrders().size());
        report.put("totalProducts", productService.getAllProducts().size());
        report.put("totalInventoryItems", inventoryService.getAllInventory().size());
        report.put("lowStockItems", inventoryService.getLowStockItems().size());
        report.put("totalDeliveries", deliveryService.getAllDeliveries().size());
        
        return report;
    }

    @GetMapping("/sales")
    public Map<String, Object> getSalesReport() {
        Map<String, Object> report = new HashMap<>();
        
        var orders = orderService.getAllOrders();
        double totalSales = orders.stream()
                .mapToDouble(order -> order.getTotalAmount().doubleValue())
                .sum();
        
        report.put("totalSales", totalSales);
        report.put("totalOrders", orders.size());
        report.put("averageOrderValue", orders.isEmpty() ? 0 : totalSales / orders.size());
        
        return report;
    }

    @GetMapping("/inventory")
    public Map<String, Object> getInventoryReport() {
        Map<String, Object> report = new HashMap<>();
        
        var inventory = inventoryService.getAllInventory();
        var lowStock = inventoryService.getLowStockItems();
        
        report.put("totalItems", inventory.size());
        report.put("lowStockItems", lowStock.size());
        report.put("lowStockPercentage", inventory.isEmpty() ? 0 : (lowStock.size() * 100.0) / inventory.size());
        
        return report;
    }

    @GetMapping("/deliveries")
    public Map<String, Object> getDeliveryReport() {
        Map<String, Object> report = new HashMap<>();
        
        var deliveries = deliveryService.getAllDeliveries();
        long delivered = deliveries.stream()
                .filter(d -> "DELIVERED".equals(d.getStatus().toString()))
                .count();
        
        report.put("totalDeliveries", deliveries.size());
        report.put("deliveredCount", delivered);
        report.put("deliveryRate", deliveries.isEmpty() ? 0 : (delivered * 100.0) / deliveries.size());
        
        return report;
    }
}