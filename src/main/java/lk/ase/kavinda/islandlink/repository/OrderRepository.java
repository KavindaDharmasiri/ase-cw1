package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByCustomerId(Long customerId);
    
    List<Order> findByRdcLocation(String rdcLocation);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.rdcLocation = :rdcLocation AND o.status = :status")
    List<Order> findByRdcLocationAndStatus(@Param("rdcLocation") String rdcLocation, 
                                          @Param("status") Order.OrderStatus status);
}