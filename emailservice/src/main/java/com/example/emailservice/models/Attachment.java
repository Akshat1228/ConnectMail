package com.example.emailservice.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;

    @Lob
    private byte[] data;  // Store the actual file content if needed

    @ManyToOne
    @JoinColumn(name = "email_id", nullable = false)
    private Email email;
}

