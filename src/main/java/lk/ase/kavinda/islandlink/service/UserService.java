package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.dto.RegisterRequest;
import lk.ase.kavinda.islandlink.entity.PasswordResetToken;
import lk.ase.kavinda.islandlink.entity.Role;
import lk.ase.kavinda.islandlink.entity.User;
import lk.ase.kavinda.islandlink.entity.RDC;
import lk.ase.kavinda.islandlink.repository.PasswordResetTokenRepository;
import lk.ase.kavinda.islandlink.repository.RoleRepository;
import lk.ase.kavinda.islandlink.repository.UserRepository;
import lk.ase.kavinda.islandlink.repository.RDCRepository;
import lk.ase.kavinda.islandlink.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RDCRepository rdcRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    public User registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        Role role;
        Role.RoleName roleName;
        switch (registerRequest.getRole()) {
            case "RETAILER":
                roleName = Role.RoleName.RETAILER;
                break;
            case "RDC_STAFF":
                roleName = Role.RoleName.RDC_STAFF;
                break;
            case "LOGISTICS":
                roleName = Role.RoleName.LOGISTICS;
                break;
            case "HEAD_OFFICE_MANAGER":
                roleName = Role.RoleName.HEAD_OFFICE_MANAGER;
                break;
            default:
                throw new RuntimeException("Invalid role: " + registerRequest.getRole());
        }
        
        role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getFullName(),
                role
        );
        
        // Set customer-specific fields if role is RETAILER
        if ("RETAILER".equals(registerRequest.getRole())) {
            user.setBusinessName(registerRequest.getBusinessName());
            user.setDistrict(registerRequest.getDistrict());
            user.setDeliveryAddress(registerRequest.getDeliveryAddress());
            user.setContactPerson(registerRequest.getContactPerson());
            user.setPhone(registerRequest.getPhone());
            user.setGpsCoordinates(registerRequest.getGpsCoordinates());
            
            if (registerRequest.getBusinessType() != null) {
                user.setBusinessType(User.BusinessType.valueOf(registerRequest.getBusinessType()));
            }
            if (registerRequest.getServicingRdcId() != null) {
                RDC rdc = rdcRepository.findById(registerRequest.getServicingRdcId()).orElse(null);
                user.setServicingRdc(rdc);
            }
            if (registerRequest.getPaymentType() != null) {
                user.setPaymentType(User.PaymentType.valueOf(registerRequest.getPaymentType()));
            }
            if (registerRequest.getCreditLimit() != null) {
                user.setCreditLimit(registerRequest.getCreditLimit());
            }
        }
        
        // Set RDC/Logistics specific fields
        if ("RDC_STAFF".equals(registerRequest.getRole()) || "LOGISTICS".equals(registerRequest.getRole())) {
            if (registerRequest.getServicingRdcId() != null) {
                RDC rdc = rdcRepository.findById(registerRequest.getServicingRdcId()).orElse(null);
                user.setServicingRdc(rdc);
            }
            user.setDepartment(registerRequest.getDepartment());
        }
        
        // Set status
        if (registerRequest.getStatus() != null) {
            user.setStatus(User.UserStatus.valueOf(registerRequest.getStatus()));
        }

        return userRepository.save(user);
    }

    private String generatePONumber() {
        return "PO" + System.currentTimeMillis();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // For now, we'll just delete the user. In production, you might want to add an 'active' field
        userRepository.delete(user);
    }

    public long getTotalUserCount() {
        return userRepository.count();
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        String token = java.util.UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        
        // Delete any existing tokens for this user
        passwordResetTokenRepository.deleteByUser(user);
        passwordResetTokenRepository.save(resetToken);
        
        // Send email with reset link
        emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), token);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));
        
        if (resetToken.isExpired() || resetToken.getUsed()) {
            throw new RuntimeException("Reset token has expired or already been used");
        }
        
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    public void updateProfile(Long userId, String fullName, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already in use");
        }
        
        user.setFullName(fullName);
        user.setEmail(email);
        userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public Role getRoleByName(String roleName) {
        try {
            Role.RoleName roleEnum = Role.RoleName.valueOf(roleName);
            return roleRepository.findByName(roleEnum).orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}