package com.kubperf.kubernetesperformanceback.controllers;

import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController  {

    @GetMapping("/test")
    public String getExample() {
        return "Test dfasdfsafsadfdas";
    }
}
