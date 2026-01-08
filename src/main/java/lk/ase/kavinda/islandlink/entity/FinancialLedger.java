package lk.ase.kavinda.islandlink.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_ledger")
public class FinancialLedger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transactionNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal debitAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal creditAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String referenceType;

    @Column(nullable = false)
    private Long referenceId;

    @ManyToOne
    @JoinColumn(name = "rdc_id")
    private RDC rdc;

    public enum TransactionType {
        SALE, PURCHASE, PAYMENT_RECEIVED, PAYMENT_MADE, INVENTORY_ADJUSTMENT, TRANSFER
    }

    public enum AccountType {
        SALES_REVENUE, COST_OF_GOODS_SOLD, ACCOUNTS_RECEIVABLE, ACCOUNTS_PAYABLE, 
        CASH, INVENTORY, DELIVERY_EXPENSE, ADMINISTRATIVE_EXPENSE
    }

    public FinancialLedger() {
        this.transactionDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTransactionNumber() { return transactionNumber; }
    public void setTransactionNumber(String transactionNumber) { this.transactionNumber = transactionNumber; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }

    public BigDecimal getDebitAmount() { return debitAmount; }
    public void setDebitAmount(BigDecimal debitAmount) { this.debitAmount = debitAmount; }

    public BigDecimal getCreditAmount() { return creditAmount; }
    public void setCreditAmount(BigDecimal creditAmount) { this.creditAmount = creditAmount; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public RDC getRdc() { return rdc; }
    public void setRdc(RDC rdc) { this.rdc = rdc; }
}