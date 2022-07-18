package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.data.ConfirmationToken;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.repository.ConfirmationTokenRepository;
import io.everyonecodes.cryptolog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;

    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository, UserRepository userRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.userRepository = userRepository;
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public ConfirmationToken createToken(User user) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, user, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15));
        confirmationTokenRepository.save(confirmationToken);
        return confirmationToken;


    }

    @Transactional
    public ModelAndView confirmToken(String token) {
        ModelAndView modelAndView = new ModelAndView();
        ConfirmationToken confirmationToken = getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            modelAndView.setViewName("alreadyconfirmed");
            return modelAndView;
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {

            confirmationTokenRepository.delete(confirmationToken);
            userRepository.delete(confirmationToken.getUser());
            modelAndView.setViewName("expired");
            return modelAndView;
        }

        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(confirmationToken);
        User user = confirmationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);
        modelAndView.setViewName("confirmed");
        return modelAndView;
    }
}