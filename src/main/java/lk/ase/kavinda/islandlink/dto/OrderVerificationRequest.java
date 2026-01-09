package lk.ase.kavinda.islandlink.dto;

import java.util.List;

public class OrderVerificationRequest {
    private Long orderId;
    private String decision; // APPROVE, PARTIAL_APPROVE, REJECT
    private String rejectionReason;
    private List<ItemAdjustment> itemAdjustments;
    private boolean notifyCustomer;

    public OrderVerificationRequest() {}

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public List<ItemAdjustment> getItemAdjustments() { return itemAdjustments; }
    public void setItemAdjustments(List<ItemAdjustment> itemAdjustments) { this.itemAdjustments = itemAdjustments; }
    public boolean isNotifyCustomer() { return notifyCustomer; }
    public void setNotifyCustomer(boolean notifyCustomer) { this.notifyCustomer = notifyCustomer; }

    public static class ItemAdjustment {
        private Long itemId;
        private Integer adjustedQuantity;
        private String reason;

        public ItemAdjustment() {}

        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        public Integer getAdjustedQuantity() { return adjustedQuantity; }
        public void setAdjustedQuantity(Integer adjustedQuantity) { this.adjustedQuantity = adjustedQuantity; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}