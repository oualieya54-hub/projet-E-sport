package org.example.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Envoi SMTP optionnel : activer via {@code classpath:mail.properties}
 * (copier {@code mail.example.properties} → {@code mail.properties}).
 */
public class EmailService {

    private static final String RESOURCE = "/mail.properties";

    public static class SendResult {
        public final boolean sent;
        public final String message;

        public SendResult(boolean sent, String message) {
            this.sent = sent;
            this.message = message;
        }
    }

    /**
     * Envoie un e-mail texte brut. Si {@code mail.send.enabled} est absent ou false,
     * retourne {@code sent=false} avec un message explicatif (aucune exception).
     */
    public SendResult sendPlainText(String toAddress, String subject, String body) {
        Properties cfg = loadMailProperties();
        if (cfg == null || cfg.isEmpty()) {
            return new SendResult(false,
                    "Fichier mail.properties introuvable. Copiez mail.example.properties vers src/main/resources/mail.properties et configurez le SMTP.");
        }
        if (!"true".equalsIgnoreCase(cfg.getProperty("mail.send.enabled", "false"))) {
            return new SendResult(false,
                    "Envoi d’e-mail désactivé (mail.send.enabled=false dans mail.properties).");
        }

        String host = cfg.getProperty("mail.smtp.host");
        String port = cfg.getProperty("mail.smtp.port", "587");
        String user = cfg.getProperty("mail.smtp.user");
        String password = cfg.getProperty("mail.smtp.password");
        String from = cfg.getProperty("mail.from", user);
        String fromName = cfg.getProperty("mail.from.name", "Game Pilot");

        if (host == null || host.isBlank() || user == null || user.isBlank()
                || password == null || password.isBlank()) {
            return new SendResult(false, "Configuration SMTP incomplète dans mail.properties.");
        }

        Properties mailProps = new Properties();
        mailProps.put("mail.transport.protocol", "smtp");
        mailProps.put("mail.smtp.host", host);
        mailProps.put("mail.smtp.port", port);
        mailProps.put("mail.smtp.auth", "true");
        mailProps.put("mail.smtp.starttls.enable", cfg.getProperty("mail.smtp.starttls.enable", "true"));

        Session session = Session.getInstance(mailProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            InternetAddress fromAddr = new InternetAddress(from);
            fromAddr.setPersonal(fromName, "UTF-8");
            message.setFrom(fromAddr);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress, false));
            message.setSubject(subject, "UTF-8");
            message.setText(body, "UTF-8");

            Transport.send(message);
            return new SendResult(true, "E-mail envoyé.");
        } catch (Exception ex) {
            return new SendResult(false, "Échec d’envoi : " + ex.getMessage());
        }
    }

    private Properties loadMailProperties() {
        try (InputStream in = EmailService.class.getResourceAsStream(RESOURCE)) {
            if (in == null) return null;
            Properties p = new Properties();
            p.load(in);
            return p;
        } catch (IOException e) {
            return null;
        }
    }
}
