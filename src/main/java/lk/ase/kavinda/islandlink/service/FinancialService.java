package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.*;
import lk.ase.kavinda.islandlink.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FinancialService {

    @Autowired
    private FinancialLedgerRepository ledgerRepository;
    
    @Autowired
    private DriverSettlementRepository settlementRepository;

    @Transactional
    public void recordSaleTransaction(Order order) {
        BigDecimal totalAmount = order.getTotalAmount();
        
        // Debit: Accounts Receivable (or Cash if cash sale)
        FinancialLedger receivableEntry = new FinancialLedger();
        receivableEntry.setTransactionNumber(generateTransactionNumber());
        receivableEntry.setTransactionType(FinancialLedger.TransactionType.SALE);
        receivableEntry.setAccountType(FinancialLedger.AccountType.ACCOUNTS_RECEIVABLE);
        receivableEntry.setDebitAmount(totalAmount);
        receivableEntry.setDescription("Sale to customer: " + order.getCustomer().getFullName());
        receivableEntry.setReferenceType("ORDER");
        receivableEntry.setReferenceId(order.getId());
        receivableEntry.setRdc(order.getRdc());
        ledgerRepository.save(receivableEntry);
        
        // Credit: Sales Revenue
        FinancialLedger revenueEntry = new FinancialLedger();
        revenueEntry.setTransactionNumber(generateTransactionNumber());
        revenueEntry.setTransactionType(FinancialLedger.TransactionType.SALE);
        revenueEntry.setAccountType(FinancialLedger.AccountType.SALES_REVENUE);
        revenueEntry.setCreditAmount(totalAmount);
        revenueEntry.setDescription("Sale revenue from order: " + order.getId());
        revenueEntry.setReferenceType("ORDER");
        revenueEntry.setReferenceId(order.getId());
        revenueEntry.setRdc(order.getRdc());
        ledgerRepository.save(revenueEntry);
        
        // Record COGS
        recordCostOfGoodsSold(order);
    }

    @Transactional
    public void recordPaymentReceived(Order order, BigDecimal amount, String paymentMethod) {
        // Debit: Cash
        FinancialLedger cashEntry = new FinancialLedger();
        cashEntry.setTransactionNumber(generateTransactionNumber());
        cashEntry.setTransactionType(FinancialLedger.TransactionType.PAYMENT_RECEIVED);
        cashEntry.setAccountType(FinancialLedger.AccountType.CASH);
        cashEntry.setDebitAmount(amount);
        cashEntry.setDescription("Payment received from: " + order.getCustomer().getFullName() + " (" + paymentMethod + ")");
        cashEntry.setReferenceType("ORDER");
        cashEntry.setReferenceId(order.getId());
        cashEntry.setRdc(order.getRdc());
        ledgerRepository.save(cashEntry);
        
        // Credit: Accounts Receivable
        FinancialLedger receivableEntry = new FinancialLedger();
        receivableEntry.setTransactionNumber(generateTransactionNumber());
        receivableEntry.setTransactionType(FinancialLedger.TransactionType.PAYMENT_RECEIVED);
        receivableEntry.setAccountType(FinancialLedger.AccountType.ACCOUNTS_RECEIVABLE);
        receivableEntry.setCreditAmount(amount);
        receivableEntry.setDescription("Payment received for order: " + order.getId());
        receivableEntry.setReferenceType("ORDER");
        receivableEntry.setReferenceId(order.getId());
        receivableEntry.setRdc(order.getRdc());
        ledgerRepository.save(receivableEntry);
    }

    @Transactional
    public void recordPurchaseTransaction(PurchaseOrder purchaseOrder) {
        BigDecimal totalAmount = purchaseOrder.getTotalAmount();
        
        // Debit: Inventory
        FinancialLedger inventoryEntry = new FinancialLedger();
        inventoryEntry.setTransactionNumber(generateTransactionNumber());
        inventoryEntry.setTransactionType(FinancialLedger.TransactionType.PURCHASE);
        inventoryEntry.setAccountType(FinancialLedger.AccountType.INVENTORY);
        inventoryEntry.setDebitAmount(totalAmount);
        inventoryEntry.setDescription("Purchase from: " + purchaseOrder.getSupplier().getName());
        inventoryEntry.setReferenceType("PURCHASE_ORDER");
        inventoryEntry.setReferenceId(purchaseOrder.getId());
        inventoryEntry.setRdc(null); // PurchaseOrder doesn't have RDC field
        ledgerRepository.save(inventoryEntry);
        
        // Credit: Accounts Payable
        FinancialLedger payableEntry = new FinancialLedger();
        payableEntry.setTransactionNumber(generateTransactionNumber());
        payableEntry.setTransactionType(FinancialLedger.TransactionType.PURCHASE);
        payableEntry.setAccountType(FinancialLedger.AccountType.ACCOUNTS_PAYABLE);
        payableEntry.setCreditAmount(totalAmount);
        payableEntry.setDescription("Purchase from: " + purchaseOrder.getSupplier().getName());
        payableEntry.setReferenceType("PURCHASE_ORDER");
        payableEntry.setReferenceId(purchaseOrder.getId());
        payableEntry.setRdc(null); // PurchaseOrder doesn't have RDC field
        ledgerRepository.save(payableEntry);
    }

    public BigDecimal getAccountBalance(FinancialLedger.AccountType accountType) {
        return ledgerRepository.getAccountBalance(accountType);
    }

    public List<FinancialLedger> getLedgerEntries(LocalDateTime startDate, LocalDateTime endDate) {
        return ledgerRepository.findByTransactionDateBetween(startDate, endDate);
    }

    private void recordCostOfGoodsSold(Order order) {
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            return; // No items to process
        }
        
        BigDecimal totalCogs = order.getOrderItems().stream()
                .map(item -> item.getProduct().getPurchasePrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Debit: Cost of Goods Sold
        FinancialLedger cogsEntry = new FinancialLedger();
        cogsEntry.setTransactionNumber(generateTransactionNumber());
        cogsEntry.setTransactionType(FinancialLedger.TransactionType.SALE);
        cogsEntry.setAccountType(FinancialLedger.AccountType.COST_OF_GOODS_SOLD);
        cogsEntry.setDebitAmount(totalCogs);
        cogsEntry.setDescription("COGS for order: " + order.getId());
        cogsEntry.setReferenceType("ORDER");
        cogsEntry.setReferenceId(order.getId());
        cogsEntry.setRdc(order.getRdc());
        ledgerRepository.save(cogsEntry);
        
        // Credit: Inventory
        FinancialLedger inventoryEntry = new FinancialLedger();
        inventoryEntry.setTransactionNumber(generateTransactionNumber());
        inventoryEntry.setTransactionType(FinancialLedger.TransactionType.SALE);
        inventoryEntry.setAccountType(FinancialLedger.AccountType.INVENTORY);
        inventoryEntry.setCreditAmount(totalCogs);
        inventoryEntry.setDescription("Inventory reduction for order: " + order.getId());
        inventoryEntry.setReferenceType("ORDER");
        inventoryEntry.setReferenceId(order.getId());
        inventoryEntry.setRdc(order.getRdc());
        ledgerRepository.save(inventoryEntry);
    }

    @Transactional
    public void recordCashDeposit(DriverSettlement settlement) {
        BigDecimal totalAmount = settlement.getTotalAmount();
        
        // Debit: Cash (Bank)
        FinancialLedger bankEntry = new FinancialLedger();
        bankEntry.setTransactionNumber(generateTransactionNumber());
        bankEntry.setTransactionType(FinancialLedger.TransactionType.PAYMENT_RECEIVED);
        bankEntry.setAccountType(FinancialLedger.AccountType.CASH);
        bankEntry.setDebitAmount(totalAmount);
        bankEntry.setDescription("Cash deposit from driver settlement: " + settlement.getSettlementNumber());
        bankEntry.setReferenceType("SETTLEMENT");
        bankEntry.setReferenceId(settlement.getId());
        bankEntry.setRdc(settlement.getRdc());
        ledgerRepository.save(bankEntry);
        
        // Credit: Cash in Hand (reducing cash in hand)
        FinancialLedger cashHandEntry = new FinancialLedger();
        cashHandEntry.setTransactionNumber(generateTransactionNumber());
        cashHandEntry.setTransactionType(FinancialLedger.TransactionType.TRANSFER);
        cashHandEntry.setAccountType(FinancialLedger.AccountType.CASH);
        cashHandEntry.setCreditAmount(totalAmount);
        cashHandEntry.setDescription("Cash in hand deposited: " + settlement.getSettlementNumber());
        cashHandEntry.setReferenceType("SETTLEMENT");
        cashHandEntry.setReferenceId(settlement.getId());
        cashHandEntry.setRdc(settlement.getRdc());
        ledgerRepository.save(cashHandEntry);
    }

    private String generateTransactionNumber() {
        return "TXN" + System.currentTimeMillis();
    }
}