package com.expenso.Expenso.service;

/**
 * Service interface for managing all email-related operations.
 *
 * Provides methods for sending OTPs, notifications, and other
 * application emails to users.
 */
public interface EmailService {

  /**
   * Sends an OTP email to the specified recipient.
   *
   * @param toEmail Recipient's email address.
   * @param otp     One-Time Password (OTP) to be sent.
   */
  void sendOtpEmail(String toEmail, String otp);
}
