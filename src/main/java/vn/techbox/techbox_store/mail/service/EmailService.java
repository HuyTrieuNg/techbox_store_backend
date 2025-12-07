package vn.techbox.techbox_store.mail.service;

public interface EmailService {
    void sendMessage(String to, String subject, String text);
    void sendHtmlMessage(String to, String subject, String htmlContent);
}
