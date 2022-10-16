package com.kubperf.kubernetesperformanceback.services;

import com.kubperf.kubernetesperformanceback.models.User;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class AuthoriaztionService {

    @Autowired
    public User user;

    private final static String API_PREFIX = "https://accounts.spotify.com/";
    private final static String ACCESS_TOKEN = "access_token";

    private final RestTemplate restTemplate;


    public AuthoriaztionService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }


    public JSONObject requestUserAuthorization() {
        String url = API_PREFIX + "authorize";

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("client_id", "32f3fa6fa3f54efcae8193f8d7da82c9")
                .queryParam("response_type", "code")
                .queryParam("redirect_uri","http://127.0.0.1:8080/login").build().toUriString();

        System.out.println("urlTemplate = " + urlTemplate);

        //create request
        HttpEntity<?> request = new HttpEntity<Object>(null, null);


        // make a request
        ResponseEntity<JSONObject> response = restTemplate.exchange(urlTemplate, HttpMethod.GET, request, JSONObject.class);

        return response.getBody();


    }

    public JSONObject authorization() {
        String url = API_PREFIX + "api/token";

        //create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("32f3fa6fa3f54efcae8193f8d7da82c9","55649f1a2f6b47c2966574c62ac6d0e1");

        //create body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "client_credentials");

        //create request
        HttpEntity<?> request = new HttpEntity<Object>(body, headers);

        // make a request
        ResponseEntity<JSONObject> response = restTemplate.exchange(url, HttpMethod.POST, request, JSONObject.class);

        user.setToken(response.getBody().get(ACCESS_TOKEN).toString());

        return response.getBody();
    }



}
