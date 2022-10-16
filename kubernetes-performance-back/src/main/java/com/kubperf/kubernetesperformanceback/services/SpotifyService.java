package com.kubperf.kubernetesperformanceback.services;

import com.kubperf.kubernetesperformanceback.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SpotifyService {

    @Autowired
    public User user;

    private final RestTemplate restTemplate;

    public SpotifyService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }


}
