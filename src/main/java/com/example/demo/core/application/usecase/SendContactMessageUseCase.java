package com.example.demo.core.application.usecase;

import java.time.Instant;
import java.util.Objects;

import com.example.demo.core.ports.in.SendContactMessagePort;
import com.example.demo.core.ports.out.EmailSenderPort;

public class SendContactMessageUseCase implements SendContactMessagePort {

    private static final int MAX_NAME_LENGTH = 120;
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MAX_MESSAGE_LENGTH = 4000;

    private final EmailSenderPort emailSenderPort;
    private final String toEmail;

    public SendContactMessageUseCase(EmailSenderPort emailSenderPort, String toEmail) {
        this.emailSenderPort = Objects.requireNonNull(emailSenderPort, "emailSenderPort es requerido");
        this.toEmail = requireNonBlank(toEmail, "toEmail", MAX_EMAIL_LENGTH);
    }

    @Override
    public void send(String name, String email, String message) {
        String safeName = requireNonBlank(name, "name", MAX_NAME_LENGTH);
        String safeEmail = requireNonBlank(email, "email", MAX_EMAIL_LENGTH);
        String safeMessage = requireNonBlank(message, "message", MAX_MESSAGE_LENGTH);

        Instant now = Instant.now();
        String subject = "Nuevo mensaje de contacto: " + safeName;

        String textBody = buildTextBody(safeName, safeEmail, safeMessage, now);
        String htmlBody = buildHtmlBody(safeName, safeEmail, safeMessage, now);

        emailSenderPort.sendEmail(toEmail, subject, textBody, htmlBody);
    }

    private String buildTextBody(String name, String email, String message, Instant now) {
        return """
                Nuevo mensaje desde el formulario de contacto

                Nombre: %s
                Email: %s
                Fecha: %s

                Mensaje:
                %s
                """.formatted(name, email, now, message);
    }

    private String buildHtmlBody(String name, String email, String message, Instant now) {
        String escapedName = escapeHtml(name);
        String escapedEmail = escapeHtml(email);
        String escapedMessage = escapeHtml(message)
                .replace("\r\n", "\n")
                .replace("\n", "<br/>");

        return """
                <h2>Nuevo mensaje de contacto</h2>
                <p><b>Nombre:</b> %s</p>
                <p><b>Email:</b> <a href="mailto:%s">%s</a></p>
                <p><b>Fecha:</b> %s</p>
                <p><b>Mensaje:</b><br/>%s</p>
                """.formatted(escapedName, escapedEmail, escapedEmail, escapeHtml(now.toString()), escapedMessage);
    }

    private String requireNonBlank(String value, String field, int maxLen) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " es requerido");
        }
        String trimmed = value.trim();
        if (trimmed.length() > maxLen) {
            throw new IllegalArgumentException(field + " no puede tener mas de " + maxLen + " caracteres");
        }
        return trimmed;
    }

    private String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        StringBuilder out = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '&' -> out.append("&amp;");
                case '<' -> out.append("&lt;");
                case '>' -> out.append("&gt;");
                case '"' -> out.append("&quot;");
                case '\'' -> out.append("&#39;");
                default -> out.append(c);
            }
        }
        return out.toString();
    }
}

