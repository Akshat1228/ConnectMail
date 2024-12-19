package com.example.emailservice.services;

import com.example.emailservice.models.User;
import com.example.emailservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println("Password encoded: " + user.getPassword());
        userRepository.save(user);
    }
}
