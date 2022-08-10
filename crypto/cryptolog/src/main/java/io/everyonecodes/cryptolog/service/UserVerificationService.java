package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.data.ConfirmationToken;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Service
public class UserVerificationService {
	
	private final UserRepository userRepository;
	private final ConfirmationTokenService confirmationTokenService;
	private final VerificationEmailSenderService emailSenderService;
	
	private final String displayName;
	private final String viewName;
	private final String resendName;
	private final String successText;
	private final String vnSuccess;
	private final String errorText;
	private final String vnError;
	
	public UserVerificationService(UserRepository userRepository,
								   ConfirmationTokenService confirmationTokenService,
								   VerificationEmailSenderService emailSenderService,
	                               @Value("${messages.verification.display.name}") String displayName,
	                               @Value("${messages.verification.display.viewName}") String viewName,
	                               @Value("${messages.verification.resendEmail.name}") String resendName,
	                               @Value("${messages.verification.resendEmail.successText}") String successText,
	                               @Value("${messages.verification.resendEmail.vnSuccess}") String vnSuccess,
	                               @Value("${messages.verification.resendEmail.errorText}") String errorText,
	                               @Value("${messages.verification.resendEmail.vnError}") String vnError) {
		this.userRepository = userRepository;
		this.confirmationTokenService = confirmationTokenService;
		this.emailSenderService = emailSenderService;
		this.displayName = displayName;
		this.viewName = viewName;
		this.resendName = resendName;
		this.successText = successText;
		this.vnSuccess = vnSuccess;
		this.errorText = errorText;
		this.vnError = vnError;
	}
	
	public ModelAndView displayVerification(ModelAndView modelAndView, User user) {
		modelAndView.addObject(displayName, user);
		modelAndView.setViewName(viewName);
		return modelAndView;
	}
	
	public ModelAndView resendEmailVerification(ModelAndView modelAndView, User user) {
		Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
		if (existingUser.isPresent()) {
			User userE = existingUser.get();
			
			ConfirmationToken confirmationToken = confirmationTokenService.createToken(userE);
			emailSenderService.sendEmail(userE,confirmationToken);
			
			modelAndView.addObject(resendName, successText);
			modelAndView.setViewName(vnSuccess);
			
		} else {
			modelAndView.addObject(resendName, errorText);
			modelAndView.setViewName(vnError);
		}
		
		return modelAndView;
	}
}
