package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.dto.DriverDTO;
import lk.ase.kavinda.islandlink.entity.Driver;
import lk.ase.kavinda.islandlink.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "http://localhost:4200")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @GetMapping
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('RDC_STAFF') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<Driver> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    @PostMapping
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('RDC_STAFF') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<Driver> createDriver(@RequestBody DriverDTO driverDTO) {
        Driver createdDriver = driverService.createDriver(driverDTO);
        return ResponseEntity.ok(createdDriver);
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('RDC_STAFF') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<Driver> getAvailableDrivers() {
        return driverService.getAvailableDrivers();
    }

    @PostMapping("/create-sample")
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('RDC_STAFF') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<String> createSampleDrivers() {
        driverService.createSampleDrivers();
        return ResponseEntity.ok("Sample drivers created");
    }
}