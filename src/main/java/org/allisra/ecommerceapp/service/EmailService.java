// src/main/java/org/allisra/ecommerceapp/service/EmailService.java

package org.allisra.ecommerceapp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.allisra.ecommerceapp.model.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.base-url:http://localhost:3000}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendVerificationEmail(User user, String token) {
        try {
            log.debug("Preparing verification email for user: {}", user.getEmail());

            String verificationUrl = baseUrl + "/verify-email?token=" + token;
            log.debug("Verification URL: {}", verificationUrl);

            Context context = new Context();
            context.setVariable("name", user.getFirstName());
            context.setVariable("verificationUrl", verificationUrl);

            log.debug("Processing email template...");
            String emailContent = templateEngine.process("email/verification-email", context);

            log.debug("Sending email to {} from {}", user.getEmail(), fromEmail);
            sendEmail(user.getEmail(), "Please verify your email", emailContent);

            log.info("Verification email sent successfully to: {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("SMTP Error sending email to {}: {}", user.getEmail(), e.getMessage());
            log.error("SMTP Error details:", e);
            throw new RuntimeException("Failed to send verification email: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error sending email to {}: {}", user.getEmail(), e.getMessage());
            log.error("Error details:", e);
            throw new RuntimeException("Failed to send verification email: " + e.getMessage(), e);
        }
    }

    @Async
    public void sendPasswordResetEmail(User user, String token) {
        try {
            String resetUrl = baseUrl + "/reset-password?token=" + token;
            Context context = new Context();
            context.setVariable("name", user.getFirstName());
            context.setVariable("resetUrl", resetUrl);

            String emailContent = templateEngine.process("email/password-reset", context);
            sendEmail(user.getEmail(), "Password Reset Request", emailContent);

            log.info("Password reset email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    @Async
    public void sendWelcomeEmail(User user) {
        try {
            Context context = new Context();
            context.setVariable("name", user.getFirstName());

            String emailContent = templateEngine.process("email/welcome", context);
            sendEmail(user.getEmail(), "Welcome to Our E-Commerce Platform", emailContent);

            log.info("Welcome email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    @Async
    public void sendOrderConfirmationEmail(User user, Long orderId) {
        try {
            String orderUrl = baseUrl + "/orders/" + orderId;
            Context context = new Context();
            context.setVariable("name", user.getFirstName());
            context.setVariable("orderId", orderId);
            context.setVariable("orderUrl", orderUrl);

            String emailContent = templateEngine.process("email/order-confirmation", context);
            sendEmail(user.getEmail(), "Order Confirmation #" + orderId, emailContent);

            log.info("Order confirmation email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send order confirmation email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    private void sendEmail(String to, String subject, String content) throws MessagingException {
        try {
            log.debug("Creating MimeMessage...");
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            log.debug("Attempting to send email...");
            mailSender.send(message);
            log.debug("Email sent successfully");
        } catch (MessagingException e) {
            log.error("Failed to send email. SMTP Configuration: host={}, port={}, username={}",
                    mailSender.toString(), fromEmail);
            throw e;
        }
    }
}