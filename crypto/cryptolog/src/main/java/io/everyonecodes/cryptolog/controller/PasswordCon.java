package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.service.PasswordService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
public class PasswordCon {
    
    private final PasswordService passwordService;
    
    public PasswordCon(PasswordService passwordService) {
        this.passwordService = passwordService;
    }
    
    @GetMapping("/forgot-password")
    public ModelAndView displayResetPassword(ModelAndView modelAndView, User user) {
        return passwordService.displayResetPassword(modelAndView, user);
    }

    @PostMapping("/forgot-password")
    public ModelAndView forgotUserPassword(ModelAndView modelAndView, User user) {
        return passwordService.forgotUserPassword(modelAndView, user);
    }


    @RequestMapping(value="/confirm-reset", method= {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView validateResetToken(ModelAndView modelAndView, @RequestParam("token") String confirmationToken)
    {
        return passwordService.validateResetToken(modelAndView, confirmationToken);
    }

    @PostMapping(value = "/reset-password")
    public ModelAndView resetUserPassword(ModelAndView modelAndView, User user) {
        return passwordService.resetUserPassword(modelAndView, user);
    }
}
