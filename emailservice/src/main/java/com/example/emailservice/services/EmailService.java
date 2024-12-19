package com.example.emailservice.services;

import com.example.emailservice.models.Attachment;
import com.example.emailservice.models.Email;
import com.example.emailservice.models.EmailForm;
import com.example.emailservice.repositories.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmailWithAttachments(EmailForm emailForm, MultipartFile[] attachments) throws IOException, MessagingException {
        // Map EmailForm to Email entity
        Email email = new Email();
        email.setRecipientEmail(emailForm.getRecipientEmail());
        email.setSubject(emailForm.getSubject());
        email.setMessage(emailForm.getMessage());
        email.setSentAt(LocalDateTime.now());

        // Process and attach files
        List<Attachment> attachmentList = new ArrayList<>();
        if (attachments != null) {
            for (MultipartFile file : attachments) {
                if (!file.isEmpty()) {
                    Attachment attachment = new Attachment();
                    attachment.setFileName(file.getOriginalFilename());
                    attachment.setFileType(file.getContentType());
                    attachment.setData(file.getBytes()); // Store file content as bytes
                    attachment.setEmail(email); // Associate attachment with email
                    attachmentList.add(attachment);
                }
            }
        }

        email.setAttachments(attachmentList);
        emailRepository.save(email); // Save email and attachments to the database

        // Send the email
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        messageHelper.setTo(email.getRecipientEmail());
        messageHelper.setSubject(email.getSubject());
        messageHelper.setText(email.getMessage());

        // Add attachments to the email
        for (MultipartFile file : attachments) {
            if (!file.isEmpty()) {
                messageHelper.addAttachment(file.getOriginalFilename(), file);
            }
        }

        javaMailSender.send(mimeMessage);
    }
}
