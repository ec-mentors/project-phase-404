package io.everyonecodes.cryptolog.service;


import io.everyonecodes.cryptolog.data.ConfirmationToken;
import io.everyonecodes.cryptolog.data.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;

@Service
public class RegistrationService {
	
	private final UserService userService;
	private final VerificationEmailSenderService emailSenderService;
	private final ConfirmationTokenService confirmationTokenService;
	
	private final String rightFormat;
	private final String registeredButNotVerified;
	private final String registeredForEmail;
	private final String proceedToLogin;
	private final String regSuccessVerifyEmail;
	
	public RegistrationService(UserService userService,
	                           VerificationEmailSenderService emailSenderService, ConfirmationTokenService confirmationTokenService, @Value("${messages.registration.rightFormat}") String rightFormat, @Value("${messages.registration.registeredButNotVerified}") String registeredButNotVerified, @Value("${messages.registration.registeredForEmail}") String registeredForEmail, @Value("${messages.registration.proceedToLogin}") String proceedToLogin, @Value("${messages.registration.regSuccessVerifyEmail}") String regSuccessVerifyEmail) {
		this.userService = userService;
		this.emailSenderService = emailSenderService;
		
		this.confirmationTokenService = confirmationTokenService;
		this.rightFormat = rightFormat;
		this.registeredButNotVerified = registeredButNotVerified;
		this.registeredForEmail = registeredForEmail;
		this.proceedToLogin = proceedToLogin;
		this.regSuccessVerifyEmail = regSuccessVerifyEmail;
	}
	
	
	public String register(Model model) {
		User user = new User();
		model.addAttribute(user);
		return "register";
	}
	
	public String registerUser(@Valid User user, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("successMessage", rightFormat);
			return "register";
		} else if (userService.isUserAlreadyPresent(user)) {
			if (!userService.isUserValid(user)){
				model.addAttribute("successMessage", registeredButNotVerified);
				
			} else {
				model.addAttribute("successMessage", registeredForEmail + " " + user.getEmail() +
						". " + proceedToLogin);
			}
			
			
		} else {
			userService.saveUser(user);
			model.addAttribute("successMessage",regSuccessVerifyEmail );
			
			ConfirmationToken confirmationToken = confirmationTokenService.createToken(user);
			emailSenderService.sendEmail2(user, confirmationToken);
			
		}
		
		model.addAttribute("user", new User());
		
		return "register";
	}
}
