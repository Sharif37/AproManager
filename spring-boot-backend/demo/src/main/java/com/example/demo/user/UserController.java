package com.example.demo.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/storeUserData")
    public User storeUserData(@RequestBody User user) {
        System.out.println(user.getProfileuri());
        return userService.createUser(user);
    }

    @GetMapping("/getAllUsers")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/check-user")
    public boolean checkUser(@RequestParam String email) {
        return userService.existsByEmail(email);
    }

    @GetMapping("/checkPasswordMatch")
    public boolean checkPasswordMatch(@RequestParam String email, @RequestParam String password) {
        return userService.checkPasswordMatch(email, password);
    }
}
