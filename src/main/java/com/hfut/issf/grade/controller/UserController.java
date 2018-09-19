package com.hfut.issf.grade.controller;

import com.hfut.issf.grade.domain.User;
import com.hfut.issf.grade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signIn")
    public String singIn(String username, String password) {
        User user = userService.signIn(username, password);
        return null;
    }
}
