package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.entity.DriverSettlement;
import lk.ase.kavinda.islandlink.repository.DriverSettlementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/driver-settlements")
@CrossOrigin(origins = "http://localhost:4200")
public class DriverSettlementController {

    @Autowired
    private DriverSettlementRepository settlementRepository;

    @GetMapping
    public List<DriverSettlement> getAllSettlements() {
        return settlementRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverSettlement> getSettlementById(@PathVariable Long id) {
        return settlementRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public DriverSettlement createSettlement(@RequestBody DriverSettlement settlement) {
        return settlementRepository.save(settlement);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DriverSettlement> updateSettlementStatus(
            @PathVariable Long id, 
            @RequestParam DriverSettlement.SettlementStatus status) {
        return settlementRepository.findById(id)
                .map(settlement -> {
                    settlement.setStatus(status);
                    return ResponseEntity.ok(settlementRepository.save(settlement));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/driver/{driverId}")
    public List<DriverSettlement> getSettlementsByDriver(@PathVariable Long driverId) {
        return settlementRepository.findAll().stream()
                .filter(s -> s.getDriver().getId().equals(driverId))
                .toList();
    }
}