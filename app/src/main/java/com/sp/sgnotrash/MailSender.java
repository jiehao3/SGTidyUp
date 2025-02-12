package com.sp.sgnotrash;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class MailSender extends Authenticator {
    private final String user;
    private final String password;
    private final Session session;

    public MailSender(String user, String password) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        session = Session.getInstance(props, this);
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public void sendEmail(String to, String subject, String messageBody) throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(user));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(messageBody);

        Transport.send(message);
    }
    public void sendEmailWithAttachment(String to, String subject, String messageBody,
                                        byte[] attachmentData, String attachmentFileName) throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(user));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        // Create a multipart message
        Multipart multipart = new MimeMultipart();

        // First part: the text body
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(messageBody);
        multipart.addBodyPart(textPart);

        // Second part: the image attachment
        MimeBodyPart attachmentPart = new MimeBodyPart();
        DataSource source = new ByteArrayDataSource(attachmentData, "image/jpeg");
        attachmentPart.setDataHandler(new DataHandler(source));
        attachmentPart.setFileName(attachmentFileName);
        multipart.addBodyPart(attachmentPart);

        // Set the multipart message as the email's content
        message.setContent(multipart);

        Transport.send(message);
    }
}
