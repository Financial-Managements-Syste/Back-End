package com.example.User_Service.service;

import com.example.User_Service.entity.User;
import com.example.User_Service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User register(User user) {
        // Copy transient plain password to password_hash
        if (user.getPassword() != null) {
            user.setPassword_hash(user.getPassword()); // here you can hash if needed
        }

        // Optionally set created_at if null
        if (user.getCreated_at() == null) {
            user.setCreated_at(java.time.LocalDateTime.now().toString());
        }

        return userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword_hash().equals(password)) {
            return user;
        }
        return null;
    }
}
