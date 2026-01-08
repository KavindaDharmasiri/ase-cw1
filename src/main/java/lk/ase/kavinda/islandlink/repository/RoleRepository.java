package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(Role.RoleName name);

    boolean existsByName(Role.RoleName roleName);
}
