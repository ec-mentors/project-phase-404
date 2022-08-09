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
	private final String admin;
	private final String adminEmail;
	private final String adminPassword;
	private final String incorrectPassword;
	private final String emailNotValidated;
	
	public LoginService(UserService userService,
						@Value("${admin.username}") String admin,
						@Value("${admin.email}") String adminEmail,
						@Value("${admin.password}") String adminPassword,
						@Value("${messages.login.incorrectPassword}") String incorrectPassword,
						@Value("${messages.login.emailNotValidated}") String emailNotValidated) {
		this.userService = userService;
		this.admin = admin;
		this.adminEmail = adminEmail;
		this.adminPassword = adminPassword;
		this.incorrectPassword = incorrectPassword;
		this.emailNotValidated = emailNotValidated;
	}
	
	public String getLoggedIn(HttpServletRequest request, Model model) {
		var oUser = userService.findUserByEmail(adminEmail);
		if (oUser.isEmpty()){
			userService.saveAdmin(new User(admin, adminEmail, adminPassword ));
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
				
				model.addAttribute("errorMessage", incorrectPassword);
				
			} else if (errorMessage.equals("User is disabled")) {
				
				model.addAttribute("errorMessage", emailNotValidated);
				
			} else {
				model.addAttribute("errorMessage", errorMessage);
				
			}
		}
		
		return "login";
	}
}
