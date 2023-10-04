package com.jfecm.bankaccountmanagement.service.imp;

import com.jfecm.bankaccountmanagement.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    @Value("${app.email.account}")
    private String account;
    @Value("${app.email.account.password}")
    private String accountPassword;

    @Override
    public void sendEmail(String receiver, String subject, String message) throws MessagingException {
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account, accountPassword);
            }
        });

        Message messageResult = configMessage(session, account, receiver, subject, message);

        Transport.send(messageResult);
        log.info("sendEmail() - OK.");
    }

    private Message configMessage(Session session, String email, String receiver, String subject, String message) throws MessagingException {
        MimeMessage mimeMessage = new MimeMessage(session);

        mimeMessage.setFrom(new InternetAddress(email));
        mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(receiver));

        BodyPart bodyPart = new MimeBodyPart();

        bodyPart.setContent(message, "text/html");

        MimeMultipart mimeMultipart = new MimeMultipart("related");

        mimeMultipart.addBodyPart(bodyPart);

        mimeMessage.setSubject(subject);
        mimeMessage.setContent(mimeMultipart);

        return mimeMessage;
    }

}
