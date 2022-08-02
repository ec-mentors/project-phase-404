package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.service.UserVerificationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class VerificationController {
    
    private final UserVerificationService verificationService;
    
    public VerificationController(UserVerificationService verificationService) {
        this.verificationService = verificationService;
    }
    
    @GetMapping("/verification")
    public ModelAndView displayVerification(ModelAndView modelAndView, User user) {
        return verificationService.displayVerification(modelAndView, user);
    }

    @PostMapping("/verification")
    public ModelAndView resendEmailverification(ModelAndView modelAndView, User user) {
        return verificationService.resendEmailVerification(modelAndView, user);
    }
}
