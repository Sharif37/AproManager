package com.example.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public User createUser(User user) {
        boolean IsUserExit= userRepository.existsByEmail(user.getEmail());
        if(IsUserExit){
            return null ;
        }else {
            User userData = new User(
                    user.getName(),
                    user.getEmail(),
                    user.getProfileUri(),
                    passwordEncoder.encode(user.getPassword()),
                    user.getSignUpDate()
            );

            return userRepository.save(userData);
        }
    }




    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }





    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user != null ) {

            return user;
        }



        return new User();
    }
    public boolean exitsByEmail(String email){
        return userRepository.existsByEmail(email );
    }


    public User authenticateUser(String email, String rawPassword) {
        User user = userRepository.findByEmail(email);

        if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {

            return user;
        }

        // Authentication failed

        return new User();
    }




}
