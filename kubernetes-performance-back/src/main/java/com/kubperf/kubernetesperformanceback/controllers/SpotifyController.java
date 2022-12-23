package com.kubperf.kubernetesperformanceback.controllers;

import com.kubperf.kubernetesperformanceback.services.SpotifyService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpotifyController {

        @Autowired
        public SpotifyService spotifyService;

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
                return spotifyService.fetchOrSavePlaylists();
        }

        @GetMapping("/fetchPlaylistsNoDelay")
        public JSONObject fetchOrSavePlaylistsNoDelay() {
                return spotifyService.fetchOrSavePlaylistsNoDelay();
        }

}
