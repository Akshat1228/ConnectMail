package com.example.emailservice.services;

import com.example.emailservice.models.EmailDTO;
import jakarta.mail.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InboxService {

    @Value("${email.service.email}")
    private String email;

    @Value("${email.service.password}")
    private String password;

    private static final String IMAP_HOST = "imap.gmail.com";

    public List<EmailDTO> fetchEmails(int page, int size) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(properties, null);

        Store store = session.getStore("imaps");
        store.connect(IMAP_HOST, email, password);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        int totalMessages = inbox.getMessageCount();
        int start = Math.max(totalMessages - ((page - 1) * size), 1);
        int end = Math.max(start - size + 1, 1);

        if (start < end) {
            return new ArrayList<>();
        }

        Message[] messages = inbox.getMessages(end, start);

        List<EmailDTO> emailDTOs = new ArrayList<>();
        for (Message message : messages) {
            try {
                EmailDTO emailDTO = new EmailDTO();
                emailDTO.setRecipientEmail(message.getAllRecipients()[0].toString());
                emailDTO.setSubject(message.getSubject());
                emailDTO.setMessage(extractMessageContent(message));
                emailDTO.setSentAt(message.getSentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                emailDTOs.add(emailDTO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return emailDTOs;
    }

    public List<EmailDTO> searchEmails(String keyword, int page, int size) throws MessagingException, IOException {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(properties, null);

        Store store = session.getStore("imaps");
        store.connect(IMAP_HOST, email, password);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        Message[] allMessages = inbox.getMessages();
        List<EmailDTO> filteredMessages = new ArrayList<>();

        for (Message message : allMessages) {
            try {
                String subject = message.getSubject();
                String content = getMessageContent(message);
                String recipients = Arrays.toString(message.getRecipients(Message.RecipientType.TO));

                if ((subject != null && subject.toLowerCase().contains(keyword.toLowerCase())) ||
                        (content != null && content.toLowerCase().contains(keyword.toLowerCase())) ||
                        (recipients != null && recipients.toLowerCase().contains(keyword.toLowerCase()))) {

                    EmailDTO emailDTO = new EmailDTO();
                    emailDTO.setRecipientEmail(recipients);
                    emailDTO.setSubject(subject);
                    emailDTO.setMessage(content);
                    emailDTO.setSentAt(message.getSentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

                    filteredMessages.add(emailDTO);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Pagination logic
        int start = (page - 1) * size;
        int end = Math.min(start + size, filteredMessages.size());

        if (start >= filteredMessages.size()) {
            return new ArrayList<>(); // Return empty list if page is out of range
        }
        System.out.println(filteredMessages);
        log.info("message-->"+filteredMessages.subList(start, end));
        return filteredMessages.subList(start, end);
    }
    private String getMessageContent(Message message) throws IOException, MessagingException {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    return bodyPart.getContent().toString();
                } else if (bodyPart.isMimeType("text/html")) {
                    return bodyPart.getContent().toString();
                }
            }
        }
        return ""; // Return empty string if no content is found
    }
    private String extractMessageContent(Message message) {
        try {
            Object content = message.getContent();
            if (content instanceof String) {
                // If the content is plain text or simple HTML
                return content.toString();
            } else if (content instanceof Multipart) {
                Multipart multipart = (Multipart) content;
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.isMimeType("text/plain")) {
                        // Return plain text content
                        return bodyPart.getContent().toString();
                    } else if (bodyPart.isMimeType("text/html")) {
                        // Return HTML content (if preferred)
                        return bodyPart.getContent().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
        }
        return "Unable to extract content"; // Fallback message
    }
}
