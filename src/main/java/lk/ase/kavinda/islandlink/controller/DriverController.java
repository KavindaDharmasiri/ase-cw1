package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.entity.Driver;
import lk.ase.kavinda.islandlink.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "http://localhost:4200")
public class DriverController {

    @Autowired
    private DriverRepository driverRepository;

    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @GetMapping("/active")
    public List<Driver> getActiveDrivers() {
        return driverRepository.findByActiveTrue();
    }

    @PostMapping
    public Driver createDriver(@RequestBody Driver driver) {
        return driverRepository.save(driver);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriver(@PathVariable Long id, @RequestBody Driver driver) {
        return driverRepository.findById(id)
                .map(existing -> {
                    existing.setName(driver.getName());
                    existing.setLicenseNumber(driver.getLicenseNumber());
                    existing.setPhone(driver.getPhone());
                    existing.setActive(driver.getActive());
                    return ResponseEntity.ok(driverRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDriver(@PathVariable Long id) {
        return driverRepository.findById(id)
                .map(driver -> {
                    driver.setActive(false);
                    driverRepository.save(driver);
                    return ResponseEntity.ok("Driver deactivated successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}