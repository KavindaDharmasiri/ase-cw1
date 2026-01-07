package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.RDC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RDCRepository extends JpaRepository<RDC, Long> {
    List<RDC> findByActiveTrue();
}