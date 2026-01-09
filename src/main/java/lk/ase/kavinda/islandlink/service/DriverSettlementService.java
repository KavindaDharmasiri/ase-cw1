package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.*;
import lk.ase.kavinda.islandlink.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DriverSettlementService {

    @Autowired
    private DriverSettlementRepository settlementRepository;
    
    @Autowired
    private DeliveryRouteRepository routeRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Autowired
    private RDCRepository rdcRepository;
    
    @Autowired
    private FinancialService financialService;

    @Transactional
    public DriverSettlement createSettlementFromRoute(Long routeId, String settledBy) {
        DeliveryRoute route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));
        
        if (route.getDriverName() == null || route.getDriverName().isEmpty()) {
            throw new RuntimeException("No driver assigned to route");
        }
        
        // Find driver by name (simplified approach)
        Driver driver = driverRepository.findAll().stream()
                .filter(d -> d.getName().equals(route.getDriverName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Driver not found: " + route.getDriverName()));
        
        // Find RDC by location
        RDC rdc = rdcRepository.findByName(route.getRdcLocation())
                .orElseThrow(() -> new RuntimeException("RDC not found: " + route.getRdcLocation()));
        
        DriverSettlement settlement = new DriverSettlement();
        settlement.setSettlementNumber("SET-" + System.currentTimeMillis());
        settlement.setDriver(driver);
        settlement.setRdc(rdc);
        settlement.setSettledBy(settledBy);
        
        // Calculate totals from delivered orders in this route
        BigDecimal totalCash = BigDecimal.ZERO;
        BigDecimal totalCheque = BigDecimal.ZERO;
        
        // Get deliveries for this route and calculate totals
        if (route.getDeliveries() != null) {
            for (Delivery delivery : route.getDeliveries()) {
                if (delivery.getStatus() == Delivery.DeliveryStatus.DELIVERED) {
                    Payment payment = paymentRepository.findByOrderId(delivery.getOrder().getId());
                    if (payment != null && payment.getStatus() == Payment.PaymentStatus.COMPLETED) {
                        if (payment.getMethod() == Payment.PaymentMethod.CASH_ON_DELIVERY) {
                            totalCash = totalCash.add(payment.getAmount());
                        } else {
                            totalCheque = totalCheque.add(payment.getAmount());
                        }
                    }
                }
            }
        }
        
        settlement.setTotalCashCollected(totalCash);
        settlement.setTotalChequeCollected(totalCheque);
        settlement.setTotalAmount(totalCash.add(totalCheque));
        
        return settlementRepository.save(settlement);
    }
    
    @Transactional
    public DriverSettlement verifySettlement(Long settlementId, String variances, String verifiedBy) {
        DriverSettlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new RuntimeException("Settlement not found"));
        
        settlement.setStatus(DriverSettlement.SettlementStatus.VERIFIED);
        settlement.setRemarks(variances);
        
        // Update financial ledger for cash book
        if (settlement.getTotalCashCollected().compareTo(BigDecimal.ZERO) > 0) {
            financialService.recordCashDeposit(settlement);
        }
        
        return settlementRepository.save(settlement);
    }
}