package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.Driver;
import lk.ase.kavinda.islandlink.entity.RDC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    List<Driver> findByRdc(RDC rdc);
    List<Driver> findByActiveTrue();
    List<Driver> findByRdcAndActiveTrue(RDC rdc);
    Driver findByLicenseNumber(String licenseNumber);
}