package com.tutorneo.core.ports.out;

public interface EmailSenderPort {
    void sendEmail(String toEmail, String subject, String textBody, String htmlBody);
}

