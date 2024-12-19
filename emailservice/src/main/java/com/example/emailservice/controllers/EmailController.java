package com.example.emailservice.controllers;

import com.example.emailservice.models.EmailForm;
import com.example.emailservice.services.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Controller
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/send-email")
    public String showEmailForm(Model model) {
        model.addAttribute("email", new EmailForm());
        return "send-email"; // Points to the Thymeleaf template
    }
    @PostMapping("/send-email")
    public String sendEmail(@ModelAttribute EmailForm emailForm, Model model) {
        try {
            emailService.sendEmailWithAttachments(emailForm, emailForm.getAttachments());
            model.addAttribute("successMessage", "Email sent successfully!");
        } catch (MessagingException | IOException e) {
            model.addAttribute("errorMessage", "Failed to send email: " + e.getMessage());
        }

//        return "send-email"; // Render the same form with success/error messages
        return "redirect:/send-email";
    }
}
