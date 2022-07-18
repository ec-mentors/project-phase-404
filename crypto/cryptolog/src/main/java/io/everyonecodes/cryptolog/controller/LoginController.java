package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.service.ConfirmationTokenService;
import io.everyonecodes.cryptolog.service.VerificationEmailSenderService;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    private final VerificationEmailSenderService emailSenderService;
    private final ConfirmationTokenService confirmationTokenService;

    public LoginController(VerificationEmailSenderService emailSenderService, ConfirmationTokenService confirmationTokenService) {
        this.emailSenderService = emailSenderService;
        this.confirmationTokenService = confirmationTokenService;
    }

    @GetMapping("/login")
    String login(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        String errorMessage = null;
        if (session != null) {
            AuthenticationException ex = (AuthenticationException) session
                    .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                errorMessage = ex.getMessage();
            }
        }
        if (errorMessage != null) {
            if (errorMessage.equals("Bad credentials")) {

                model.addAttribute("errorMessage", "Your email or password is incorrect. Please try again");

            } else if (errorMessage.equals("User is disabled")) {

                model.addAttribute("errorMessage", "Your email is not validated!");

            } else {
                model.addAttribute("errorMessage", errorMessage);

            }
        }
        return "login";
    }
}
