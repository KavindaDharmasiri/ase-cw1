package lk.ase.kavinda.islandlink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderConfirmation(String to, String orderNumber, String customerName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Order Confirmation - " + orderNumber);
        message.setText("Dear " + customerName + ",\n\n" +
                "Your order " + orderNumber + " has been confirmed and is being processed.\n\n" +
                "Thank you for choosing IslandLink!\n\n" +
                "Best regards,\nIslandLink Team");
        mailSender.send(message);
    }

    public void sendDeliveryUpdate(String to, String orderNumber, String status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Delivery Update - " + orderNumber);
        message.setText("Your order " + orderNumber + " status: " + status + "\n\n" +
                "Track your order in real-time through our portal.\n\n" +
                "Best regards,\nIslandLink Team");
        mailSender.send(message);
    }

    public void sendInvoice(String to, String invoiceNumber, String customerName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Invoice - " + invoiceNumber);
        message.setText("Dear " + customerName + ",\n\n" +
                "Please find your invoice " + invoiceNumber + " attached.\n\n" +
                "Best regards,\nIslandLink Team");
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String username, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset - IslandLink");
        message.setText("Dear " + username + ",\n\n" +
                "You have requested a password reset. Click the link below to reset your password:\n\n" +
                "http://localhost:4200/reset-password?token=" + token + "\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Best regards,\nIslandLink Team");
        mailSender.send(message);
    }
}