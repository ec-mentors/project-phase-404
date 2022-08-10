package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.service.PasswordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PasswordConTest {
	
	@Autowired
	PasswordCon passwordController;
	
	@MockBean
	PasswordService passwordService;
	
	String urlForgetPw = "/forgot-password";
	
//	@Value("/")
//	String urlConfirmPw = "/confirm-reset";
	
	@Test
	void displayResetPassword() {
//		testRestTemplate.getForObject(urlForgetPw, ModelAndView.class);
		
//		Mockito.verify(passwordController).displayResetPassword();
//		Mockito.verify(passwordService).displayResetPassword();
		
	}
}