package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.dto.DriverDTO;
import lk.ase.kavinda.islandlink.entity.Driver;
import lk.ase.kavinda.islandlink.entity.RDC;
import lk.ase.kavinda.islandlink.repository.DriverRepository;
import lk.ase.kavinda.islandlink.repository.RDCRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RDCRepository rdcRepository;

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver createDriver(DriverDTO driverDTO) {
        Driver driver = new Driver();
        driver.setName(driverDTO.getName());
        driver.setLicenseNumber(driverDTO.getLicenseNumber());
        driver.setPhone(driverDTO.getPhoneNumber());
        
        // Convert status to active boolean
        driver.setActive("AVAILABLE".equals(driverDTO.getStatus()));
        
        // Set RDC - use provided RDC or default to first available RDC
        RDC rdc = null;
        if (driverDTO.getRdcId() != null) {
            rdc = rdcRepository.findById(driverDTO.getRdcId()).orElse(null);
        }
        if (rdc == null) {
            // Get first available RDC as default
            rdc = rdcRepository.findAll().stream().findFirst().orElse(null);
        }
        driver.setRdc(rdc);
        
        return driverRepository.save(driver);
    }

    public Driver createDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    public List<Driver> getAvailableDrivers() {
        return driverRepository.findByActiveTrue();
    }

    public void createSampleDrivers() {
        if (driverRepository.count() == 0) {
            Driver driver1 = new Driver();
            driver1.setName("Kamal Perera");
            driver1.setLicenseNumber("DL123456");
            driver1.setPhone("0771234567");
            driver1.setActive(true);
            driverRepository.save(driver1);

            Driver driver2 = new Driver();
            driver2.setName("Sunil Silva");
            driver2.setLicenseNumber("DL789012");
            driver2.setPhone("0779876543");
            driver2.setActive(true);
            driverRepository.save(driver2);
        }
    }
}