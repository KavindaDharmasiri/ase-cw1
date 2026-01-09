package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.dto.VehicleDTO;
import lk.ase.kavinda.islandlink.entity.RDC;
import lk.ase.kavinda.islandlink.entity.Vehicle;
import lk.ase.kavinda.islandlink.repository.RDCRepository;
import lk.ase.kavinda.islandlink.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RDCRepository rdcRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle createVehicle(VehicleDTO vehicleDTO) {
        // Ensure at least one RDC exists
        ensureDefaultRDCExists();
        
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber(vehicleDTO.getVehicleNumber());
        vehicle.setVehicleType(vehicleDTO.getVehicleType());
        vehicle.setCapacity(vehicleDTO.getCapacity());
        
        // Convert status to active boolean
        vehicle.setActive("AVAILABLE".equals(vehicleDTO.getStatus()));
        
        // Set RDC - use provided RDC or default to first available RDC
        RDC rdc = null;
        if (vehicleDTO.getRdcId() != null) {
            rdc = rdcRepository.findById(vehicleDTO.getRdcId()).orElse(null);
        }
        if (rdc == null) {
            // Get first available RDC as default
            rdc = rdcRepository.findAll().stream().findFirst().orElse(null);
        }
        vehicle.setRdc(rdc);
        
        return vehicleRepository.save(vehicle);
    }

    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findByActiveTrue();
    }

    public void createSampleVehicles() {
        ensureDefaultRDCExists();
        
        if (vehicleRepository.count() == 0) {
            RDC defaultRDC = rdcRepository.findAll().stream().findFirst().orElse(null);
            
            Vehicle vehicle1 = new Vehicle();
            vehicle1.setVehicleNumber("CAB-1234");
            vehicle1.setVehicleType("Truck");
            vehicle1.setCapacity(new BigDecimal("2000.0"));
            vehicle1.setActive(true);
            vehicle1.setRdc(defaultRDC);
            vehicleRepository.save(vehicle1);

            Vehicle vehicle2 = new Vehicle();
            vehicle2.setVehicleNumber("CAB-5678");
            vehicle2.setVehicleType("Van");
            vehicle2.setCapacity(new BigDecimal("800.0"));
            vehicle2.setActive(true);
            vehicle2.setRdc(defaultRDC);
            vehicleRepository.save(vehicle2);
        }
    }
    
    private void ensureDefaultRDCExists() {
        if (rdcRepository.count() == 0) {
            RDC defaultRDC = new RDC();
            defaultRDC.setName("Main RDC");
            defaultRDC.setLocation("Colombo");
            defaultRDC.setAddress("Main Distribution Center, Colombo");
            defaultRDC.setContactNumber("0112345678");
            defaultRDC.setManagerName("Default Manager");
            defaultRDC.setActive(true);
            rdcRepository.save(defaultRDC);
        }
    }
}