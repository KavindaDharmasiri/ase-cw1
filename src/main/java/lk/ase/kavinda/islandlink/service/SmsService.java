package lk.ase.kavinda.islandlink.service;

import org.springframework.stereotype.Service;

@Service
public class SmsService {

    public void sendOrderConfirmationSms(String phoneNumber, String orderNumber, String customerName) {
        // Mock SMS implementation - replace with actual SMS gateway
        String message = String.format("Dear %s, your order %s has been confirmed. Thank you for choosing IslandLink!", 
                customerName, orderNumber);
        sendSms(phoneNumber, message);
    }

    public void sendDeliveryUpdateSms(String phoneNumber, String orderNumber, String status) {
        String message = String.format("Order %s update: %s. Track your order at http://localhost:4200/track", 
                orderNumber, status);
        sendSms(phoneNumber, message);
    }

    public void sendLowStockAlertSms(String phoneNumber, String productName, int currentStock) {
        String message = String.format("ALERT: %s is running low. Current stock: %d units. Please reorder.", 
                productName, currentStock);
        sendSms(phoneNumber, message);
    }

    public void sendPasswordResetSms(String phoneNumber, String username, String resetCode) {
        String message = String.format("Dear %s, your password reset code is: %s. Valid for 15 minutes.", 
                username, resetCode);
        sendSms(phoneNumber, message);
    }

    private void sendSms(String phoneNumber, String message) {
        // Mock implementation - log the SMS
        System.out.println("SMS to " + phoneNumber + ": " + message);
        
        // In production, integrate with SMS gateway like:
        // - Twilio
        // - AWS SNS
        // - Local SMS provider
        
        // Example Twilio integration:
        // Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        // Message.creator(new PhoneNumber(phoneNumber), new PhoneNumber(FROM_NUMBER), message).create();
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        // Basic validation - starts with + and contains only digits
        return phoneNumber != null && phoneNumber.matches("\\+\\d{10,15}");
    }
}