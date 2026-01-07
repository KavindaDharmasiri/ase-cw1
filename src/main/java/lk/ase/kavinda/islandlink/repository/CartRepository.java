package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.Cart;
import lk.ase.kavinda.islandlink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);
    Optional<Cart> findByUserAndProductId(User user, Long productId);
    
    @Modifying
    @Transactional
    void deleteByUser(User user);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.user = ?1 AND c.product.id = ?2")
    void deleteByUserAndProductId(User user, Long productId);
}