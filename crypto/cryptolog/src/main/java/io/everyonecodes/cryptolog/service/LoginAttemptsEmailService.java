package io.everyonecodes.cryptolog.service;


import io.everyonecodes.cryptolog.data.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptsEmailService {

    private final JavaMailSender mailSender;
    
    private final String warningTitle;
    private final String warningText;
    private final String warningFromEmail;

    public LoginAttemptsEmailService(JavaMailSender mailSender, @Value("${cryptolog.messages.email.warningTitle}") String warningTitle, @Value("${cryptolog.messages.email.warningText}") String warningText, @Value("${cryptolog.messages.email.warningFrom}") String warningFromEmail) {
        this.mailSender = mailSender;
        this.warningTitle = warningTitle;
        this.warningText = warningText;
        this.warningFromEmail = warningFromEmail;
    }

    @Async
    public void sendEmailLoginFail(User user) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setSubject(warningTitle);
        simpleMailMessage.setText(warningText);
        simpleMailMessage.setFrom(warningFromEmail);
        mailSender.send(simpleMailMessage);

    }
}
