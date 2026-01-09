package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.Order;
import lk.ase.kavinda.islandlink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByRdcLocation(String rdcLocation);

    List<Order> findByStatus(Order.OrderStatus status);

    List<Order> findByOrderDateAfter(LocalDateTime date);

    long countByOrderDateAfter(LocalDateTime date);

    @Query("SELECT o FROM Order o WHERE o.rdcLocation = :rdcLocation AND o.status = :status")
    List<Order> findByRdcLocationAndStatus(@Param("rdcLocation") String rdcLocation,
                                          @Param("status") Order.OrderStatus status);

    List<Order> findByCustomerAndStatus(User customer, Order.OrderStatus status);
    
    List<Order> findByRdcId(Long rdcId);
    
    List<Order> findByStatusAndRdcId(Order.OrderStatus status, Long rdcId);

    List<Order> findByStatusAndDeliveryRouteIsNull(Order.OrderStatus status);

    List<Order> findByDeliveryRouteId(Long deliveryRouteId);

    @Query("SELECT d.order FROM Delivery d WHERE d.deliveryRoute.id = :routeId AND d.order.status = :status")
    List<Order> findByDeliveryRouteIdAndStatus(@Param("routeId") Long routeId, @Param("status") Order.OrderStatus status);
}
