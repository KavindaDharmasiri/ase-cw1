package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.Vehicle;
import lk.ase.kavinda.islandlink.entity.RDC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByRdc(RDC rdc);
    List<Vehicle> findByActiveTrue();
    List<Vehicle> findByRdcAndActiveTrue(RDC rdc);
    Vehicle findByVehicleNumber(String vehicleNumber);
}