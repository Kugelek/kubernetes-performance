package com.kubperf.kubernetesperformanceback.controllers;

import com.kubperf.kubernetesperformanceback.services.SpotifyService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SongsController {

        @Autowired
        public SpotifyService spotifyService;

        @GetMapping("/songs/fetch/{token}")
        public JSONObject fetchSongs(@PathVariable String token) {
            return spotifyService.fetchSongs(token);
        }

}
