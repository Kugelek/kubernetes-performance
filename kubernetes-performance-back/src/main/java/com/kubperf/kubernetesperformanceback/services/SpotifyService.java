package com.kubperf.kubernetesperformanceback.services;

import com.kubperf.kubernetesperformanceback.models.User;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SpotifyService {

    @Autowired
    public User user;

    private final RestTemplate restTemplate;

    public SpotifyService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }



    public JSONObject fetchSongs(String token) {
        String url = "https://api.spotify.com/v1/users/adamzebr/playlists";

        //create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token);

        String urlTemplate = UriComponentsBuilder
                .fromHttpUrl(url)
                .build().toUriString();

        HttpEntity<?> request = new HttpEntity<Object>(null, headers);
        ResponseEntity<JSONObject> response = restTemplate.exchange(urlTemplate, HttpMethod.GET, request, JSONObject.class);

        System.out.println(response.getBody());
        return response.getBody();

    }


//
//    public JSONObject getSongs(String token) {
//
//        return songDao.findAll();
//    }

}
