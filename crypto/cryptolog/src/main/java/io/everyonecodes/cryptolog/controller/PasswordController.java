package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.service.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PasswordController {

    @Autowired
    private UserServiceImp userService;

//    @Autowired
//    private EmailService emailService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Value("${server.port}")
    private String serverPort;

    // Display forgotPassword page
    @GetMapping("/forgot")
    public String displayForgotPasswordPage(Model model) {
        return "forgotPassword";
    }

    // Process form submission from forgotPassword page
    @PostMapping("/forgot")
    public String processForgotPasswordForm(Model model, @RequestParam("email") String userEmail, HttpServletRequest request) {

        // Lookup user in database by e-mail
        Optional<User> optional = userService.findUserByEmail(userEmail);

        if (!optional.isPresent()) {
            model.addAttribute("errorMessage", "We didn't find an account for that e-mail address.");
        } else {

            // Generate random 36-character string token for reset password
            User user = optional.get();
            user.setResetToken(UUID.randomUUID().toString());

            // Save token to database
//            userServiceImpl.save(user);
            userService.saveUser(user);

            String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + serverPort;

            // Email message
            SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
            passwordResetEmail.setFrom("support@demo.com");
            passwordResetEmail.setTo(user.getEmail());
            passwordResetEmail.setSubject("Password Reset Request");
            passwordResetEmail.setText("To reset your password, click the link below:\n" + appUrl
                    + "/reset?token=" + user.getResetToken());

            mailSender.send(passwordResetEmail);

            // Add success message to view
            model.addAttribute("successMessage", "A password reset link has been sent to " + userEmail);
        }

        return "forgotPassword";
    }

    // Display form to reset password
    @GetMapping("/reset")
    public String displayResetPasswordPage(Model model, @RequestParam("token") String token) {

        Optional<User> user = userService.findUserByResetToken(token);

        if (user.isPresent()) { // Token found in DB
            model.addAttribute("resetToken", token);
        } else { // Token not found in DB
            model.addAttribute("errorMessage", "Oops!  This is an invalid password reset link.");
        }

        return "resetPassword";
    }

    // Process reset password form
    @PostMapping("/reset")
    public String setNewPassword(Model model, @RequestParam Map<String, String> requestParams, RedirectAttributes redir) {

        // Find the user associated with the reset token
        Optional<User> user = userService.findUserByResetToken(requestParams.get("token"));

        // This should always be non-null but we check just in case
        if (user.isPresent()) {

            User resetUser = user.get();

            // Set new password
            resetUser.setPassword(bCryptPasswordEncoder.encode(requestParams.get("password")));

            // Set the reset token to null so it cannot be used again
            resetUser.setResetToken(null);

            // Save user
//            UserServiceimpl?
//            userServiceImpl.save(resetUser);
            userService.saveUser(resetUser);

            // In order to set a model attribute on a redirect, we must use
            // RedirectAttributes
            redir.addFlashAttribute("successMessage", "You have successfully reset your password.  You may now login.");

            return "redirect:login";

        } else {
            model.addAttribute("errorMessage", "Oops!  This is an invalid password reset link.");
        }
        return "resetPassword";
    }

    // Going to reset page without a token redirects to login page
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingParams(MissingServletRequestParameterException ex) {
        return "redirect:login";
    }
}
