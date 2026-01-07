package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.entity.DeliveryZone;
import lk.ase.kavinda.islandlink.repository.DeliveryZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/delivery-zones")
@CrossOrigin(origins = "http://localhost:4200")
public class DeliveryZoneController {

    @Autowired
    private DeliveryZoneRepository deliveryZoneRepository;

    @GetMapping
    public List<DeliveryZone> getAllDeliveryZones() {
        return deliveryZoneRepository.findAll();
    }

    @GetMapping("/active")
    public List<DeliveryZone> getActiveDeliveryZones() {
        return deliveryZoneRepository.findByActiveTrue();
    }

    @PostMapping
    public DeliveryZone createDeliveryZone(@RequestBody DeliveryZone deliveryZone) {
        return deliveryZoneRepository.save(deliveryZone);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryZone> updateDeliveryZone(@PathVariable Long id, @RequestBody DeliveryZone deliveryZone) {
        return deliveryZoneRepository.findById(id)
                .map(existing -> {
                    existing.setName(deliveryZone.getName());
                    existing.setDescription(deliveryZone.getDescription());
                    existing.setDeliveryFee(deliveryZone.getDeliveryFee());
                    existing.setEstimatedDeliveryDays(deliveryZone.getEstimatedDeliveryDays());
                    existing.setActive(deliveryZone.getActive());
                    return ResponseEntity.ok(deliveryZoneRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<DeliveryZone> activateDeliveryZone(@PathVariable Long id) {
        return deliveryZoneRepository.findById(id)
                .map(zone -> {
                    zone.setActive(true);
                    return ResponseEntity.ok(deliveryZoneRepository.save(zone));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<DeliveryZone> deactivateDeliveryZone(@PathVariable Long id) {
        return deliveryZoneRepository.findById(id)
                .map(zone -> {
                    zone.setActive(false);
                    return ResponseEntity.ok(deliveryZoneRepository.save(zone));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}