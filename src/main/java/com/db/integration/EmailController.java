package com.db.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {
        @Autowired
        private EmailService emailService;

        // Endpoint: http://localhost:8080/test-email?to=your-email@gmail.com
        @GetMapping("/test-email")
        public String sendTestEmail(@RequestParam String to) {
            try {
                 emailService.sendProviderVerificationEmail(5L, "Josh","Cleaning");
                return "Email sent successfully to " + to + ". Check your inbox (and spam folder).";
            } catch (Exception e) {
                return "Error sending email: " + e.getMessage();
            }
        }



    }
