package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.GRNItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GRNItemRepository extends JpaRepository<GRNItem, Long> {
}