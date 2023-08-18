package com.kubperf.kubernetesperformanceback.controllers;

import com.kubperf.kubernetesperformanceback.services.AuthorizationService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizationController {


    @Autowired
    public AuthorizationService authorizationService;


    @GetMapping("requestUserAuthorization")
    public JSONObject requestUserAuthorization() {
        return authorizationService.requestUserAuthorization();
    }


    @GetMapping("/authorizationUser")
    public JSONObject authorizationUser() {
        return authorizationService.authorization();
    }
}
