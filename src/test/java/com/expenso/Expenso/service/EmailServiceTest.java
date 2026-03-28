package com.expenso.Expenso.service;

import com.expenso.Expenso.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

  @Mock
  private JavaMailSender mailSender;

  @InjectMocks
  private EmailServiceImpl emailService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void sendOtpEmail_shouldSendEmailSuccessfully() {
    String email = "rishi@gmail.com";
    String otp = "123456";

    emailService.sendOtpEmail(email, otp);

    ArgumentCaptor<SimpleMailMessage> messageCaptor =
      ArgumentCaptor.forClass(SimpleMailMessage.class);

    verify(mailSender, times(1)).send(messageCaptor.capture());

    SimpleMailMessage capturedMessage = messageCaptor.getValue();

    assertThat(capturedMessage.getTo()).contains(email);
    assertThat(capturedMessage.getSubject())
      .isEqualTo("Expenso - One-Time Password (OTP) for Registration");

    assertThat(capturedMessage.getText()).contains(otp);
    assertThat(capturedMessage.getText()).contains("Thank you for choosing Expenso");
    assertThat(capturedMessage.getText()).contains("The Expenso Team");
  }
}