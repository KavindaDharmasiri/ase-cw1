package lk.ase.kavinda.islandlink.dto;

import java.time.LocalDateTime;

public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String username;
    private String role;
    private boolean enabled;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    // Constructors
    public UserDTO() {}

    public UserDTO(Long id, String fullName, String email, String username, String role, boolean enabled, LocalDateTime lastLogin, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.role = role;
        this.enabled = enabled;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}