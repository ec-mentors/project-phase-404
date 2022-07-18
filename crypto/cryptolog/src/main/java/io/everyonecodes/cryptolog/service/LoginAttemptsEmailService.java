package io.everyonecodes.cryptolog.service;


import io.everyonecodes.cryptolog.data.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptsEmailService {

    private final JavaMailSender mailSender;

    public LoginAttemptsEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendEmailLoginFail(User user) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo("raul_bh_93@yahoo.com");
        simpleMailMessage.setSubject("CryptoLog Warning");
        simpleMailMessage.setText("Someone is trying to log in on your CryptoLog account!");
        simpleMailMessage.setFrom("cryptolog@gmail.com");
        mailSender.send(simpleMailMessage);

    }
}
