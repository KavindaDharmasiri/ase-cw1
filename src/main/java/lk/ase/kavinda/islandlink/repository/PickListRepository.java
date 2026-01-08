package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.PickList;
import lk.ase.kavinda.islandlink.entity.RDC;
import lk.ase.kavinda.islandlink.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PickListRepository extends JpaRepository<PickList, Long> {
    List<PickList> findByRdc(RDC rdc);
    List<PickList> findByDriver(Driver driver);
    List<PickList> findByStatus(PickList.PickListStatus status);
    List<PickList> findByCreatedDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT MAX(p.pickListNumber) FROM PickList p WHERE p.pickListNumber LIKE ?1%")
    String findMaxPickListNumberByPrefix(String prefix);
}