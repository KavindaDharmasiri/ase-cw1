package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.dto.VehicleDTO;
import lk.ase.kavinda.islandlink.entity.Vehicle;
import lk.ase.kavinda.islandlink.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "http://localhost:4200")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('RDC_STAFF') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @PostMapping
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('RDC_STAFF') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<Vehicle> createVehicle(@RequestBody VehicleDTO vehicleDTO) {
        Vehicle createdVehicle = vehicleService.createVehicle(vehicleDTO);
        return ResponseEntity.ok(createdVehicle);
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('RDC_STAFF') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<Vehicle> getAvailableVehicles() {
        return vehicleService.getAvailableVehicles();
    }

    @PostMapping("/create-sample")
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('RDC_STAFF') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<String> createSampleVehicles() {
        vehicleService.createSampleVehicles();
        return ResponseEntity.ok("Sample vehicles created");
    }
}