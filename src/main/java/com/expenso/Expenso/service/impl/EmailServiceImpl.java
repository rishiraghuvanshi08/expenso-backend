package com.expenso.Expenso.service.impl;

import com.expenso.Expenso.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link EmailService}.
 */
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender;

  /**
   * @see EmailService#sendOtpEmail(String, String)
   */
  public void sendOtpEmail(String toEmail, String otp) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(toEmail);
    message.setSubject("Expenso - One-Time Password (OTP) for Registration");

    message.setText(
      "Dear User,\n\n" +
        "Thank you for choosing Expenso.\n\n" +
        "To complete your registration, please use the following One-Time Password (OTP):\n\n" +
        otp + "\n\n" +
        "Once you confirm the OTP, your account will be successfully registered in our system.\n" +
        "You will then be able to log in and begin using our services.\n\n" +
        "If you did not initiate this request, please ignore this email.\n\n" +
        "Best regards,\n" +
        "The Expenso Team"
    );

    mailSender.send(message);
  }
}