package org.example.koudynamicpricingbackend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.koudynamicpricingbackend.entities.Flight;
import org.example.koudynamicpricingbackend.entities.Ticket;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    public void sendTicketInfoEmail(String toEmail, String contactName, String pnrCode, Flight flight, List<Ticket> tickets, BigDecimal totalPrice) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Reservation Confirmed! PNR: " + pnrCode + " ✈️");
            helper.setFrom("noreply@kouairlines.com");

            // Tarih ve Saat Formatlayıcılar
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, EEEE"); // 15 Dec 2025, Monday
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm"); // 14:30

            // Yolcu Listesi HTML'ini Dinamik Oluşturma
            StringBuilder passengerRows = new StringBuilder();
            for (Ticket ticket : tickets) {
                passengerRows.append("""
                    <tr style="border-bottom: 1px solid #eee;">
                        <td style="padding: 12px; color: #333;">%s %s</td>
                        <td style="padding: 12px; color: #333; font-weight: bold;">%s</td>
                        <td style="padding: 12px; color: #555;">%s</td>
                    </tr>
                """.formatted(
                        ticket.getPassenger().getFirstName(),
                        ticket.getPassenger().getLastName(),
                        ticket.getSeat().getSeatNumber(),
                        ticket.getPassenger().getBirthDate()
                ));
            }

            // Ana HTML Şablonu
            String htmlContent = """
                <div style="font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1); border: 1px solid #e0e0e0;">
                    
                    <div style="background-color: #0056b3; padding: 25px; text-align: center;">
                        <h1 style="color: #ffffff; margin: 0; font-size: 24px; letter-spacing: 1px;">KOU Airlines</h1>
                        <p style="color: #e0e0e0; margin: 5px 0 0 0; font-size: 14px;">Booking Confirmation</p>
                    </div>

                    <div style="background-color: #f8f9fa; padding: 20px; text-align: center; border-bottom: 1px solid #eee;">
                        <p style="margin: 0; color: #666; font-size: 12px; text-transform: uppercase; letter-spacing: 1px;">Reservation Code (PNR)</p>
                        <h2 style="margin: 5px 0 0 0; color: #0056b3; font-size: 32px; letter-spacing: 3px;">%s</h2>
                        <p style="margin: 5px 0 0 0; color: #28a745; font-weight: bold; font-size: 14px;">✅ Confirmed</p>
                    </div>

                    <div style="padding: 25px;">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                            <div style="text-align: left;">
                                <h3 style="margin: 0; font-size: 24px; color: #333;">%s</h3> <p style="margin: 5px 0 0 0; color: #666; font-size: 14px;">%s</p> <p style="margin: 5px 0 0 0; color: #0056b3; font-weight: bold; font-size: 18px;">%s</p> </div>
                            <div style="text-align: center; color: #999; font-size: 20px;">
                                ✈️ 
                                <div style="font-size: 10px; border-top: 1px dotted #ccc; width: 60px; margin: 5px auto;"></div>
                                <span style="font-size: 12px; color: #666;">%s</span> </div>
                            <div style="text-align: right;">
                                <h3 style="margin: 0; font-size: 24px; color: #333;">%s</h3> <p style="margin: 5px 0 0 0; color: #666; font-size: 14px;">%s</p> <p style="margin: 5px 0 0 0; color: #0056b3; font-weight: bold; font-size: 18px;">%s</p> </div>
                        </div>
                        
                        <div style="text-align: center; background: #f1f5f9; padding: 10px; border-radius: 5px; margin-bottom: 20px;">
                            📅 <strong>Date:</strong> %s
                        </div>

                        <h4 style="margin: 0 0 10px 0; color: #555; border-bottom: 2px solid #0056b3; display: inline-block; padding-bottom: 5px;">Passenger Details</h4>
                        <table style="width: 100%%; border-collapse: collapse; font-size: 14px;">
                            <thead>
                                <tr style="background-color: #f8f9fa; color: #666; text-align: left;">
                                    <th style="padding: 10px;">Name</th>
                                    <th style="padding: 10px;">Seat</th>
                                    <th style="padding: 10px;">Birth Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                %s </tbody>
                        </table>
                    </div>

                    <div style="background-color: #f8f9fa; padding: 15px 25px; border-top: 1px solid #eee; display: flex; justify-content: space-between; align-items: center;">
                        <span style="color: #666; font-weight: bold;">Total Paid:</span>
                        <span style="color: #0056b3; font-size: 20px; font-weight: bold;">$%s</span>
                    </div>

                    <div style="padding: 20px; text-align: center; background-color: #ffffff;">
                        <a href="http://localhost:3000/my-flights" style="display: inline-block; background-color: #0056b3; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 14px;">Manage My Booking</a>
                        <p style="margin-top: 20px; color: #999; font-size: 12px;">
                            © 2025 KOU Airlines. All rights reserved.<br>
                            Umuttepe Campus, Kocaeli, TR
                        </p>
                    </div>
                </div>
            """.formatted(
                    pnrCode,
                    flight.getDepartureAirport().getIataCode(), // IST
                    flight.getDepartureAirport().getCity(),
                    flight.getDepartureTime().format(timeFormatter),
                    "Direct",
                    flight.getArrivalAirport().getIataCode(),   // ESB
                    flight.getArrivalAirport().getCity(),
                    flight.getArrivalTime().format(timeFormatter),
                    flight.getDepartureTime().format(dateFormatter),
                    passengerRows.toString(), // Tablo satırlarını buraya gömüyoruz
                    totalPrice.toString()
            );

            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
            log.info("Rich ticket email sent to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send email to {}", toEmail, e);
        }
    }
}
