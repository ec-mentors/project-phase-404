package io.everyonecodes.cryptolog.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ConfirmationTokenServiceTest {
	
	@Autowired
	ConfirmationTokenService confirmationTokenService;
	
	@MockBean ConfirmationTokenRepository confirmationTokenRepository;
	
	@MockBean UserRepository userRepository;
	
	
	@Test
	void getToken_Found() {
		String token = "userUUID";
		User user = new User(1, "test", "test@email.com");
		
		ConfirmationToken expected = new ConfirmationToken(token, user, LocalDateTime.now(), LocalDateTime.now());
		
		Mockito.when(confirmationTokenRepository.findByToken(token)).thenReturn(Optional.of(expected));
		
		Optional<ConfirmationToken> result = confirmationTokenService.getToken(token);
		
		Assertions.assertEquals(Optional.of(expected), result);
		
		Mockito.verify(confirmationTokenRepository).findByToken(token);
	}
	
	@Test
	void getToken_NotFound() {
		String token = "userUUID";
		
		Mockito.when(confirmationTokenRepository.findByToken(token)).thenReturn(Optional.empty());
		
		Optional<ConfirmationToken> result = confirmationTokenService.getToken(token);
		
		Assertions.assertEquals(Optional.empty(), result);
		
		Mockito.verify(confirmationTokenRepository).findByToken(token);
	}
	
	@Test
	void createToken() {
		User user = new User(1, "test", "test@email.com");
		
		ConfirmationToken confirmationToken = new ConfirmationToken(null, user, null, null);
		
		Assertions.assertNull(confirmationToken.getToken());
		
		confirmationToken = confirmationTokenService.createToken(user);
		Assertions.assertNotNull(confirmationToken.getToken());
		
		Mockito.verify(confirmationTokenRepository).save(confirmationToken);
	}
	
	@Test
	void confirmToken_shouldThrowExceptionWhenTokenIsNotFound() {
		String token = "doesn't exist";
		
		Assertions.assertThrows(IllegalStateException.class,
		                        () -> confirmationTokenService.confirmToken(token));
	}
	
	@Test
	void confirmToken_tokenGetsConfirmed() {
		User user = new User(1, "test", "test@email.com");

		String token = "test-token";
		ConfirmationToken confirmationToken = new ConfirmationToken(token, user, LocalDateTime.now(), null, LocalDateTime.now().plusMinutes(15));
		
		Mockito.when(confirmationTokenRepository.findByToken(confirmationToken.getToken())).thenReturn(Optional.of(confirmationToken));
		
		Assertions.assertNull(confirmationToken.getConfirmedAt());
		Assertions.assertFalse(confirmationToken.getUser().isVerified());
		
		ModelAndView modelAndView = confirmationTokenService.confirmToken(token);
		
		Assertions.assertNotNull(confirmationToken.getConfirmedAt());
		Assertions.assertTrue(confirmationToken.getUser().isVerified());
		Assertions.assertEquals("confirmed", modelAndView.getViewName());
		
		Mockito.verify(confirmationTokenRepository).findByToken(token);
		Mockito.verify(confirmationTokenRepository).save(confirmationToken);
		Mockito.verify(userRepository).save(confirmationToken.getUser());
		Mockito.verify(confirmationTokenRepository, Mockito.never()).delete(confirmationToken);
	}
	
	
	@Test
	void confirmToken_tokenAlreadyConfirmed() {
		
		User user = new User(1, "test", "test@email.com");
		
//		user.setVerified(true);
		
		String token = "test-token";
		ConfirmationToken confirmationToken = new ConfirmationToken(token, user, LocalDateTime.now().plusMinutes(2), LocalDateTime.now().plusMinutes(3), LocalDateTime.now().plusMinutes(15));
		
		Mockito.when(confirmationTokenRepository.findByToken(confirmationToken.getToken())).thenReturn(Optional.of(confirmationToken));
		
		ModelAndView modelAndView = confirmationTokenService.confirmToken(token);
		
		Assertions.assertEquals("alreadyconfirmed", modelAndView.getViewName());
		
		Mockito.verify(confirmationTokenRepository).findByToken(token);
		Mockito.verify(confirmationTokenRepository, Mockito.never()).save(confirmationToken);
		Mockito.verify(userRepository, Mockito.never()).save(confirmationToken.getUser());
		Mockito.verify(userRepository, Mockito.never()).delete(confirmationToken.getUser());
		Mockito.verify(confirmationTokenRepository, Mockito.never()).delete(confirmationToken);
	}
	
	@Test
	void confirmToken_tokenIsExpired() {
		User user = new User(1, "test", "test@email.com");
		
		String token = "test-token";
		
		ConfirmationToken confirmationToken = new ConfirmationToken(token, user, LocalDateTime.now().minusMinutes(30), null, LocalDateTime.now().minusMinutes(15));
		
		Mockito.when(confirmationTokenRepository.findByToken(confirmationToken.getToken())).thenReturn(Optional.of(confirmationToken));
		
		ModelAndView modelAndView = confirmationTokenService.confirmToken(token);
		
		Assertions.assertEquals("expired", modelAndView.getViewName());
		
		Mockito.verify(confirmationTokenRepository).findByToken(token);
		Mockito.verify(confirmationTokenRepository, Mockito.never()).save(confirmationToken);
		Mockito.verify(userRepository, Mockito.never()).save(confirmationToken.getUser());
		Mockito.verify(userRepository).delete(confirmationToken.getUser());
		Mockito.verify(confirmationTokenRepository).delete(confirmationToken);
	}
}