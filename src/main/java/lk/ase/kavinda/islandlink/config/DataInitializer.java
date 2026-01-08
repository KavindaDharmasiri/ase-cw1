package lk.ase.kavinda.islandlink.config;

import lk.ase.kavinda.islandlink.entity.Role;
import lk.ase.kavinda.islandlink.entity.User;
import lk.ase.kavinda.islandlink.repository.RoleRepository;
import lk.ase.kavinda.islandlink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create default HO manager if none exists
        if (!userRepository.existsByUsername("admin")) {
            Role hoRole = roleRepository.findByName(Role.RoleName.HEAD_OFFICE_MANAGER)
                    .orElseThrow(() -> new RuntimeException("HEAD_OFFICE_MANAGER role not found"));

            User admin = new User(
                    "admin",
                    "admin@islandlink.lk",
                    passwordEncoder.encode("admin123"),
                    "System Administrator",
                    hoRole
            );

            userRepository.save(admin);
            System.out.println("Default admin user created: admin/admin123");
        }
    }
}