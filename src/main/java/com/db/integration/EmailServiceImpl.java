package com.db.integration;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import java.io.UnsupportedEncodingException;

@Service
public class EmailServiceImpl implements EmailService{
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.backend.url}")
    private String backendUrl;

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}") // Or app.email.from if you configured a custom sender
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email asynchronously to avoid blocking the main thread
     * (e.g., making the admin wait while the SMTP server connects).
     */
    @Async
    @Override
    public void sendEmail(String to, String subject, String body, String senderName, String replyTo) {
        try {
            logger.info("Attempting to send email to: {}", to);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setText(body, true); // 'true' enables HTML support
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(fromEmail, senderName);

            if (replyTo != null && !replyTo.isEmpty()) {
                helper.setReplyTo(replyTo);
            }
            mailSender.send(mimeMessage);

            logger.info("Email sent successfully to: {}", to);

        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new IllegalStateException("Failed to send email");
        }
    }
    @Async
    @Override
    public void sendProviderVerificationEmail(Long providerId, String providerName, String categories) throws MessagingException, UnsupportedEncodingException {
        String verificationLink = backendUrl + "/api/admin/providers/verify/" + providerId;

        String subject = "Action Required: New Service Provider Onboarded";
        String htmlBody = "<html><body style='font-family: Arial, sans-serif; color: #333;'>"
                + "<h2>New Service Provider Onboarded</h2>"
                + "<p>Hello Admin,</p>"
                + "<p>A new service provider has completed the onboarding process.</p>"
                + "<ul>"
                + "<li><strong>Name:</strong> " + providerName + "</li>"
                + "<li><strong>Categories:</strong> " + categories + "</li>"
                + "</ul>"
                + "<p>Please review their profile. Click the button below to verify and activate this provider:</p>"
                + "<br>"
                + "<a href='" + verificationLink + "' style='background-color: #4CAF50; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;'>Verify & Activate Provider</a>"
                + "<br><br><p>Thank you,<br>Local Service Finder System</p>"
                + "</body></html>";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setText(htmlBody, true); // 'true' enables HTML support
        helper.setTo(adminEmail);
        helper.setSubject(subject);
        helper.setFrom(fromEmail, "Finder App");

        mailSender.send(mimeMessage);

    }

    // ... existing methods in EmailService ...
    @Async
    @Override
    public void sendProviderWelcomeEmail(String providerEmail, String providerName) throws MessagingException, UnsupportedEncodingException {
        // Change this to your frontend login URL when you build the UI
        String loginLink = backendUrl + "/login";

        String subject = "Account Verified - Welcome to Local Service Finder!";
        String body = "Hello " + providerName + ",\n\n"
                + "Great news! Your service provider account has been successfully verified and approved by our admin team.\n\n"
                + "You can now log in to the portal, manage your schedule, and start accepting bookings from local customers.\n\n"
                + "Access your dashboard here: " + loginLink + "\n\n"
                + "Welcome aboard!\n"
                + "Local Service Finder Team";


        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setText(body, true); // 'true' enables HTML support
        helper.setTo(providerEmail);
        helper.setSubject(subject);
        helper.setFrom(fromEmail, "Finder App");

        mailSender.send(mimeMessage);
    }

    // ... inside EmailService.java ...
    @Async
    @Override
    public void sendBookingNotificationToProvider(String providerEmail, String providerName,
                                                  String customerName, String categoryName,
                                                  String date, String time, Long bookingId) {

        // The link the provider will click to confirm the booking
        String confirmLink = backendUrl + "/api/bookings/confirm/" + bookingId;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(providerEmail);
            helper.setSubject("New Booking Request: " + categoryName);

            String htmlBody = "<html><body style='font-family: Arial, sans-serif; color: #333;'>"
                    + "<h2 style='color: #4CAF50;'>New Booking Request!</h2>"
                    + "<p>Hello " + providerName + ",</p>"
                    + "<p>You have received a new service request. Here are the details:</p>"
                    + "<table style='border-collapse: collapse; width: 100%; max-width: 500px; margin-bottom: 20px;'>"
                    + "<tr><td style='padding: 8px; border-bottom: 1px solid #ddd;'><strong>Customer:</strong></td><td style='padding: 8px; border-bottom: 1px solid #ddd;'>" + customerName + "</td></tr>"
                    + "<tr><td style='padding: 8px; border-bottom: 1px solid #ddd;'><strong>Service:</strong></td><td style='padding: 8px; border-bottom: 1px solid #ddd;'>" + categoryName + "</td></tr>"
                    + "<tr><td style='padding: 8px; border-bottom: 1px solid #ddd;'><strong>Date:</strong></td><td style='padding: 8px; border-bottom: 1px solid #ddd;'>" + date + "</td></tr>"
                    + "<tr><td style='padding: 8px; border-bottom: 1px solid #ddd;'><strong>Time:</strong></td><td style='padding: 8px; border-bottom: 1px solid #ddd;'>" + time + "</td></tr>"
                    + "</table>"
                    + "<p>Please click the button below to accept and confirm this appointment:</p>"
                    + "<a href='" + confirmLink + "' style='background-color: #4CAF50; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;'>Accept Booking</a>"
                    + "<br><br><p>Thank you,<br>Local Service Finder System</p>"
                    + "</body></html>";

            helper.setText(htmlBody, true);

            mailSender.send(message);
            System.out.println("Booking notification email sent to provider successfully.");

        } catch (MessagingException e) {
            System.err.println("Failed to send booking notification email: " + e.getMessage());
        }
    }

    // ... inside EmailService.java ...
    @Async
    @Override
    public void sendBookingConfirmationToCustomer(String customerEmail, String customerName,
                                                  String providerName, String categoryName,
                                                  String date, String time) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(customerEmail);
            helper.setSubject("Booking Confirmed: " + categoryName + " with " + providerName);

            String htmlBody = "<html><body style='font-family: Arial, sans-serif; color: #333;'>"
                    + "<h2 style='color: #4CAF50;'>Your Appointment is Confirmed!</h2>"
                    + "<p>Hello " + customerName + ",</p>"
                    + "<p>Great news! <strong>" + providerName + "</strong> has accepted your booking request.</p>"
                    + "<table style='border-collapse: collapse; width: 100%; max-width: 500px; margin-bottom: 20px;'>"
                    + "<tr><td style='padding: 8px; border-bottom: 1px solid #ddd;'><strong>Service:</strong></td><td style='padding: 8px; border-bottom: 1px solid #ddd;'>" + categoryName + "</td></tr>"
                    + "<tr><td style='padding: 8px; border-bottom: 1px solid #ddd;'><strong>Date:</strong></td><td style='padding: 8px; border-bottom: 1px solid #ddd;'>" + date + "</td></tr>"
                    + "<tr><td style='padding: 8px; border-bottom: 1px solid #ddd;'><strong>Time:</strong></td><td style='padding: 8px; border-bottom: 1px solid #ddd;'>" + time + "</td></tr>"
                    + "</table>"
                    + "<p>The service provider will arrive at your provided address at the scheduled time. You can review your booking details in your dashboard.</p>"
                    + "<br><br><p>Thank you for using our platform!<br>Local Service Finder System</p>"
                    + "</body></html>";

            helper.setText(htmlBody, true);

            mailSender.send(message);
            System.out.println("Confirmation email sent to customer successfully.");

        } catch (MessagingException e) {
            System.err.println("Failed to send customer confirmation email: " + e.getMessage());
        }
    }

}
