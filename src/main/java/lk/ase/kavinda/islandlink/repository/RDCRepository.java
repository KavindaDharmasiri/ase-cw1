package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.RDC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RDCRepository extends JpaRepository<RDC, Long> {
    List<RDC> findByActiveTrue();
    Optional<RDC> findByName(String name);
}