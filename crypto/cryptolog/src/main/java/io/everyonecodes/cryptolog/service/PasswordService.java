package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.data.ConfirmationToken;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.repository.ConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;

@Service
public class PasswordService {
	
	private final UserService userService;
	private final ConfirmationTokenRepository confirmationTokenRepository;
	private final EmailSenderService emailSenderService;
	private final ConfirmationTokenService confirmationTokenService;
	PasswordEncoder encoder = new BCryptPasswordEncoder(12);
	private final String email;
	private final String resetSubject;
	private final String resetRequestReceived;
	private final String resetRequestURL;
	private final String resetCheckInbox;
	private final String resetSuccessfully;
	private final String mailNotExists;
	private final String brokenLink;
	
	public PasswordService(UserService userService, ConfirmationTokenRepository confirmationTokenRepository, EmailSenderService emailSenderService, ConfirmationTokenService confirmationTokenService,
						   @Value("${spring.mail.username}") String email,
						   @Value("${messages.passwordService.resetSubject}") String resetSubject,
						   @Value("${messages.passwordService.resetRequestReceived}") String resetRequestReceived,
						   @Value("${messages.passwordService.resetRequestURL}") String resetRequestURL,
						   @Value("${messages.passwordService.resetCheckInbox}") String resetCheckInbox,
						   @Value("${messages.passwordService.resetSuccessfully}") String resetSuccessfully,
						   @Value("${messages.passwordService.mailNotExists}") String mailNotExists,
						   @Value("${messages.passwordService.brokenLink}") String brokenLink) {
		
		this.userService = userService;
		this.confirmationTokenRepository = confirmationTokenRepository;
		this.emailSenderService = emailSenderService;
		this.confirmationTokenService = confirmationTokenService;
		this.email = email;
		
		this.resetSubject = resetSubject;
		this.resetRequestReceived = resetRequestReceived;
		this.resetRequestURL = resetRequestURL;
		this.resetCheckInbox = resetCheckInbox;
		this.resetSuccessfully = resetSuccessfully;
		this.mailNotExists = mailNotExists;
		this.brokenLink = brokenLink;
	}
	
	public ModelAndView displayResetPassword(ModelAndView mav, User user) {
		mav.addObject("user", user);
		mav.setViewName("forgotPassword");
		return mav;
	}
	
	public ModelAndView forgotUserPassword(ModelAndView mav, User user) {
		Optional<User> existingUser = userService.findUserByEmail(user.getEmail());
		if(existingUser.isPresent()) {
			String baseURL = ServletUriComponentsBuilder.fromCurrentContextPath()
					.replacePath(null)
					.build()
					.toUriString();

			User userE = existingUser.get();
			ConfirmationToken confirmationToken = confirmationTokenService.createToken(userE);
			
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setTo(user.getEmail());
			mailMessage.setSubject(resetSubject);
			mailMessage.setFrom(email);
			mailMessage.setText(resetRequestReceived + baseURL + resetRequestURL + confirmationToken.getToken());
			
			emailSenderService.sendEmail(mailMessage);
			
			mav.addObject("message", resetCheckInbox);
			mav.setViewName("successForgotPassword");
			
		} else {
			mav.addObject("message", mailNotExists);
			mav.setViewName("error");
		}
		
		return mav;
	}
	
	public ModelAndView validateResetToken(ModelAndView mav, @RequestParam("token")String confirmationToken)
	{
		Optional<ConfirmationToken> oToken = confirmationTokenRepository.findByToken(confirmationToken);
		
		if(oToken.isPresent()) {
			ConfirmationToken token = oToken.get();
			Optional<User> existingUser = userService.findUserByEmail((token.getUser().getEmail()));
			if (existingUser.isPresent()) {
				User user = existingUser.get();
				user.setVerified(true);
				userService.save(user);
				mav.addObject("user", user);
				mav.addObject("emailId", user.getEmail());
				mav.setViewName("resetPassword2");
			}
		} else {
			mav.addObject("message", brokenLink);
			mav.setViewName("error");
		}
		return mav;
	}
	
	public ModelAndView resetUserPassword(ModelAndView mav, User user) {
		
		if(user.getEmail() != null) {
			Optional<User>  otTokenUser = userService.findUserByEmail(user.getEmail());
			if (otTokenUser.isPresent()){
				
				User tokenUser = otTokenUser.get();
				tokenUser.setPassword(encoder.encode(user.getPassword()));
				userService.save(tokenUser);
				mav.addObject("message", resetSuccessfully);
				mav.setViewName("successResetPassword");
			}
		}
		else {
			mav.addObject("message", brokenLink);
			mav.setViewName("error");
		}
		
		return mav;
	}
}
