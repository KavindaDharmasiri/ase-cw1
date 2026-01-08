package lk.ase.kavinda.islandlink.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "settlement_items")
public class SettlementItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "settlement_id", nullable = false)
    private DriverSettlement driverSettlement;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal invoiceAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal cashCollected = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal chequeCollected = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    private String chequeNumber;

    @Column(length = 500)
    private String remarks;

    public enum PaymentMethod {
        CASH, CHEQUE, CREDIT
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DriverSettlement getDriverSettlement() { return driverSettlement; }
    public void setDriverSettlement(DriverSettlement driverSettlement) { this.driverSettlement = driverSettlement; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public BigDecimal getInvoiceAmount() { return invoiceAmount; }
    public void setInvoiceAmount(BigDecimal invoiceAmount) { this.invoiceAmount = invoiceAmount; }

    public BigDecimal getCashCollected() { return cashCollected; }
    public void setCashCollected(BigDecimal cashCollected) { this.cashCollected = cashCollected; }

    public BigDecimal getChequeCollected() { return chequeCollected; }
    public void setChequeCollected(BigDecimal chequeCollected) { this.chequeCollected = chequeCollected; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getChequeNumber() { return chequeNumber; }
    public void setChequeNumber(String chequeNumber) { this.chequeNumber = chequeNumber; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}