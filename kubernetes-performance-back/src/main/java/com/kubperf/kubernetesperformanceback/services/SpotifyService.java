package com.kubperf.kubernetesperformanceback.services;

import com.kubperf.kubernetesperformanceback.models.User;
import com.sun.management.OperatingSystemMXBean;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/*

 *   1. Counter - ilość żądań o playlistę + dwie dodatkowe metryki- żądania pomyślne i niepomyślne (result=success/error) (DONE)
            todo: dodać tagowanie dynamiczne wg kodu statusu (np. 200, 401, 500)
 *   2. Gauge - error rate (DONE)
 * todo:
 *   3. Histogram - a) zbiór czasów zapytań o playlisty / b) średnia czasu zapytania o playlistę (dodanie listy przechowującą czasy i obliczanie ich średniej)
 *   4. Summary - to samo i porównamy sobie jego użycie z histogramem
*  */

@Service
public class SpotifyService {

    @Autowired
    public User user;

    public JSONObject tempPlaylists;

    private final static String API_PREFIX = "https://api.spotify.com/v1/";
    private final static String USER = "21h3z55pecogcrafiq3bvnady";
    private final static String ARTIST = "5LHRHt1k9lMyONurDHEdrp";
    private final static String ALBUM = "6hHIX3lfGKnZ2ji41YZMVV";
    private final Counter playlistsSuccessfulCounter = Metrics.counter("playlists_requests_total","result", "success");
    private final Counter playlistsFailedCounter = Metrics.counter("playlists_requests_total","result", "error");
    private final AtomicReference<Double> playlistErrorRate = new AtomicReference<>(0.0);
    private final List<Long> requestTimesList = new ArrayList<>();
    private final AtomicReference<Double> usage = new AtomicReference<>(0.0);

    @PostConstruct
    public void setGauge() {
        Metrics.globalRegistry.gauge("cpu_usage_gauge", Collections.emptyList(), usage, AtomicReference::get);
        Metrics.globalRegistry.gauge("albums_request_avg_time",
                Collections.emptyList(),
                requestTimesList,
                x -> x.stream().mapToDouble(Long::doubleValue).average().orElse(0.0)
        );
        Metrics.globalRegistry.gauge("playlists_error_rate", Collections.emptyList(), playlistErrorRate, AtomicReference::get);
    }
    private final RestTemplate restTemplate;
    Logger logger = LoggerFactory.getLogger(SpotifyService.class);
    public SpotifyService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public JSONObject makeRequest(String url) {

        ResponseEntity<JSONObject> response;
        long startTime = System.nanoTime();;
        try {
            //create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(user.getToken());

            String urlTemplate = UriComponentsBuilder
                    .fromHttpUrl(url)
                    .build().toUriString();
            HttpEntity<?> request = new HttpEntity<Object>(null, headers);
            response = restTemplate.exchange(urlTemplate, HttpMethod.GET, request, JSONObject.class);
            long responseTime = System.nanoTime() - startTime;
            if(url.contains("albums")) {
                requestTimesList.add(responseTime);
            }

            if(isPlaylistRequest(url)) {
                playlistsSuccessfulCounter.increment();
            }
            logger.info(String.format("Request %s is successful", url));
            updateGauge();

            return response.getBody();

        } catch (Exception e) {
            if(isPlaylistRequest(url))
                playlistsFailedCounter.increment();
            if(url.contains("albums")) {
                long responseTime = System.nanoTime() - startTime;
                requestTimesList.add(responseTime / 1000000);
            }
            updateGauge();
            throw e;
        }
    }
    public JSONObject makeUnauthorizedRequest(String url) {

        //create headers
        ResponseEntity<JSONObject> response;
        try {
            HttpHeaders headers = new HttpHeaders();
            String urlTemplate = UriComponentsBuilder
                    .fromHttpUrl(url)
                    .build().toUriString();

            HttpEntity<?> request = new HttpEntity<Object>(null, headers);
            response = restTemplate.exchange(urlTemplate, HttpMethod.GET, request, JSONObject.class);

            if(isPlaylistRequest(url)) {
                playlistsSuccessfulCounter.increment();
                updateGauge();
            }
            return response.getBody();

        } catch (Exception e) {
            if(isPlaylistRequest(url)) {
                playlistsFailedCounter.increment();
                updateGauge();
            }
            throw e;

        }
    }


    public JSONObject fetchPlaylists() {
        String url = API_PREFIX + "users/" + USER + "/playlists";
        logger.info("request fetchPlaylists (GET) started");
        return makeRequest(url);
    }
    public JSONObject fetchPlaylistsInvalid() {
        String url = API_PREFIX + "users/" + USER + "/playlists";
        logger.info("request fetchPlaylistsInvalid (GET) started");
        return makeUnauthorizedRequest(url);
    }

    public JSONObject fetchArtistAlbums() {
        String url = API_PREFIX + "artists/" + ARTIST + "/albums";
        logger.info("request fetchArtistAlbums (GET) started");
        return makeRequest(url);
    }

    public JSONObject fetchAlbumTracks() {
        String url = API_PREFIX + "albums/" + ALBUM + "/tracks";
        logger.info("request fetchAlbumTracks (GET) started");
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
            ie.printStackTrace();
        }
    }
    public void updateGauge() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        Long cpuUsage = 0L;
        for(Long threadID : threadMXBean.getAllThreadIds()) {
//            logger.info("*********");
//            logger.info(String.format("Thread name: %s", threadMXBean.getThreadInfo(threadID).getThreadName()));
//            logger.info(String.format("State: %s", threadMXBean.getThreadInfo(threadID).getThreadState()));
//            logger.info(String.format("Cpu time: %d", threadMXBean.getThreadCpuTime(threadID)));
//            if(threadMXBean.getThreadInfo(threadID).getThreadName().contains("api.spotify.com"));
            cpuUsage += threadMXBean.getThreadCpuTime(threadID);
        }
        logger.info(String.format("App cpu usage (nanoseconds): %d", cpuUsage));
        logger.info(String.format("Process CPU Usage: %d", operatingSystemMXBean.getProcessCpuTime()));
        usage.set((cpuUsage.doubleValue() / operatingSystemMXBean.getProcessCpuTime()) * 100);
        logger.info(String.format("cpu_usage_gauge = %f",Metrics.globalRegistry.get("cpu_usage_gauge").gauge().value()));
        playlistErrorRate.set((playlistsFailedCounter.count() / (playlistsFailedCounter.count() + playlistsSuccessfulCounter.count())) * 100.0);
    }

    public boolean isPlaylistRequest(String url){
        return url.contains("playlists");
    }
}
