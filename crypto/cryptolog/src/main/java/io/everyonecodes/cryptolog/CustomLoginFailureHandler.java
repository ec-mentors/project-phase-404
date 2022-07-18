package io.everyonecodes.cryptolog;


import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.repository.UserRepository;
import io.everyonecodes.cryptolog.service.LoginAttemptsEmailService;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Service
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final UserRepository userRepository;
    private final LoginAttemptsEmailService sendFailedLoginEmail;

    public CustomLoginFailureHandler(UserRepository userRepository, LoginAttemptsEmailService sendFailedLoginEmail) {
        this.userRepository = userRepository;
        this.sendFailedLoginEmail = sendFailedLoginEmail;
    }


    public void updateFailedLoginAttempts(User user) {
        user.setLoginAttempts(user.getLoginAttempts() + 1);
        userRepository.save(user);
    }


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("email");
        var oUser = userRepository.findByEmail(email);
        if (oUser.isPresent()){
            User user = oUser.get();
                if (user.isVerified()) {
                    if (user.getLoginAttempts() <= 2) {
                        updateFailedLoginAttempts(user);
                    }
                    if (user.getLoginAttempts() >= 3) {
                        sendFailedLoginEmail.sendEmailLoginFail(user);
                    }
                }

            }
        super.setDefaultFailureUrl("/login?error=true");
        super.onAuthenticationFailure(request, response, exception);
        }


    }


