package com.tutorneo.adapters.out.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.tutorneo.core.ports.out.EmailSenderPort;

@Component
@ConditionalOnProperty(name = "mailjet.enabled", havingValue = "false", matchIfMissing = true)
public class NoopEmailAdapter implements EmailSenderPort {

    private static final Logger log = LoggerFactory.getLogger(NoopEmailAdapter.class);

    @Override
    public void sendEmail(String toEmail, String subject, String textBody, String htmlBody) {
        log.warn("Email sending is disabled (no Mailjet configuration provided). Skipping email to {}", toEmail);
    }
}
