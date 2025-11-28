package org.example.koudynamicpricingbackend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Async // Ensures this runs in a separate thread (does not block the user response)
    public void sendWelcomeEmail(String toEmail, String firstName) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            // 'true' indicates this is a multipart message (supports HTML content)
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Welcome to KOU Airlines! ✈️");
            helper.setFrom("noreply@kouairlines.com"); // The visible sender name

            // Professional HTML Template using Java Text Blocks
            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;">
                    <h2 style="color: #0056b3; text-align: center;">Welcome to KOU Airlines</h2>
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Thank you for joining the <strong>KOU Dynamic Pricing System</strong>. We are thrilled to have you on board.</p>
                    <p>To start using our AI-powered flight search engine, please verify your account by clicking the button below:</p>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="http://localhost:3000/login" style="background-color: #0056b3; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; font-weight: bold;">
                            Go to Login Page
                        </a>
                    </div>
                    
                    <p style="color: #666; font-size: 14px;">If you did not create this account, please ignore this email.</p>
                    <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
                    <p style="text-align: center; color: #999; font-size: 12px;">
                        © 2025 Kocaeli University - Dynamic Pricing Project<br>
                        Umuttepe Campus, Kocaeli
                    </p>
                </div>
                """.formatted(firstName); // Injects the user's name into the %s placeholder

            // The 'true' argument tells the mail sender to render this as HTML
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("Welcome email sent successfully to: {}", toEmail);

        } catch (MessagingException e) {
            // Since this is async, we cannot throw the exception to the controller.
            // We must log it here to debug issues later.
            log.error("Failed to send email to {}", toEmail, e);
        }
    }
}
