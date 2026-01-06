package lk.ase.kavinda.islandlink.config;

import lk.ase.kavinda.islandlink.entity.Role;
import lk.ase.kavinda.islandlink.entity.User;
import lk.ase.kavinda.islandlink.entity.Product;
import lk.ase.kavinda.islandlink.entity.Inventory;
import lk.ase.kavinda.islandlink.repository.RoleRepository;
import lk.ase.kavinda.islandlink.repository.UserRepository;
import lk.ase.kavinda.islandlink.repository.ProductRepository;
import lk.ase.kavinda.islandlink.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(Role.RoleName.RETAILER));
            roleRepository.save(new Role(Role.RoleName.RDC_STAFF));
            roleRepository.save(new Role(Role.RoleName.LOGISTICS));
            roleRepository.save(new Role(Role.RoleName.HEAD_OFFICE_MANAGER));
        }
        
        if (userRepository.count() == 0) {
            Role retailerRole = roleRepository.findByName(Role.RoleName.RETAILER).orElse(null);
            if (retailerRole != null) {
                User testUser = new User(
                    "kavinda",
                    "kavinda@test.com",
                    passwordEncoder.encode("Akkgdkavinda1#"),
                    "Kavinda Test",
                    retailerRole
                );
                userRepository.save(testUser);
            }
        }
        
        // Initialize inventory for existing products
        if (inventoryRepository.count() == 0) {
            for (Product product : productRepository.findAll()) {
                // Create inventory for multiple RDCs
                inventoryRepository.save(new Inventory(product, "Colombo", 100));
                inventoryRepository.save(new Inventory(product, "Kandy", 50));
                inventoryRepository.save(new Inventory(product, "Galle", 75));
                inventoryRepository.save(new Inventory(product, "Jaffna", 25));
            }
        }
    }
}