package com.kubperf.kubernetesperformanceback.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kubperf.kubernetesperformanceback.models.User;
import com.sun.management.OperatingSystemMXBean;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
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
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collections;
import java.util.Objects;
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

    JsonObject tempPlaylists;

    private static final String API_PREFIX = "https://api.spotify.com/v1/";
    private static final String USER_ID = "21h3z55pecogcrafiq3bvnady";
    private static final String ARTIST = "5LHRHt1k9lMyONurDHEdrp";
    private static final String ALBUM = "6hHIX3lfGKnZ2ji41YZMVV";
    private final Counter playlistsSuccessfulCounter = Metrics.counter("playlists_requests_total","result", "success");
    private final Counter playlistsErrorCounter = Metrics.counter("playlists_requests_total","result", "error");
    private final AtomicReference<Double> playlistErrorRate = new AtomicReference<>(0.0);
    private final AtomicReference<Double> usage = new AtomicReference<>(0.0);
    DistributionSummary responseSizesHistogram = DistributionSummary.builder("response.sizes")
            .description("Histogram for response sizes")
            .baseUnit("bytes")
            .publishPercentileHistogram()
            .publishPercentiles(0.1, 0.25, 0.5, 0.75, 0.9, 0.95)
            .distributionStatisticExpiry(Duration.ofHours(6))
            .register(Metrics.globalRegistry);

    DistributionSummary responseSizesSummary = Metrics.globalRegistry.summary("response.size.summary");

    @PostConstruct
    public void init() {
        Metrics.globalRegistry.gauge("cpu_usage_gauge", Collections.emptyList(), usage, AtomicReference::get);
        Metrics.globalRegistry.gauge("playlists_error_rate", Collections.emptyList(), playlistErrorRate, AtomicReference::get);
    }
    private final RestTemplate restTemplate;
    Logger logger = LoggerFactory.getLogger(SpotifyService.class);
    public SpotifyService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public JsonObject makeRequest(String url) {

        ResponseEntity<JsonObject> response = null;
        long startTime = System.nanoTime();
            //create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(user.getToken());

            String urlTemplate = UriComponentsBuilder
                    .fromHttpUrl(url)
                    .build().toUriString();
        try {
            HttpEntity<?> request = new HttpEntity<Object>(null, headers);
            response = restTemplate.exchange(urlTemplate, HttpMethod.GET, request, JsonObject.class);
            if(response.hasBody())
                logger.info(response.getBody().toString());
            else
                logger.info("NO body");

            JsonObject responseBody = (url.contains("albums") && response.hasBody()) ? trimResponse(response) : response.getBody();

            int responseSize = responseBody.toString().getBytes().length;

            responseSizesHistogram.record(responseSize);
            responseSizesSummary.record(responseSize);

            if(isPlaylistRequest(url)) {
                playlistsSuccessfulCounter.increment();
            }
            logger.info(String.format("Request %s is successful", url));
            updateGauge();

            return responseBody;

        } catch (Exception e) {
            responseSizesHistogram.record(e.toString().getBytes().length);
            responseSizesSummary.record(e.toString().getBytes().length);
            if(isPlaylistRequest(url))
                playlistsErrorCounter.increment();

            updateGauge();
            throw e;
        }
    }
    public JsonObject makeUnauthorizedRequest(String url) {

        //create headers
        ResponseEntity<JsonObject> response = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            String urlTemplate = UriComponentsBuilder
                    .fromHttpUrl(url)
                    .build().toUriString();

            HttpEntity<?> request = new HttpEntity<Object>(null, headers);
            response = restTemplate.exchange(urlTemplate, HttpMethod.GET, request, JsonObject.class);
            responseSizesHistogram.record(response.getBody().toString().getBytes().length);
            responseSizesSummary.record(response.getBody().toString().getBytes().length);

            if(isPlaylistRequest(url)) {
                playlistsSuccessfulCounter.increment();
                updateGauge();
            }
            return response.getBody();

        } catch (Exception e) {
            responseSizesHistogram.record(e.toString().getBytes().length);
            responseSizesSummary.record(e.toString().getBytes().length);
            if(isPlaylistRequest(url)) {
                playlistsErrorCounter.increment();
                updateGauge();
            }
            throw e;

        }
    }


    public JsonObject fetchPlaylists() {
        String url = API_PREFIX + "users/" + USER_ID + "/playlists";
        logger.info("request fetchPlaylists (GET) started");
        return makeRequest(url);
    }
    public JsonObject fetchPlaylistsInvalid() {
        String url = API_PREFIX + "users/" + USER_ID + "/playlists";
        logger.info("request fetchPlaylistsInvalid (GET) started");
        return makeUnauthorizedRequest(url);
    }

    public JsonObject fetchArtistAlbums() {
        String url = API_PREFIX + "artists/" + ARTIST + "/albums";
        logger.info("request fetchArtistAlbums (GET) started");
        return makeRequest(url);
    }

    public JsonObject fetchAlbumTracks() {
        String url = API_PREFIX + "albums/" + ALBUM + "/tracks";
        logger.info("request fetchAlbumTracks (GET) started");
        return makeRequest(url);
    }

    public JsonObject fetchOrSavePlaylists() {
        if(tempPlaylists != null){
            simulateComplexComputation();
            System.out.println("@@@@@@@@@@@");
            responseSizesHistogram.record(tempPlaylists.toString().getBytes().length / 1000.0);
            return tempPlaylists;
        }else{
            String url = API_PREFIX + "users/" + USER_ID + "/playlists";
            this.tempPlaylists = makeRequest(url);
            return this.tempPlaylists;
        }
    }
    public JsonObject fetchOrSavePlaylistsNoDelay() {
        if(tempPlaylists != null){
            responseSizesHistogram.record(tempPlaylists.toString().getBytes().length / 1000.0);
            return tempPlaylists;
        }else{
            String url = API_PREFIX + "users/" + USER_ID + "/playlists";
            this.tempPlaylists = makeRequest(url);
            responseSizesHistogram.record(tempPlaylists.toString().getBytes().length / 1000.0);
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
        playlistErrorRate.set((playlistsErrorCounter.count() / (playlistsErrorCounter.count() + playlistsSuccessfulCounter.count())) * 100.0);
    }

    public boolean isPlaylistRequest(String url){
        return url.contains("playlists");
    }

    public JsonObject trimResponse(ResponseEntity<JsonObject> response) {
        JsonArray items = new JsonArray();
        logger.info(response.getBody().toString());
        JsonArray responseItems = ((JsonArray) response.getBody().get("items"));
        for(int i=0; i < responseItems.size(); i++){
            JsonObject item = (JsonObject) responseItems.get(i);
            item.remove("available_markets");
            items.add(item);
        }

        JsonObject trimmedResponse = new JsonObject();

        trimmedResponse.add("next", response.getBody().get("next"));
        trimmedResponse.add("total", response.getBody().get("total"));
        trimmedResponse.add("offset", response.getBody().get("offset"));
        trimmedResponse.add("previous", response.getBody().get("previous"));
        trimmedResponse.add("limit", response.getBody().get("limit"));
        trimmedResponse.add("href", response.getBody().get("href"));
        trimmedResponse.add("items", items);
        return trimmedResponse;
    }
}
