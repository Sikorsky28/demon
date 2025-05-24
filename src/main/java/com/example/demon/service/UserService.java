package com.example.demon.service;

import com.example.demon.model.User;
import com.example.demon.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired private UserRepository userRepository;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    @Transactional
    public boolean register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return false;
        }
        userRepository.save(user);
        return true;
    }
}


