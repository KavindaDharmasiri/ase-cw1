package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrderId(Long orderId);
    List<Delivery> findByDeliveryRouteId(Long id);
    List<Delivery> findByStatus(Delivery.DeliveryStatus status);
    List<Delivery> findByStatusIn(List<Delivery.DeliveryStatus> statuses);
    long countByStatusIn(List<Delivery.DeliveryStatus> statuses);
}
