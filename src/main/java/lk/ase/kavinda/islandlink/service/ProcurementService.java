package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.dto.CreatePurchaseOrderRequest;
import lk.ase.kavinda.islandlink.entity.*;
import lk.ase.kavinda.islandlink.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ProcurementService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private RDCRepository rdcRepository;
    
    @Autowired
    private ProductRepository productRepository;

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    public Optional<PurchaseOrder> getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id);
    }

    @Transactional
    public PurchaseOrder createPurchaseOrderFromRequest(CreatePurchaseOrderRequest request) {
        // Find supplier
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        
        // Create PurchaseOrder
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        
        // Set RDC if provided
        if (request.getRdcId() != null) {
            RDC rdc = rdcRepository.findById(request.getRdcId())
                    .orElseThrow(() -> new RuntimeException("RDC not found"));
            purchaseOrder.setRdc(rdc);
        }
        
        // Generate PO number
        String poNumber = generatePoNumber();
        purchaseOrder.setPoNumber(poNumber);
        
        // Set initial total amount to zero (will be updated when items are added)
        purchaseOrder.setTotalAmount(BigDecimal.ZERO);
        
        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    public PurchaseOrder updatePurchaseOrderStatus(Long id, PurchaseOrder.POStatus status) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
        
        po.setStatus(status);
        if (status == PurchaseOrder.POStatus.RECEIVED) {
            po.setActualDeliveryDate(LocalDateTime.now());
        }
        
        return purchaseOrderRepository.save(po);
    }

    public List<PurchaseOrder> getPurchaseOrdersByStatus(PurchaseOrder.POStatus status) {
        return purchaseOrderRepository.findByStatus(status);
    }

    public List<PurchaseOrder> getPurchaseOrdersByRdc(Long rdcId) {
        RDC rdc = rdcRepository.findById(rdcId)
                .orElseThrow(() -> new RuntimeException("RDC not found"));
        return purchaseOrderRepository.findByRdc(rdc);
    }

    private String generatePoNumber() {
        String prefix = "PO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String maxPoNumber = purchaseOrderRepository.findMaxPoNumberByPrefix(prefix);
        
        if (maxPoNumber == null) {
            return prefix + "001";
        }
        
        int nextNumber = Integer.parseInt(maxPoNumber.substring(prefix.length())) + 1;
        return prefix + String.format("%03d", nextNumber);
    }
}