package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.service.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class RegistrationController {
    
    private final RegistrationService registrationService;
    
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }
    
    @GetMapping("/register")
    String register(Model model) {
        return registrationService.register(model);
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid User user, BindingResult bindingResult, Model model) {
        return registrationService.registerUser(user, bindingResult, model);
    }
}