package com.db.admin.controller;

import com.db.admin.service.ServiceProviderService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/admin/providers")
public class ServiceProviderController {

    @Autowired
    ServiceProviderService serviceProviderService;

    // This is the API called when the Admin clicks the email link
    @GetMapping("/verify/{id}")
    public ResponseEntity<String> verifyProvider(@PathVariable Long id) {
        try {
            serviceProviderService.verifyProvider(id);
            // Returning HTML so it looks nice when the admin's browser opens it
            String htmlResponse = "<html><body>"
                    + "<h2>Verification Successful!</h2>"
                    + "<p>The service provider has been approved and is now active on the platform.</p>"
                    + "</body></html>";
            return ResponseEntity.ok().body(htmlResponse);

        } catch (RuntimeException e) {
            String errorHtml = "<html><body>"
                    + "<h2 style='color:red;'>Verification Failed</h2>"
                    + "<p>" + e.getMessage() + "</p>"
                    + "</body></html>";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorHtml);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
