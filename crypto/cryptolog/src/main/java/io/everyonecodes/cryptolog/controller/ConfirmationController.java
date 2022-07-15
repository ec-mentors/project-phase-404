package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.data.ConfirmationToken;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.service.ConfirmationTokenService;
import io.everyonecodes.cryptolog.service.UserService;
import io.everyonecodes.cryptolog.service.VerificationEmailSenderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class ConfirmationController {

    private final ConfirmationTokenService confirmationTokenService;

    public ConfirmationController(ConfirmationTokenService confirmationTokenService) {
        this.confirmationTokenService = confirmationTokenService;
    }

    @GetMapping(path = "confirm")
    public ModelAndView confirm(@RequestParam("token") String token) {
        return confirmationTokenService.confirmToken(token);
    }
}
