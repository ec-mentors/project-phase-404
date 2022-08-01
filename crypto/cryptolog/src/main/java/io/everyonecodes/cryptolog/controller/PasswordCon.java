package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.data.ConfirmationToken;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.repository.ConfirmationTokenRepository;
import io.everyonecodes.cryptolog.service.ConfirmationTokenService;
import io.everyonecodes.cryptolog.service.EmailSenderService;
import io.everyonecodes.cryptolog.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping
public class PasswordCon {


    private final UserService userService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailSenderService emailSenderService;
    private final ConfirmationTokenService confirmationTokenService;
    PasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final String email;


    public PasswordCon(UserService userService, ConfirmationTokenRepository confirmationTokenRepository, EmailSenderService emailSenderService, ConfirmationTokenService confirmationTokenService,
                       @Value("${spring.mail.from.email}") String email ) {

        this.userService = userService;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.emailSenderService = emailSenderService;
        this.confirmationTokenService = confirmationTokenService;
        this.email = email;

    }

    @GetMapping("/forgot-password")
    public ModelAndView displayResetPassword(ModelAndView mav, User user) {
        mav.addObject("user", user);
        mav.setViewName("forgotPassword");
        return mav;
    }

    @PostMapping ("/forgot-password")
    public ModelAndView forgotUserPassword(ModelAndView mav, User user) {
        Optional<User> existingUser = userService.findUserByEmail(user.getEmail());
        if(existingUser.isPresent()) {
            User userE = existingUser.get();
           ConfirmationToken confirmationToken =confirmationTokenService.createToken(userE);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("Complete Password Reset!");
            mailMessage.setFrom(email);
            mailMessage.setText("To complete the password reset process, please click here: "
                    +"http://localhost:9200/confirm-reset?token="+confirmationToken.getToken());

            emailSenderService.sendEmail(mailMessage);

            mav.addObject("message", "Request to reset password received. Check your inbox for the reset link.");
            mav.setViewName("successForgotPassword");

        } else {
            mav.addObject("message", "This email does not exist!");
            mav.setViewName("error");
        }

        return mav;
    }


    @RequestMapping(value="/confirm-reset", method= {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView validateResetToken(ModelAndView mav, @RequestParam("token")String confirmationToken)
    {
        Optional<ConfirmationToken> oToken = confirmationTokenRepository.findByToken(confirmationToken);

        if(oToken.isPresent()) {
            ConfirmationToken token = oToken.get();
            Optional<User> existingUser = userService.findUserByEmail((token.getUser().getEmail()));
            if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setVerified(true);
            userService.save(user);
            mav.addObject("user", user);
            mav.addObject("emailId", user.getEmail());
            mav.setViewName("resetPassword2");
        }
        } else {
            mav.addObject("message", "The link is invalid or broken!");
            mav.setViewName("error");
        }
        return mav;
    }

    @PostMapping(value = "/reset-password")
    public ModelAndView resetUserPassword(ModelAndView mav, User user) {

        if(user.getEmail() != null) {
           Optional<User>  otTokenUser = userService.findUserByEmail(user.getEmail());
           if (otTokenUser.isPresent()){

               User tokenUser = otTokenUser.get();
               tokenUser.setPassword(encoder.encode(user.getPassword()));
               userService.save(tokenUser);
               mav.addObject("message", "Password successfully reset. You can now log in with the new credentials.");
               mav.setViewName("successResetPassword");
           }
           }
             else {
            mav.addObject("message","The link is invalid or broken!");
            mav.setViewName("error");
        }

        return mav;
    }

}
