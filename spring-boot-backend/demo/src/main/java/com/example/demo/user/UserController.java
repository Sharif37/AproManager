package com.example.demo.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/Signup")
    public User storeUserData(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/showAllMember")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }



    @GetMapping("/checkUser")
    public User checkUser(@RequestParam String email) {
        return userService.findByEmail(email);
    }

    @GetMapping("/login")
    public User checkPasswordMatch(@RequestParam String email, @RequestParam String password) {
        return userService.authenticateUser(email,password);
    }

}
