package lk.ase.kavinda.islandlink.repository;

import lk.ase.kavinda.islandlink.entity.FinancialLedger;
import lk.ase.kavinda.islandlink.entity.RDC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FinancialLedgerRepository extends JpaRepository<FinancialLedger, Long> {
    List<FinancialLedger> findByTransactionType(FinancialLedger.TransactionType transactionType);
    List<FinancialLedger> findByAccountType(FinancialLedger.AccountType accountType);
    List<FinancialLedger> findByRdc(RDC rdc);
    List<FinancialLedger> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
    List<FinancialLedger> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);
    
    @Query("SELECT SUM(f.debitAmount - f.creditAmount) FROM FinancialLedger f WHERE f.accountType = ?1")
    BigDecimal getAccountBalance(FinancialLedger.AccountType accountType);
}