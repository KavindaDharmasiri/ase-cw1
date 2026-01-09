package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.service.OrderService;
import lk.ase.kavinda.islandlink.service.ProductService;
import lk.ase.kavinda.islandlink.service.InventoryService;
import lk.ase.kavinda.islandlink.service.DeliveryService;
import lk.ase.kavinda.islandlink.service.ReportExportService;
import lk.ase.kavinda.islandlink.service.FinancialService;
import lk.ase.kavinda.islandlink.entity.Order;
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
    
    @Autowired
    private FinancialService financialService;

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String category) {
        Map<String, Object> report = new HashMap<>();
        
        // Apply filters if provided
        var orders = orderService.getAllOrders();
        if (startDate != null && endDate != null) {
            // Filter by date range
            orders = orders.stream()
                    .filter(order -> order.getOrderDate().isAfter(java.time.LocalDateTime.parse(startDate))
                            && order.getOrderDate().isBefore(java.time.LocalDateTime.parse(endDate)))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        report.put("totalOrders", orders.size());
        report.put("totalProducts", productService.getAllProducts().size());
        report.put("totalInventoryItems", inventoryService.getAllInventory().size());
        report.put("lowStockItems", inventoryService.getLowStockItems().size());
        report.put("totalDeliveries", deliveryService.getAllDeliveries().size());
        
        // Add financial metrics
        double totalRevenue = orders.stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.DELIVERED)
                .mapToDouble(order -> order.getTotalAmount().doubleValue())
                .sum();
        report.put("totalRevenue", totalRevenue);
        
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

    @GetMapping("/financial")
    public Map<String, Object> getFinancialReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> report = new HashMap<>();
        
        java.time.LocalDateTime start = startDate != null ? 
            java.time.LocalDateTime.parse(startDate) : java.time.LocalDateTime.now().minusDays(30);
        java.time.LocalDateTime end = endDate != null ? 
            java.time.LocalDateTime.parse(endDate) : java.time.LocalDateTime.now();
        
        var ledgerEntries = financialService.getLedgerEntries(start, end);
        
        double totalRevenue = ledgerEntries.stream()
                .filter(entry -> entry.getAccountType() == lk.ase.kavinda.islandlink.entity.FinancialLedger.AccountType.SALES_REVENUE)
                .mapToDouble(entry -> entry.getCreditAmount().doubleValue())
                .sum();
                
        double totalCash = ledgerEntries.stream()
                .filter(entry -> entry.getAccountType() == lk.ase.kavinda.islandlink.entity.FinancialLedger.AccountType.CASH)
                .mapToDouble(entry -> entry.getDebitAmount().subtract(entry.getCreditAmount()).doubleValue())
                .sum();
        
        report.put("totalRevenue", totalRevenue);
        report.put("totalCash", totalCash);
        report.put("ledgerEntries", ledgerEntries.size());
        
        return report;
    }

    @GetMapping("/settlements")
    public Map<String, Object> getSettlementReport() {
        Map<String, Object> report = new HashMap<>();
        
        // This would typically fetch from DriverSettlementRepository
        report.put("totalSettlements", 0);
        report.put("pendingSettlements", 0);
        report.put("completedSettlements", 0);
        
        return report;
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