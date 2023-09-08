package com.kubperf.kubernetesperformanceback.controllers;

import com.kubperf.kubernetesperformanceback.services.SpotifyService;

import com.google.gson.JsonObject;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;




@RestController
public class SpotifyController {

        @Autowired
        public SpotifyService spotifyService;


        final static Logger logger = LoggerFactory.getLogger(SpotifyController.class);

        @GetMapping("/playlists")
        public JsonObject fetchPlaylists() {
            return spotifyService.fetchPlaylists();
        }

        @GetMapping("/artist/albums")
        public JsonObject fetchArtistAlbums() {
                return spotifyService.fetchArtistAlbums();
        }

        @GetMapping("/album/tracks")
        public JsonObject fetchAlbumTracks() {
                return spotifyService.fetchAlbumTracks();
        }

        @GetMapping("/fetchPlaylists")
        public JsonObject fetchOrSavePlaylists() {
                logger.warn("Playlist fetched");
                return spotifyService.fetchOrSavePlaylists();
        }
        @GetMapping("/fetchPlaylistsInvalid")
        public JsonObject fetchOrSavePlaylistsInvalid() {
                return spotifyService.fetchPlaylistsInvalid();
        }
        @GetMapping("/fetchPlaylistsNoDelay")
        public JsonObject fetchOrSavePlaylistsNoDelay() {
                return spotifyService.fetchOrSavePlaylistsNoDelay();
        }
}
