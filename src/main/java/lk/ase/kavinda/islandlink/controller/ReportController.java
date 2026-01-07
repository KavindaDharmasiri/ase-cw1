package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.service.OrderService;
import lk.ase.kavinda.islandlink.service.ProductService;
import lk.ase.kavinda.islandlink.service.InventoryService;
import lk.ase.kavinda.islandlink.service.DeliveryService;
import lk.ase.kavinda.islandlink.service.ReportExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private ReportExportService reportExportService;

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

    @GetMapping("/sales/export")
    public ResponseEntity<byte[]> exportSalesReport() {
        List<Object[]> salesData = Arrays.asList(
            new Object[]{"ORD001", "ABC Store", "Product A", "10", "$100", "2024-01-06"},
            new Object[]{"ORD002", "XYZ Shop", "Product B", "5", "$50", "2024-01-06"}
        );
        
        byte[] csvData = reportExportService.generateSalesReport(salesData);
        String filename = reportExportService.generateReportFilename("sales");
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csvData);
    }

    @GetMapping("/inventory/export")
    public ResponseEntity<byte[]> exportInventoryReport() {
        List<Object[]> inventoryData = Arrays.asList(
            new Object[]{"Product A", "Category 1", "50", "10", "Colombo RDC", "2024-01-06"},
            new Object[]{"Product B", "Category 2", "25", "5", "Kandy RDC", "2024-01-06"}
        );
        
        byte[] csvData = reportExportService.generateInventoryReport(inventoryData);
        String filename = reportExportService.generateReportFilename("inventory");
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csvData);
    }
}