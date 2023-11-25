package com.example.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

   
    public User createUser(User user) {
        return userRepository.save(user);
    }

    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    
    public User updateUser(User user) {
        return userRepository.save(user);
    }

   

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

   
    public boolean checkPasswordMatch(String email, String password) {
        return userRepository.existsByEmailAndPassword(email, password);
    }
}
