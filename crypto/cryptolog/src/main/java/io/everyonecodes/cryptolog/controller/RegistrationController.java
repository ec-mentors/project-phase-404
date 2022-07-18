package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.data.ConfirmationToken;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.service.ConfirmationTokenService;
import io.everyonecodes.cryptolog.service.EmailSenderService;
import io.everyonecodes.cryptolog.service.UserService;
//import io.everyonecodes.cryptolog.service.VerificationEmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class RegistrationController {

    private final UserService userService;
//    private final VerificationEmailSenderService emailSenderService;
    private final ConfirmationTokenService confirmationTokenService;
    private  final EmailSenderService mailSenderService;

    public RegistrationController(UserService userService,

                                  ConfirmationTokenService confirmationTokenService, EmailSenderService mailSenderService) {
        this.userService = userService;

        this.confirmationTokenService = confirmationTokenService;
        this.mailSenderService = mailSenderService;
    }
    //                                  VerificationEmailSenderService emailSenderService,
    //        this.emailSenderService = emailSenderService;


    @GetMapping("/register")
    String register(Model model) {
        User user = new User();
        model.addAttribute(user);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("successMessage", "Please write the fields in the right format!");
            return "register";
        } else if (userService.isUserAlreadyPresent(user)) {
            model.addAttribute("successMessage", "There is already an account registered with CryptoLog for the email " + user.getEmail() + ". Please proceed to log in.");
        } else {
            userService.saveUser(user);
            model.addAttribute("successMessage", "User is registered successfully, please verify your email.");
            ConfirmationToken confirmationToken = confirmationTokenService.createToken(user);

//            emailSenderService.sendEmail(user, confirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("Please verify your email");
            mailMessage.setFrom("raulbodog993@gmail.com");
            mailMessage.setText("To complete the  process, please click here: "
                    +"http://localhost:9200/confirm?token="+confirmationToken.getToken());
            mailSenderService.sendEmail(mailMessage);
        }
        model.addAttribute("user", new User());
        return "register";
    }
}
