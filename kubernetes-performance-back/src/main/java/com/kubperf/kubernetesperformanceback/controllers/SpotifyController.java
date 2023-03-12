package com.kubperf.kubernetesperformanceback.controllers;

import com.kubperf.kubernetesperformanceback.services.SpotifyService;

import net.minidev.json.JSONObject;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class SpotifyController {

        @Autowired
        public SpotifyService spotifyService;




        final static Logger logger = LoggerFactory.getLogger(SpotifyController.class);



        private RestTemplate restTemplate;

//        @Value("${spring.application.name}")
//        private String applicationName;

        public  SpotifyController(RestTemplate restTemplate) {
                this.restTemplate = restTemplate;
        }
        @GetMapping("/path1")
        public ResponseEntity<String> path1() {
                logger.info("Incoming request at {} for request /path1 ", "app1");
                String response = restTemplate.getForObject("http://localhost:8090/path2", String.class);
                return ResponseEntity.ok("response from /path1 + " + response);
        }
        @GetMapping("/path2")
        public ResponseEntity<String>  path2() {
                logger.info("Incoming request at {} at /path2", "app2");
                return ResponseEntity.ok("response from /path2 ");
        }

        @GetMapping("/playlists")
        public JSONObject fetchPlaylists() {
            return spotifyService.fetchPlaylists();
        }

        @GetMapping("/artist/albums")
        public JSONObject fetchArtistAlbums() {
                return spotifyService.fetchArtistAlbums();
        }

        @GetMapping("/album/tracks")
        public JSONObject fetchAlbumTracks() {
                return spotifyService.fetchAlbumTracks();
        }

        @GetMapping("/fetchPlaylists")
        public JSONObject fetchOrSavePlaylists() {
                logger.warn("Playlist fetched");
                return spotifyService.fetchOrSavePlaylists();
        }

        @GetMapping("/fetchPlaylistsNoDelay")
        public JSONObject fetchOrSavePlaylistsNoDelay() {
                return spotifyService.fetchOrSavePlaylistsNoDelay();
        }

}
