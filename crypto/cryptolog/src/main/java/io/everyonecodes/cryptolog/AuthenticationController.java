package io.everyonecodes.cryptolog;

import io.everyonecodes.cryptolog.data.ConfirmationToken;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.service.ConfirmationTokenService;
import io.everyonecodes.cryptolog.service.UserService;
import io.everyonecodes.cryptolog.service.VerificationEmailSenderService;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class AuthenticationController {
    private final UserService userService;
    private final VerificationEmailSenderService emailSenderService;
    private final ConfirmationTokenService confirmationTokenService;

    public AuthenticationController(UserService userService, VerificationEmailSenderService emailSenderService, ConfirmationTokenService confirmationTokenService) {
        this.userService = userService;
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
                model.addAttribute("errorMessage", "Your Account is not verified. Please check your emails for instructions");
            } else {
                model.addAttribute("errorMessage", errorMessage);
            }
        }

        return "login";
    }

    @GetMapping("/register")
    String register(Model model) {
        User user = new User();
        model.addAttribute(user);
        return "register";
    }

    @GetMapping("/home")
    String home() {
        return "home";
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
            emailSenderService.sendEmail(user, confirmationToken);
        }
        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping(path = "confirm")
    public ModelAndView confirm(@RequestParam("token") String token) {
        return confirmationTokenService.confirmToken(token);
    }
}

