package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    
    List<Delivery> findByStatus(Delivery.DeliveryStatus status);
    
    Optional<Delivery> findByOrderId(Long orderId);
    
    @Query("SELECT d FROM Delivery d WHERE d.order.rdcLocation = :rdcLocation")
    List<Delivery> findByRdcLocation(@Param("rdcLocation") String rdcLocation);
    
    @Query("SELECT d FROM Delivery d WHERE d.order.customer.id = :customerId")
    List<Delivery> findByCustomerId(@Param("customerId") Long customerId);
}