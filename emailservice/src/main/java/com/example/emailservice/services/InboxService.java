package com.example.emailservice.services;

import org.springframework.stereotype.Service;

import javax.mail.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Service
public class InboxService {

    private static final String IMAP_HOST = "imap.gmail.com";
    private static final String EMAIL = "akshat281999@gmail.com"; // Replace with your email
    private static final String PASSWORD = "hkvbzetjmqnfpjut";    // Replace with your password

    public List<Message> fetchEmails(int page, int size) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(properties, null);

        Store store = session.getStore("imaps");
        store.connect(IMAP_HOST, EMAIL, PASSWORD);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        int totalMessages = inbox.getMessageCount();
        int start = totalMessages - ((page - 1) * size);
        int end = Math.max(start - size + 1, 1);

        Message[] messages = inbox.getMessages(end, start);
        return Arrays.asList(messages);
    }

    public List<Message> searchEmails(String keyword, int page, int size) throws MessagingException, IOException {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(properties, null);

        Store store = session.getStore("imaps");
        store.connect(IMAP_HOST, EMAIL, PASSWORD);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        Message[] allMessages = inbox.getMessages();
        List<Message> filteredMessages = new ArrayList<>();

        for (Message message : allMessages) {
            if (message.getSubject() != null && message.getSubject().contains(keyword)) {
                filteredMessages.add(message);
            }
        }

        // Pagination logic
        int start = (page - 1) * size;
        int end = Math.min(start + size, filteredMessages.size());
        return filteredMessages.subList(start, end);
    }
}
