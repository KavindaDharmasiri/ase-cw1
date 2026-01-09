package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByActiveTrue();
    List<Vehicle> findByVehicleType(String vehicleType);
}