package com.example.ReadMailSubjectAddInDB.Service;

import com.example.ReadMailSubjectAddInDB.Dao.EmailDao;
import com.example.ReadMailSubjectAddInDB.Entity.Email;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.FlagTerm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

    @Value("${mail.host}")
    private String host;

    @Value("${mail.user}")
    private String user;

    @Value("${mail.password}")
    private String password;

    @Value("${mail.keyword}")
    private String keyword;

    private final EmailDao emailDao;

    public EmailService(EmailDao emailDao) {
        this.emailDao = emailDao;
    }

    public void fetchAndReadMailUsingKeyword() {
        try {
            // Set up properties for IMAP connection
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "imaps");

            // Create email session
            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore("imaps");
            store.connect(host, user, password);

            // Open inbox folder
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            // Fetch unread emails
            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            System.out.println("Total unread messages: " + messages.length);

            for (Message message : messages) {
                String subject = message.getSubject();
                Address[] from = message.getFrom();
                String sender = (from != null && from.length > 0) ? from[0].toString() : "Unknown";

                String content = getTextFromMessage(message);
                System.out.println("Checking email: " + subject);
                System.out.println("Sender: " + sender);
                System.out.println("Content: " + content);

                // Check if content contains the keyword
                if (content.contains(keyword)) {
                    System.out.println("Keyword '" + keyword + "' found! Saving email...");
                    emailDao.saveEmail(subject, sender, content);
                    message.setFlag(Flags.Flag.SEEN, true); // Mark as read
                } else {
                    System.out.println("Keyword not found, skipping...");
                }
            }

            // Close inbox and store
            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            return getTextFromMultipart((MimeMultipart) message.getContent());
        }
        return "";
    }

    private String getTextFromMultipart(MimeMultipart multipart) throws Exception {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent().toString());
            } else if (bodyPart.isMimeType("text/html")) {
                String html = bodyPart.getContent().toString();
                result.append(org.jsoup.Jsoup.parse(html).text()); // Convert HTML to plain text
            }
        }
        return result.toString();
    }
}
