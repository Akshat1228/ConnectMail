package com.example.emailservice.controllers;

import com.example.emailservice.models.User;
import com.example.emailservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        userService.registerUser(user);
        model.addAttribute("successMessage", "Registration successful! Please sign in.");
        return "/signin";
    }

    @GetMapping("/signin")
    public String showSignInForm() {
        return "signin";
    }
}
