package lk.ase.kavinda.islandlink.service;

import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportExportService {

    public byte[] exportToCsv(List<String[]> data, String[] headers) {
        StringBuilder csv = new StringBuilder();
        
        // Add headers
        csv.append(String.join(",", headers)).append("\n");
        
        // Add data rows
        for (String[] row : data) {
            csv.append(String.join(",", row)).append("\n");
        }
        
        return csv.toString().getBytes();
    }

    public byte[] generateSalesReport(List<Object[]> salesData) {
        String[] headers = {"Order ID", "Customer", "Product", "Quantity", "Amount", "Date"};
        
        List<String[]> csvData = salesData.stream()
            .map(row -> new String[]{
                row[0].toString(),
                row[1].toString(),
                row[2].toString(),
                row[3].toString(),
                row[4].toString(),
                row[5].toString()
            })
            .collect(java.util.stream.Collectors.toList());
            
        return exportToCsv(csvData, headers);
    }

    public byte[] generateInventoryReport(List<Object[]> inventoryData) {
        String[] headers = {"Product", "Category", "Current Stock", "Min Level", "RDC Location", "Last Updated"};
        
        List<String[]> csvData = inventoryData.stream()
            .map(row -> new String[]{
                row[0].toString(),
                row[1].toString(),
                row[2].toString(),
                row[3].toString(),
                row[4].toString(),
                row[5].toString()
            })
            .collect(java.util.stream.Collectors.toList());
            
        return exportToCsv(csvData, headers);
    }

    public byte[] generateDeliveryReport(List<Object[]> deliveryData) {
        String[] headers = {"Order ID", "Customer", "Status", "Driver", "Delivery Date", "RDC"};
        
        List<String[]> csvData = deliveryData.stream()
            .map(row -> new String[]{
                row[0].toString(),
                row[1].toString(),
                row[2].toString(),
                row[3].toString(),
                row[4].toString(),
                row[5].toString()
            })
            .collect(java.util.stream.Collectors.toList());
            
        return exportToCsv(csvData, headers);
    }

    public String generateReportFilename(String reportType) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return reportType + "_report_" + timestamp + ".csv";
    }
}