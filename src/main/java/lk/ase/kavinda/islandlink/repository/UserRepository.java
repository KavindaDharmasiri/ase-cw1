package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.User;
import lk.ase.kavinda.islandlink.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.role.name = :roleName")
    List<User> findByRoleName(@Param("roleName") Role.RoleName roleName);
    
    @Query("SELECT u FROM User u WHERE u.servicingRdc.id = :rdcId AND u.role.name = :roleName")
    List<User> findByServicingRdcIdAndRoleName(@Param("rdcId") Long rdcId, @Param("roleName") Role.RoleName roleName);
}