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
	private final String u;
	private final String title;
	private final String er;
	private final String emId;
	private final String resetPw;
	private final String successRPw;
	private final String forgotPw;
	private final String successFPw;

	public PasswordService(UserService userService, ConfirmationTokenRepository confirmationTokenRepository, EmailSenderService emailSenderService, ConfirmationTokenService confirmationTokenService,
	                       @Value("${spring.mail.username}") String email,
	                       @Value("${messages.passwordService.resetSubject}") String resetSubject,
	                       @Value("${messages.passwordService.resetRequestReceived}") String resetRequestReceived,
	                       @Value("${messages.passwordService.resetCheckInbox}") String resetCheckInbox,
	                       @Value("${messages.passwordService.resetSuccessfully}") String resetSuccessfully,
	                       @Value("${messages.passwordService.mailNotExists}") String mailNotExists,
	                       @Value("${messages.passwordService.brokenLink}") String brokenLink,
	                       @Value("${messages.passwordService.u}") String u,
	                       @Value("${messages.passwordService.title}") String title,
	                       @Value("${messages.passwordService.er}") String er,
	                       @Value("${messages.passwordService.emId}") String emId,
	                       @Value("${messages.passwordService.resetPw}") String resetPw,
	                       @Value("${messages.passwordService.successRPw}") String successRPw,
	                       @Value("${messages.passwordService.forgotPw}") String forgotPw,
	                       @Value("${messages.passwordService.successFPw}") String successFPw)
			{
		
		this.userService = userService;
		this.confirmationTokenRepository = confirmationTokenRepository;
		this.emailSenderService = emailSenderService;
		this.confirmationTokenService = confirmationTokenService;
		this.email = email;
		
		this.resetSubject = resetSubject;
		this.resetRequestReceived = resetRequestReceived;
		this.resetCheckInbox = resetCheckInbox;
		this.resetSuccessfully = resetSuccessfully;
		this.mailNotExists = mailNotExists;
		this.brokenLink = brokenLink;
		this.u = u;
		this.title = title;
		this.er = er;
		this.emId = emId;
		this.resetPw = resetPw;
		this.successRPw = successRPw;
		this.forgotPw = forgotPw;
		this.successFPw = successFPw;
	}
	
	public ModelAndView displayResetPassword(ModelAndView mav, User user) {
		mav.addObject(u, user);
		mav.setViewName(forgotPw);
		return mav;
	}
	
	public ModelAndView forgotUserPassword(ModelAndView mav, User user) {
		Optional<User> existingUser = userService.findUserByEmail(user.getEmail());
		if(existingUser.isPresent()) {
			User userE = existingUser.get();
			ConfirmationToken confirmationToken = confirmationTokenService.createToken(userE);
			
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setTo(user.getEmail());
			mailMessage.setSubject(resetSubject);
			mailMessage.setFrom(email);
			mailMessage.setText(resetRequestReceived + confirmationToken.getToken());
			
			emailSenderService.sendEmail(mailMessage);
			
			mav.addObject(title, resetCheckInbox);
			mav.setViewName(successFPw);
		} else {
			mav.addObject(title, mailNotExists);
			mav.setViewName(er);
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
				mav.addObject(u, user);
				mav.addObject(emId, user.getEmail());
				mav.setViewName(resetPw);
			}
		} else {
			mav.addObject(title, brokenLink);
			mav.setViewName(er);
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
				mav.addObject(title, resetSuccessfully);
				mav.setViewName(successRPw);
			}
		} else {
			mav.addObject(title, brokenLink);
			mav.setViewName(er);
		}
		
		return mav;
	}
}
