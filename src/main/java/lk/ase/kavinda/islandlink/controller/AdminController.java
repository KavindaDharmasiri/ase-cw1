package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.dto.UserDTO;
import lk.ase.kavinda.islandlink.entity.AuditLog;
import lk.ase.kavinda.islandlink.entity.Role;
import lk.ase.kavinda.islandlink.entity.StockAlert;
import lk.ase.kavinda.islandlink.entity.User;
import lk.ase.kavinda.islandlink.service.AuditService;
import lk.ase.kavinda.islandlink.service.UserService;
import lk.ase.kavinda.islandlink.repository.StockAlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    @Autowired
    private AuditService auditService;

    @Autowired
    private UserService userService;

    @Autowired
    private StockAlertRepository stockAlertRepository;

    @GetMapping("/audit-logs")
    public List<AuditLog> getAuditLogs() {
        return auditService.getAuditLogs();
    }

    @GetMapping("/audit-logs/user/{username}")
    public List<AuditLog> getAuditLogsByUser(@PathVariable String username) {
        return auditService.getAuditLogsByUser(username);
    }

    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(user -> new UserDTO(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getRole().getName().name(),
                    true, // enabled
                    null, // lastLogin
                    null, // createdAt
                    user.getBusinessName(),
                    user.getDistrict(),
                    user.getServicingRdc() != null ? user.getServicingRdc().getId() : null,
                    user.getPaymentType() != null ? user.getPaymentType().name() : null,
                    user.getCreditLimit(),
                    user.getOutstandingBalance()
                ))
                .collect(Collectors.toList());
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            user.setFullName(userDTO.getFullName());
            user.setEmail(userDTO.getEmail());
            user.setUsername(userDTO.getUsername());
            
            // Update role if changed
            if (!user.getRole().getName().name().equals(userDTO.getRole())) {
                Role newRole = userService.getRoleByName(userDTO.getRole());
                if (newRole != null) {
                    user.setRole(newRole);
                }
            }
            
            User updatedUser = userService.updateUser(user);
            
            UserDTO responseDTO = new UserDTO(
                updatedUser.getId(),
                updatedUser.getFullName(),
                updatedUser.getEmail(),
                updatedUser.getUsername(),
                updatedUser.getRole().getName().name(),
                true,
                null,
                null,
                updatedUser.getBusinessName(),
                updatedUser.getDistrict(),
                updatedUser.getServicingRdc() != null ? updatedUser.getServicingRdc().getId() : null,
                updatedUser.getPaymentType() != null ? updatedUser.getPaymentType().name() : null,
                updatedUser.getCreditLimit(),
                updatedUser.getOutstandingBalance()
            );
            
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/stock-alerts")
    public List<StockAlert> getActiveStockAlerts() {
        return stockAlertRepository.findByStatusOrderByCreatedAtDesc(StockAlert.AlertStatus.ACTIVE);
    }

    @PutMapping("/stock-alerts/{id}/resolve")
    public ResponseEntity<?> resolveStockAlert(@PathVariable Long id) {
        try {
            StockAlert alert = stockAlertRepository.findById(id).orElse(null);
            if (alert != null) {
                alert.setStatus(StockAlert.AlertStatus.RESOLVED);
                alert.setResolvedAt(LocalDateTime.now());
                stockAlertRepository.save(alert);
                return ResponseEntity.ok("Alert resolved");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error resolving alert: " + e.getMessage());
        }
    }

    @GetMapping("/system-stats")
    public ResponseEntity<?> getSystemStats() {
        try {
            long totalUsers = userService.getTotalUserCount();
            long activeAlerts = stockAlertRepository.findByStatusOrderByCreatedAtDesc(StockAlert.AlertStatus.ACTIVE).size();
            
            return ResponseEntity.ok(new SystemStats(totalUsers, activeAlerts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting system stats: " + e.getMessage());
        }
    }

    public static class SystemStats {
        public long totalUsers;
        public long activeAlerts;

        public SystemStats(long totalUsers, long activeAlerts) {
            this.totalUsers = totalUsers;
            this.activeAlerts = activeAlerts;
        }
    }
}
