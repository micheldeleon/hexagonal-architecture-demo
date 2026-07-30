package com.tutorneo.adapters.in.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tutorneo.core.ports.out.EmailSenderPort;

@RestController
@RequestMapping("/api/mail")
public class PruebaController {

    private final EmailSenderPort emailPort;

    public PruebaController(EmailSenderPort emailPort) {
        this.emailPort = emailPort;
    }

    @PostMapping("/test")
    public ResponseEntity<String> sendTestEmail(@RequestParam String to) {
        emailPort.sendEmail(
                to,
                "My first Mailjet Email!",
                "Greetings from Mailjet!",
                "<h3>EL PELUCA SAPEEEEE<a href=\"https://www.mailjet.com/\">Mailjet!</a></h3><br />May the delivery force be with you!");
        return ResponseEntity.ok("Correo de prueba enviado a " + to);
    }
}
