package com.example.emailservice.models;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class EmailDTO {
    private String recipientEmail;
    private String subject;
    private String message;
    private LocalDateTime sentAt;
}
