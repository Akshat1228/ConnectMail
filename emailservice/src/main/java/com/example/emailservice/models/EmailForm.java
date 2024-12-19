package com.example.emailservice.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EmailForm {
    @NotEmpty(message = "Recipient email is required")
    @Email(message = "Please provide a valid email address")
    private String recipientEmail;

    @NotEmpty(message = "Subject is required")
    private String subject;

    @NotEmpty(message = "Message content is required")
    private String message;

    private MultipartFile[] attachments;

}
