package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Long> {
    @Query("SELECT r FROM Return r JOIN Order o ON r.orderId = o.id WHERE o.customer.id = ?1")
    List<Return> findByUserId(Long userId);
}