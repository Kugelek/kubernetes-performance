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

    public JSONObject tempPlaylists;

    private final static String API_PREFIX = "https://api.spotify.com/v1/";
    private final static String USER = "adamzebr";
    private final static String ARTIST = "5LHRHt1k9lMyONurDHEdrp";
    private final static String ALBUM = "6hHIX3lfGKnZ2ji41YZMVV";

    private final RestTemplate restTemplate;

    public SpotifyService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public JSONObject makeRequest(String url) {
        //create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());

        String urlTemplate = UriComponentsBuilder
                .fromHttpUrl(url)
                .build().toUriString();

        HttpEntity<?> request = new HttpEntity<Object>(null, headers);
        ResponseEntity<JSONObject> response = restTemplate.exchange(urlTemplate, HttpMethod.GET, request, JSONObject.class);

        System.out.println(response.getBody());
        return response.getBody();
    }


    public JSONObject fetchPlaylists() {
        String url = API_PREFIX + "users/" + USER + "/playlists";
        return makeRequest(url);
    }

    public JSONObject fetchArtistAlbums() {
        String url = API_PREFIX + "artists/" + ARTIST + "/albums";
        return makeRequest(url);
    }

    public JSONObject fetchAlbumTracks() {
        String url = API_PREFIX + "albums/" + ALBUM + "/tracks";
        return makeRequest(url);
    }

    public JSONObject fetchOrSavePlaylists() {
        if(tempPlaylists != null){
            simulateComplexComputation();
            System.out.println("@@@@@@@@@@@");
            return tempPlaylists;
        }else{
            String url = API_PREFIX + "users/" + USER + "/playlists";
            this.tempPlaylists = makeRequest(url);
            return this.tempPlaylists;
        }
    }
    public JSONObject fetchOrSavePlaylistsNoDelay() {
        if(tempPlaylists != null){
            return tempPlaylists;
        }else{
            String url = API_PREFIX + "users/" + USER + "/playlists";
            this.tempPlaylists = makeRequest(url);
            return this.tempPlaylists;
        }
    }




    public void simulateComplexComputation(){
        try{
            Thread.sleep(2000);
        }catch(InterruptedException ie){

        }
    }

}
