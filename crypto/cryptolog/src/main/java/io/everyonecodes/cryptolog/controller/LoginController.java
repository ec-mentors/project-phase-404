package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.service.UserService;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
   private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    String login(HttpServletRequest request, Model model) {
        var oUser = userService.findUserByEmail("admin@gmail.com");
        if (oUser.isEmpty()){
            userService.saveAdmin(new User("admin","admin@gmail.com","Password1!" ));
        }
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

                model.addAttribute("errorMessage", "Your email is not validated! Resend <a href=\"/verification\">Verification Link</a>?");

            } else {
                model.addAttribute("errorMessage", errorMessage);

            }
        }
        model.addAttribute("title", "Login - CryptoLog");
        return "login";
    }
}
