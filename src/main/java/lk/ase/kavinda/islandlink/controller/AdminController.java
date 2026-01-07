package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.entity.AuditLog;
import lk.ase.kavinda.islandlink.entity.StockAlert;
import lk.ase.kavinda.islandlink.entity.User;
import lk.ase.kavinda.islandlink.service.AuditService;
import lk.ase.kavinda.islandlink.service.UserService;
import lk.ase.kavinda.islandlink.repository.StockAlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok("User deactivated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deactivating user: " + e.getMessage());
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