package com.kubperf.kubernetesperformanceback.controllers;

import com.kubperf.kubernetesperformanceback.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@Controller
public class LoginController {

    @Autowired
    public User user;

    @GetMapping("/login")
    public String login(@PathVariable(value = "code") String code) {
        System.out.println("Code = " + code);
        return "start";
    }

}
