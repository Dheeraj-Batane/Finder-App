package com.db.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        // You can pass dynamic data to the frontend here later
        model.addAttribute("pageTitle", "Home - Local Service Finder");
        // Returns the template name "index.html" (without the extension)
        return "index";
    }
    // Add this to HomeController.java
    @GetMapping("/provider-signup")
    public String providerSignUp(Model model) {
        model.addAttribute("pageTitle", "Become a Professional - Local Service Finder");
        return "provider-signup";
    }
    @GetMapping("/provider-onboarding")
    public String providerOnboarding(Model model) {
        model.addAttribute("pageTitle", "Complete Your Profile - Local Service Finder");
        return "provider-onboarding";
    }
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("pageTitle", "Login - Local Service Finder");
        return "login";
    }

    @GetMapping("/user-dashboard")
    public String userDashboard(Model model) {
        model.addAttribute("pageTitle", "User Dashboard - Local Service Finder");
        return "user-dashboard";
    }
    @GetMapping("/providers")
    public String providersList(Model model) {
        model.addAttribute("pageTitle", "Available Providers - Local Service Finder");
        return "providers";
    }

}
