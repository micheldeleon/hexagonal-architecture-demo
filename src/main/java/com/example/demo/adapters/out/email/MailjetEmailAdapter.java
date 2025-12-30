package com.example.demo.adapters.out.email;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.example.demo.core.ports.out.EmailSenderPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
@ConditionalOnProperty(name = "mailjet.enabled", havingValue = "true")
public class MailjetEmailAdapter implements EmailSenderPort {

    private static final Logger log = LoggerFactory.getLogger(MailjetEmailAdapter.class);
    private static final URI MAILJET_URI = URI.create("https://api.mailjet.com/v3.1/send");
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(20);
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_MS = 750;
    private static final String PROP_API_KEY = "mailjet.api.apikey";
    private static final String PROP_SECRET_KEY = "mailjet.api.secretkey";
    private static final String PROP_FROM_EMAIL = "mailjet.from.email";
    private static final String PROP_FROM_NAME = "mailjet.from.name";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String apiSecret;
    private final String defaultFromEmail;
    private final String defaultFromName;

    public MailjetEmailAdapter(ObjectMapper objectMapper, Environment environment) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(REQUEST_TIMEOUT)
                .build();
        this.apiKey = requireProperty(environment, PROP_API_KEY);
        this.apiSecret = requireProperty(environment, PROP_SECRET_KEY);
        this.defaultFromEmail = requireProperty(environment, PROP_FROM_EMAIL);
        this.defaultFromName = Optional.ofNullable(environment.getProperty(PROP_FROM_NAME))
                .filter(name -> !name.isBlank())
                .orElse(this.defaultFromEmail);
    }

    @Override
    public void sendEmail(String toEmail, String subject, String textBody, String htmlBody) {
        sendEmail(defaultFromEmail, defaultFromName, toEmail, subject, textBody, htmlBody, null, Map.of());
    }

    public void sendEmail(String fromEmail, String fromName, String toEmail, String subject,
            String textBody, String htmlBody, Long templateId, Map<String, Object> variables) {
        Objects.requireNonNull(fromEmail, "fromEmail is required");
        Objects.requireNonNull(toEmail, "toEmail is required");
        String payload = buildPayload(fromEmail, Optional.ofNullable(fromName).orElse(fromEmail), toEmail, subject,
                textBody, htmlBody, templateId, variables);
        executeWithRetry(payload);
    }

    private void executeWithRetry(String payload) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(MAILJET_URI)
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/json")
                .header("Authorization", buildBasicAuthHeader())
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        long backoff = INITIAL_BACKOFF_MS;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                int status = response.statusCode();
                if (status >= 200 && status < 300) {
                    logSuccess(response.body());
                    return;
                }
                if (status >= 400 && status < 500) {
                    throw new IllegalStateException(
                            String.format("Mailjet rejected the request (status %d). Body: %s", status,
                                    response.body()));
                }
                log.warn("Mailjet responded with status {} (attempt {}/{}). Retrying...", status, attempt, MAX_RETRIES);
            } catch (IOException ex) {
                log.warn("I/O error sending email via Mailjet (attempt {}/{}): {}", attempt, MAX_RETRIES,
                        ex.getMessage());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Thread interrupted while sending email via Mailjet", ex);
            }

            if (attempt == MAX_RETRIES) {
                throw new IllegalStateException("Max retry attempts reached when calling Mailjet API");
            }

            try {
                Thread.sleep(backoff);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Thread interrupted during Mailjet backoff wait", ex);
            }
            backoff *= 2;
        }
    }

    private void logSuccess(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode message = root.path("Messages").isArray() && root.path("Messages").size() > 0
                    ? root.path("Messages").get(0)
                    : null;
            String messageId = message != null && message.hasNonNull("MessageID")
                    ? message.get("MessageID").asText()
                    : "unknown";
            log.info("Mailjet email sent successfully. MessageID={}", messageId);
        } catch (IOException e) {
            log.info("Mailjet email sent successfully, but response parsing failed: {}", e.getMessage());
        }
    }

    private String buildPayload(String fromEmail, String fromName, String toEmail, String subject,
            String textBody, String htmlBody, Long templateId, Map<String, Object> variables) {
        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode messages = root.putArray("Messages");
        ObjectNode message = messages.addObject();

        ObjectNode from = message.putObject("From");
        from.put("Email", fromEmail);
        from.put("Name", fromName);

        ArrayNode to = message.putArray("To");
        ObjectNode toNode = to.addObject();
        toNode.put("Email", toEmail);
        toNode.put("Name", toEmail);

        Optional.ofNullable(subject).ifPresent(value -> message.put("Subject", value));
        Optional.ofNullable(textBody).ifPresent(value -> message.put("TextPart", value));
        Optional.ofNullable(htmlBody).ifPresent(value -> message.put("HTMLPart", value));

        if (templateId != null) {
            message.put("TemplateID", templateId);
            message.put("TemplateLanguage", true);
        }

        if (variables != null && !variables.isEmpty()) {
            ObjectNode variablesNode = message.putObject("Variables");
            variables.forEach((key, value) -> variablesNode.putPOJO(key, value));
        }

        return root.toString();
    }

    private String buildBasicAuthHeader() {
        String credentials = apiKey + ":" + apiSecret;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    private String requireProperty(Environment environment, String propertyKey) {
        String value = environment.getProperty(propertyKey);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Configuration property " + propertyKey + " must be provided");
        }
        return value;
    }
}
