package io.everyonecodes.registration.service;

import io.everyonecodes.registration.data.Role;
import io.everyonecodes.registration.data.User;
import io.everyonecodes.registration.repository.RoleRepository;
import io.everyonecodes.registration.repository.UserRepository;
import io.everyonecodes.registration.repository.UserService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImp implements UserService {
private final BCryptPasswordEncoder encoder;
private final RoleRepository roleRepository;
private final UserRepository userRepository;
private final JavaMailSender mailSender;

    public UserServiceImp(BCryptPasswordEncoder encoder, RoleRepository roleRepository, UserRepository userRepository, JavaMailSender mailSender) {
        this.encoder = encoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @Override
    public void saveUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setStatus("NOT_VERIFIED");
        Role userRole = roleRepository.findByRole("SITE_USER");
        user.setRoles(new HashSet<>(List.of(userRole)));
        userRepository.save(user);
    }

    @Override
    public boolean isUserAlreadyPresent(User user) {
        return userRepository.findByEmail(user.getEmail()).isPresent();
    }

}