package com.kubperf.kubernetesperformanceback.controllers;

import com.kubperf.kubernetesperformanceback.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {

    @Autowired
    public User user;

    @GetMapping("/")
    public String login() {
        user.setUserId("test");
        System.out.println("UserId = " + user.getUserId());
        return "login";
    }
}
