package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.DeliveryRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeliveryRouteRepository extends JpaRepository<DeliveryRoute, Long> {
    
    List<DeliveryRoute> findByStatus(DeliveryRoute.RouteStatus status);
    
    List<DeliveryRoute> findByRdcLocation(String rdcLocation);
    
    List<DeliveryRoute> findByDriverName(String driverName);
    
    List<DeliveryRoute> findByVehicleNumber(String vehicleNumber);
    
    List<DeliveryRoute> findByScheduledDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT dr FROM DeliveryRoute dr WHERE dr.status IN :statuses")
    List<DeliveryRoute> findByStatusIn(@Param("statuses") List<DeliveryRoute.RouteStatus> statuses);
    
    @Query("SELECT COUNT(dr) FROM DeliveryRoute dr WHERE dr.status = :status")
    long countByStatus(@Param("status") DeliveryRoute.RouteStatus status);
    
    @Query("SELECT dr FROM DeliveryRoute dr WHERE dr.scheduledDate >= :date AND dr.status != 'COMPLETED'")
    List<DeliveryRoute> findActiveRoutesFromDate(@Param("date") LocalDateTime date);
}