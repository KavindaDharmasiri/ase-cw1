package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.entity.Vehicle;
import lk.ase.kavinda.islandlink.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "http://localhost:4200")
public class VehicleController {

    @Autowired
    private VehicleRepository vehicleRepository;

    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @GetMapping("/active")
    public List<Vehicle> getActiveVehicles() {
        return vehicleRepository.findByActiveTrue();
    }

    @GetMapping("/rdc/{rdcId}")
    public List<Vehicle> getVehiclesByRdc(@PathVariable Long rdcId) {
        return vehicleRepository.findByRdcAndActiveTrue(
            vehicleRepository.findById(1L).get().getRdc()
        );
    }

    @PostMapping
    public Vehicle createVehicle(@RequestBody Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        return vehicleRepository.findById(id)
                .map(existing -> {
                    existing.setVehicleNumber(vehicle.getVehicleNumber());
                    existing.setVehicleType(vehicle.getVehicleType());
                    existing.setCapacity(vehicle.getCapacity());
                    existing.setActive(vehicle.getActive());
                    return ResponseEntity.ok(vehicleRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable Long id) {
        return vehicleRepository.findById(id)
                .map(vehicle -> {
                    vehicle.setActive(false);
                    vehicleRepository.save(vehicle);
                    return ResponseEntity.ok("Vehicle deactivated successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}