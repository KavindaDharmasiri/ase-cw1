package lk.ase.kavinda.islandlink.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "driver_settlements")
public class DriverSettlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String settlementNumber;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "rdc_id", nullable = false)
    private RDC rdc;

    @Column(nullable = false)
    private LocalDateTime settlementDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalCashCollected = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalChequeCollected = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "driverSettlement", cascade = CascadeType.ALL)
    private List<SettlementItem> items;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementStatus status = SettlementStatus.PENDING;

    @Column(length = 1000)
    private String remarks;

    @Column(nullable = false)
    private String settledBy;

    public enum SettlementStatus {
        PENDING, VERIFIED, COMPLETED
    }

    public DriverSettlement() {
        this.settlementDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSettlementNumber() { return settlementNumber; }
    public void setSettlementNumber(String settlementNumber) { this.settlementNumber = settlementNumber; }

    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }

    public RDC getRdc() { return rdc; }
    public void setRdc(RDC rdc) { this.rdc = rdc; }

    public LocalDateTime getSettlementDate() { return settlementDate; }
    public void setSettlementDate(LocalDateTime settlementDate) { this.settlementDate = settlementDate; }

    public BigDecimal getTotalCashCollected() { return totalCashCollected; }
    public void setTotalCashCollected(BigDecimal totalCashCollected) { this.totalCashCollected = totalCashCollected; }

    public BigDecimal getTotalChequeCollected() { return totalChequeCollected; }
    public void setTotalChequeCollected(BigDecimal totalChequeCollected) { this.totalChequeCollected = totalChequeCollected; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public List<SettlementItem> getItems() { return items; }
    public void setItems(List<SettlementItem> items) { this.items = items; }

    public SettlementStatus getStatus() { return status; }
    public void setStatus(SettlementStatus status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getSettledBy() { return settledBy; }
    public void setSettledBy(String settledBy) { this.settledBy = settledBy; }
}