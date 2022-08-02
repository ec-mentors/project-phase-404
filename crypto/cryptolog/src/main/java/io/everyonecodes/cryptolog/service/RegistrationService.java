package io.everyonecodes.cryptolog.service;


import io.everyonecodes.cryptolog.data.User;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;

@Service
public class RegistrationService {
	
	private final UserService userService;
	private final VerificationEmailSenderService emailSenderService;
	private final ConfirmationTokenService confirmationTokenService;
	
	public RegistrationService(UserService userService,
	
	                              VerificationEmailSenderService emailSenderService, ConfirmationTokenService confirmationTokenService) {
		this.userService = userService;
		this.emailSenderService = emailSenderService;
		
		this.confirmationTokenService = confirmationTokenService;
	}
	
	
	public String register(Model model) {
		User user = new User();
		model.addAttribute(user);
		return "register";
	}
	
	public String registerUser(@Valid User user, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("successMessage", "Please write the fields in the right format!");
			return "register";
		} else if (userService.isUserAlreadyPresent(user)) {
			if (!userService.isUserValid(user)){
				model.addAttribute("successMessage", "Account already registered, but the Email is NOT VERIFIED! Please proceed to the login page in order to validate your email.");
				
			} else {
				model.addAttribute("successMessage", "There is already an account registered with CryptoLog for the email " + user.getEmail() +
						". Please proceed to log in.");
			}
			
			
		} else {
			userService.saveUser(user);
			model.addAttribute("successMessage", "User is registered successfully, please verify your email.");
			
			ConfirmationToken confirmationToken = confirmationTokenService.createToken(user);
			emailSenderService.sendEmail2(user, confirmationToken);
			
		}
		model.addAttribute("user", new User());
		return "register";
	}
}
