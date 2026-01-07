package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.dto.JwtResponse;
import lk.ase.kavinda.islandlink.dto.LoginRequest;
import lk.ase.kavinda.islandlink.dto.RegisterRequest;
import lk.ase.kavinda.islandlink.entity.User;
import lk.ase.kavinda.islandlink.security.JwtUtils;
import lk.ase.kavinda.islandlink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.registerUser(registerRequest);
            return ResponseEntity.ok("User registered successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("Login attempt for username: " + loginRequest.getUsername());
            
            User user = userService.findByUsername(loginRequest.getUsername());
            if (user == null) {
                System.out.println("User not found: " + loginRequest.getUsername());
                return ResponseEntity.badRequest().body("User not found");
            }
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            String jwt = jwtUtils.generateJwtToken(loginRequest.getUsername());

            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().getName().toString()
            ));
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            userService.requestPasswordReset(request.getEmail());
            return ResponseEntity.ok("Password reset email sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Password reset successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId, @RequestBody UpdateProfileRequest request) {
        try {
            userService.updateProfile(userId, request.getFullName(), request.getEmail());
            return ResponseEntity.ok("Profile updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtUtils.validateJwtToken(token)) {
                    String username = jwtUtils.getUserNameFromJwtToken(token);
                    User user = userService.findByUsername(username);
                    if (user != null) {
                        return ResponseEntity.ok(Map.of(
                            "valid", true,
                            "username", user.getUsername(),
                            "role", user.getRole().getName().toString()
                        ));
                    }
                }
            }
            return ResponseEntity.status(401).body(Map.of("valid", false));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("valid", false));
        }
    }

    public static class ForgotPasswordRequest {
        private String email;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ResetPasswordRequest {
        private String token;
        private String newPassword;
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class UpdateProfileRequest {
        private String fullName;
        private String email;
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}