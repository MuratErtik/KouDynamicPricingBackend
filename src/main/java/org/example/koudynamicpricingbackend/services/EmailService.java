package org.example.koudynamicpricingbackend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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

    @Async
    public void sendTicketInfoEmail(String toEmail, String contactName, String pnrCode, String flightInfo, BigDecimal totalPrice) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Reservation Confirmed! PNR: " + pnrCode + " ✈️");
            helper.setFrom("noreply@kouairlines.com");

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #ffffff;">
                    
                    <div style="text-align: center; padding-bottom: 20px; border-bottom: 2px solid #0056b3;">
                        <h2 style="color: #0056b3; margin: 0;">KOU Airlines</h2>
                        <p style="color: #666; font-size: 14px; margin-top: 5px;">Your Journey Begins Here</p>
                    </div>

                    <div style="padding: 20px 0;">
                        <p>Dear <strong>%s</strong>,</p>
                        <p>Your flight reservation has been successfully confirmed. Below are your flight details.</p>
                    </div>

                    <div style="background-color: #f8f9fa; border-left: 5px solid #0056b3; padding: 15px; margin: 20px 0;">
                        <p style="margin: 0; font-size: 12px; color: #666; text-transform: uppercase;">Reservation Code (PNR)</p>
                        <p style="margin: 5px 0 0 0; font-size: 24px; font-weight: bold; color: #333; letter-spacing: 2px;">%s</p>
                    </div>

                    <table style="width: 100%%; border-collapse: collapse; margin-bottom: 20px;">
                        <tr style="background-color: #f1f1f1;">
                            <td style="padding: 10px; font-weight: bold; color: #555;">Flight Route:</td>
                            <td style="padding: 10px; color: #333;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 10px; font-weight: bold; color: #555; border-bottom: 1px solid #eee;">Total Price:</td>
                            <td style="padding: 10px; color: #333; border-bottom: 1px solid #eee;">$%s</td>
                        </tr>
                    </table>

                    <p style="text-align: center; margin-top: 30px;">
                        <a href="http://localhost:3000/my-flights" style="background-color: #28a745; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; font-weight: bold;">
                            Manage Booking
                        </a>
                    </p>

                    <hr style="border: 0; border-top: 1px solid #eee; margin: 30px 0;">
                    <p style="text-align: center; color: #999; font-size: 12px;">
                        © 2025 KOU Airlines Dynamic Pricing Project<br>
                        Have a safe flight!
                    </p>
                </div>
                """.formatted(contactName, pnrCode, flightInfo, totalPrice.toString());

            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("Ticket confirmation email sent successfully to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send ticket email to {}", toEmail, e);
        }
    }
}
