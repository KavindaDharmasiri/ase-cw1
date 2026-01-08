package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.StockTransfer;
import lk.ase.kavinda.islandlink.entity.Product;
import lk.ase.kavinda.islandlink.entity.User;
import lk.ase.kavinda.islandlink.repository.StockTransferRepository;
import lk.ase.kavinda.islandlink.repository.ProductRepository;
import lk.ase.kavinda.islandlink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StockTransferService {

    @Autowired
    private StockTransferRepository stockTransferRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryService inventoryService;

    public List<StockTransfer> getAllTransfers() {
        return stockTransferRepository.findAll();
    }

    public List<StockTransfer> getTransfersByStatus(StockTransfer.TransferStatus status) {
        return stockTransferRepository.findByStatus(status);
    }

    public List<StockTransfer> getTransfersByFromRdc(String fromRdc) {
        return stockTransferRepository.findByFromRdc(fromRdc);
    }

    public List<StockTransfer> getTransfersByToRdc(String toRdc) {
        return stockTransferRepository.findByToRdc(toRdc);
    }

    public Optional<StockTransfer> getTransferById(Long id) {
        return stockTransferRepository.findById(id);
    }

    @Transactional
    public StockTransfer requestTransfer(Long productId, String fromRdc, String toRdc, 
                                       Integer quantity, String reason, Long requestedById) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        User requestedBy = userRepository.findById(requestedById)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StockTransfer transfer = new StockTransfer();
        transfer.setProduct(product);
        transfer.setFromRdc(fromRdc);
        transfer.setToRdc(toRdc);
        transfer.setQuantity(quantity);
        transfer.setReason(reason);
        transfer.setRequestedBy(requestedBy);

        return stockTransferRepository.save(transfer);
    }

    @Transactional
    public StockTransfer approveTransfer(Long transferId, Long approvedById, String notes) {
        StockTransfer transfer = stockTransferRepository.findById(transferId)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));

        User approvedBy = userRepository.findById(approvedById)
                .orElseThrow(() -> new RuntimeException("User not found"));

        transfer.setStatus(StockTransfer.TransferStatus.APPROVED);
        transfer.setApprovedBy(approvedBy);
        transfer.setApprovedDate(LocalDateTime.now());
        transfer.setNotes(notes);

        return stockTransferRepository.save(transfer);
    }

    @Transactional
    public StockTransfer completeTransfer(Long transferId) {
        StockTransfer transfer = stockTransferRepository.findById(transferId)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));

        if (transfer.getStatus() != StockTransfer.TransferStatus.APPROVED) {
            throw new RuntimeException("Transfer must be approved before completion");
        }

        // Perform actual stock transfer
        inventoryService.transferStock(
            transfer.getProduct().getId(),
            1L, // Convert fromRdc string to Long - temporary fix
            2L, // Convert toRdc string to Long - temporary fix
            transfer.getQuantity()
        );

        transfer.setStatus(StockTransfer.TransferStatus.COMPLETED);
        transfer.setCompletedDate(LocalDateTime.now());

        return stockTransferRepository.save(transfer);
    }

    public StockTransfer rejectTransfer(Long transferId, String notes) {
        StockTransfer transfer = stockTransferRepository.findById(transferId)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));

        transfer.setStatus(StockTransfer.TransferStatus.REJECTED);
        transfer.setNotes(notes);

        return stockTransferRepository.save(transfer);
    }
}