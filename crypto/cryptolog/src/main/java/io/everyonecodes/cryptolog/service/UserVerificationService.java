package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.data.ConfirmationToken;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Service
public class UserVerificationService {
	
	private final UserRepository userRepository;
	private final ConfirmationTokenService confirmationTokenService;
	private final VerificationEmailSenderService emailSenderService;
	
	public UserVerificationService(UserRepository userRepository, ConfirmationTokenService confirmationTokenService, VerificationEmailSenderService emailSenderService) {
		this.userRepository = userRepository;
		this.confirmationTokenService = confirmationTokenService;
		this.emailSenderService = emailSenderService;
	}
	
	public ModelAndView displayVerification(ModelAndView modelAndView, User user) {
		modelAndView.addObject("user", user);
		modelAndView.setViewName("verification");
		return modelAndView;
	}
	
	public ModelAndView resendEmailVerification(ModelAndView modelAndView, User user) {
		Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
		if (existingUser.isPresent()) {
			User userE = existingUser.get();
			
			ConfirmationToken confirmationToken = confirmationTokenService.createToken(userE);
			emailSenderService.sendEmail(userE,confirmationToken);
			
			modelAndView.addObject("message", "Verification link successfully resent.");
			modelAndView.setViewName("verificationSuccess");
			
		} else {
			modelAndView.addObject("message", "This email does not exist!");
			modelAndView.setViewName("error");
		}
		
		return modelAndView;
	}
}
