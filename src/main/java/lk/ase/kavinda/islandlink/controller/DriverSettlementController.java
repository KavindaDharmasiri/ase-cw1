package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.entity.DriverSettlement;
import lk.ase.kavinda.islandlink.entity.DeliveryRoute;
import lk.ase.kavinda.islandlink.service.DriverSettlementService;
import lk.ase.kavinda.islandlink.repository.DriverSettlementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/driver-settlements")
@CrossOrigin(origins = "http://localhost:4200")
public class DriverSettlementController {

    @Autowired
    private DriverSettlementRepository settlementRepository;
    
    @Autowired
    private DriverSettlementService settlementService;

    @GetMapping
    @PreAuthorize("hasRole('RDC_STAFF') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<DriverSettlement> getAllSettlements() {
        return settlementRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('RDC_STAFF') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<DriverSettlement> getSettlementById(@PathVariable Long id) {
        return settlementRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/route/{routeId}/settle")
    @PreAuthorize("hasRole('RDC_STAFF')")
    public ResponseEntity<?> createSettlementFromRoute(@PathVariable Long routeId, @RequestBody SettlementRequest request) {
        try {
            DriverSettlement settlement = settlementService.createSettlementFromRoute(routeId, request.getSettledBy());
            return ResponseEntity.ok(Map.of("success", true, "settlement", settlement));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/verify")
    @PreAuthorize("hasRole('RDC_STAFF')")
    public ResponseEntity<?> verifySettlement(@PathVariable Long id, @RequestBody VerifySettlementRequest request) {
        try {
            DriverSettlement settlement = settlementService.verifySettlement(id, request.getVariances(), request.getVerifiedBy());
            return ResponseEntity.ok(Map.of("success", true, "settlement", settlement));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/driver/{driverId}")
    @PreAuthorize("hasRole('RDC_STAFF') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<DriverSettlement> getSettlementsByDriver(@PathVariable Long driverId) {
        return settlementRepository.findAll().stream()
                .filter(s -> s.getDriver().getId().equals(driverId))
                .toList();
    }
    
    public static class SettlementRequest {
        private String settledBy;
        
        public String getSettledBy() { return settledBy; }
        public void setSettledBy(String settledBy) { this.settledBy = settledBy; }
    }
    
    public static class VerifySettlementRequest {
        private String variances;
        private String verifiedBy;
        
        public String getVariances() { return variances; }
        public void setVariances(String variances) { this.variances = variances; }
        public String getVerifiedBy() { return verifiedBy; }
        public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }
    }
}