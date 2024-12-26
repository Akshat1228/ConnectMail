package com.example.emailservice.controllers;

import com.example.emailservice.models.EmailDTO;
import com.example.emailservice.models.EmailForm;
import com.example.emailservice.services.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import com.example.emailservice.services.InboxService;
import java.util.List;
import java.util.stream.Collectors;;
@Controller
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private InboxService inboxService;

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

    @GetMapping("/inbox")
    public String showInbox(@RequestParam(defaultValue = "") String keyword,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model) {
        try {
            List<EmailDTO> emails;
            if (keyword.isEmpty()) {
                // Implement search functionality later if needed
                emails = inboxService.fetchEmails(page + 1, size); // Placeholder
            } else {
                emails = inboxService.searchEmails(keyword,page + 1, size);
            }

            model.addAttribute("emails", emails);
            model.addAttribute("keyword", keyword);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", (int) Math.ceil((double) emails.size() / size));
            return "inbox";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load emails: " + e.getMessage());
            return "inbox";
        }
    }
//    @GetMapping("/search")
//    public ResponseEntity<List<String>> searchInbox(@RequestParam String keyword,
//                                                    @RequestParam(defaultValue = "1") int page,
//                                                    @RequestParam(defaultValue = "10") int size) {
//        try {
//            List<Message> messages = inboxService.searchEmails(keyword, page, size);
//            List<String> subjects = messages.stream()
//                    .map(message -> {
//                        try {
//                            return message.getSubject();
//                        } catch (javax.mail.MessagingException e) {
//                            return "Error fetching subject";
//                        }
//                    })
//                    .collect(Collectors.toList());
//            return ResponseEntity.ok(subjects);
//        } catch (IOException | javax.mail.MessagingException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
    
}
