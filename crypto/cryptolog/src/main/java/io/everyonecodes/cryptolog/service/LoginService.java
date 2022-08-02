package io.everyonecodes.cryptolog.service;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
public class LoginService {
	
	private final UserService userService;
	
	public LoginService(UserService userService) {
		this.userService = userService;
	}
	
	public String getLoggedIn(HttpServletRequest request, Model model) {
		var oUser = userService.findUserByEmail("admin@gmail.com");
		if (oUser.isEmpty()){
			userService.saveAdmin(new User("admin", "admin@gmail.com", "Password1!" ));
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
				
				model.addAttribute("errorMessage", "Your email or password is incorrect. Please try again");
				
			} else if (errorMessage.equals("User is disabled")) {
				
				model.addAttribute("errorMessage", "Your email is not validated! Resend <a href=\"/verification\">Verification Link</a>?");
				
			} else {
				model.addAttribute("errorMessage", errorMessage);
				
			}
		}
		return "login";
	}
}
