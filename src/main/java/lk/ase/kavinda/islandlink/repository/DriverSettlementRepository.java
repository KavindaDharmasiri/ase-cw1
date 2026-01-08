package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.DriverSettlement;
import lk.ase.kavinda.islandlink.entity.Driver;
import lk.ase.kavinda.islandlink.entity.RDC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DriverSettlementRepository extends JpaRepository<DriverSettlement, Long> {
    List<DriverSettlement> findByDriver(Driver driver);
    List<DriverSettlement> findByRdc(RDC rdc);
    List<DriverSettlement> findByStatus(DriverSettlement.SettlementStatus status);
    List<DriverSettlement> findBySettlementDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT MAX(d.settlementNumber) FROM DriverSettlement d WHERE d.settlementNumber LIKE ?1%")
    String findMaxSettlementNumberByPrefix(String prefix);
}