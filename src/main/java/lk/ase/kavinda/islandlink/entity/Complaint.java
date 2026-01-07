package lk.ase.kavinda.islandlink.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String category;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status;

    @Column(name = "submit_date", nullable = false)
    private LocalDateTime submitDate;

    @Column(name = "resolved_date")
    private LocalDateTime resolvedDate;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    public enum ComplaintStatus {
        PENDING, IN_PROGRESS, RESOLVED, CLOSED
    }

    @PrePersist
    protected void onCreate() {
        submitDate = LocalDateTime.now();
        status = ComplaintStatus.PENDING;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }

    public LocalDateTime getSubmitDate() { return submitDate; }
    public void setSubmitDate(LocalDateTime submitDate) { this.submitDate = submitDate; }

    public LocalDateTime getResolvedDate() { return resolvedDate; }
    public void setResolvedDate(LocalDateTime resolvedDate) { this.resolvedDate = resolvedDate; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
}