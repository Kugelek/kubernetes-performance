package com.kubperf.kubernetesperformanceback.controllers;

import com.kubperf.kubernetesperformanceback.services.AuthoriaztionService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizationController {

    @Autowired
    public AuthoriaztionService authoriaztionService;

    @GetMapping("/authorizationUser")
    public JSONObject authorizationUser() {
        return authoriaztionService.authorization();
    }
}
