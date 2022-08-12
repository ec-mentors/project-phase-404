package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.data.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
public class LoginService {
	
	private final UserService userService;
	private final String incorrectPassword;
	private final String emailNotValidated;
	private final String badCred;
	private final String errMess;
	private final String disabled;
	private final String login;
	
	public LoginService(UserService userService,
	                    @Value("${messages.login.incorrectPassword}") String incorrectPassword,
	                    @Value("${messages.login.emailNotValidated}") String emailNotValidated,
	                    @Value("${messages.login.getLoggedIn.badCred}") String badCred,
	                    @Value("${messages.login.getLoggedIn.errMess}") String errMess,
	                    @Value("${messages.login.getLoggedIn.disabled}") String disabled,
	                    @Value("${messages.login.getLoggedIn.login}") String login) {
		this.userService = userService;
		this.incorrectPassword = incorrectPassword;
		this.emailNotValidated = emailNotValidated;
		this.badCred = badCred;
		this.errMess = errMess;
		this.disabled = disabled;
		this.login = login;
	}
	
	public String getLoggedIn(HttpServletRequest request, Model model) {
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
			if (errorMessage.equals(badCred)) {
				
				model.addAttribute(errMess, incorrectPassword);
				
			} else if (errorMessage.equals(disabled)) {
				
				model.addAttribute(errMess, emailNotValidated);
				
			} else {
				model.addAttribute(errMess, errorMessage);
				
			}
		}
		
		return login;
	}
}
