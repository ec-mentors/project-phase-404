package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.data.Token;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class EmailSenderService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    TokenRepository tokenRepository;
    public void sendEmail(User user){
        LocalDateTime timeNow = LocalDateTime.now();
        timeNow.plusMinutes(15);
        Random random = new Random();
        int code = random.nextInt(9999 + 1 - 1000) + 1000;
        String tokenString = String.valueOf(code);
        Token token = new Token(tokenString,user,timeNow);
        tokenRepository.save(token);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("cryptolog@gmail.com");
        message.setTo(user.getEmail());
        message.setText("This is your verification code:" + tokenString);
        message.setSubject("Verification Email");
        mailSender.send(message);
    }
}
