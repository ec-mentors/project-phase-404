package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.service.LoginService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {
    
    private final LoginService loginService;
    
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }
    
    @GetMapping("/login")
    String login(HttpServletRequest request, Model model) {
        
        return loginService.getLoggedIn(request, model);
    }
}
