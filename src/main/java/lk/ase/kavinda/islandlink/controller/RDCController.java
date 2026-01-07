package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.entity.RDC;
import lk.ase.kavinda.islandlink.repository.RDCRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rdcs")
@CrossOrigin(origins = "http://localhost:4200")
public class RDCController {

    @Autowired
    private RDCRepository rdcRepository;

    @GetMapping
    public List<RDC> getAllActiveRDCs() {
        return rdcRepository.findByActiveTrue();
    }

    @GetMapping("/all")
    public List<RDC> getAllRDCs() {
        return rdcRepository.findAll();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<String> activateRDC(@PathVariable Long id) {
        if (rdcRepository.existsById(id)) {
            RDC rdc = rdcRepository.findById(id).get();
            rdc.setActive(true);
            rdcRepository.save(rdc);
            return ResponseEntity.ok("RDC activated successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<RDC> createRDC(@RequestBody RDC rdc) {
        rdc.setActive(true);
        RDC savedRDC = rdcRepository.save(rdc);
        return ResponseEntity.ok(savedRDC);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RDC> updateRDC(@PathVariable Long id, @RequestBody RDC rdc) {
        if (rdcRepository.existsById(id)) {
            rdc.setId(id);
            RDC updatedRDC = rdcRepository.save(rdc);
            return ResponseEntity.ok(updatedRDC);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRDC(@PathVariable Long id) {
        if (rdcRepository.existsById(id)) {
            RDC rdc = rdcRepository.findById(id).get();
            rdc.setActive(false);
            rdcRepository.save(rdc);
            return ResponseEntity.ok("RDC deactivated successfully");
        }
        return ResponseEntity.notFound().build();
    }
}