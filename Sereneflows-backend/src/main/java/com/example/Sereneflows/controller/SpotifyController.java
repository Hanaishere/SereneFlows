package com.example.Sereneflows.controller;

import com.example.Sereneflows.service.SongService;
import com.example.Sereneflows.service.YouTubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@CrossOrigin("*")
@RestController
public class SpotifyController {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;

    @Autowired
    private SongService songService;
    @Autowired
    private YouTubeService youTubeService;

    @GetMapping("/login")
    public String login() {
        return "https://accounts.spotify.com/authorize"
                + "?client_id=" + clientId
                + "&response_type=code"
                + "&redirect_uri=" + redirectUri
                + "&scope=playlist-modify-public";
    }


    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code) {

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://accounts.spotify.com/api/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        try {
            Map response = restTemplate.postForObject(url, request, Map.class);

            // 👇 Return ONLY access token (easy for you)
            return (String) response.get("access_token");

        } catch (Exception e) {
            return "Error getting token: " + e.getMessage();
        }
    }


    @GetMapping("/search")
    public List<Map<String, String>> searchSong(
            @RequestParam String query,
            @RequestParam String token) {

        String url = "https://api.spotify.com/v1/search?q=" + query + "&type=track&limit=5";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map body = response.getBody();
        Map tracks = (Map) body.get("tracks");
        List<Map<String, Object>> items = (List<Map<String, Object>>) tracks.get("items");

        List<Map<String, String>> result = new ArrayList<>();

        for (Map<String, Object> item : items) {
            String name = (String) item.get("name");

            List<Map<String, Object>> artists = (List<Map<String, Object>>) item.get("artists");
            String artistName = (String) artists.get(0).get("name");

            Map externalUrls = (Map) item.get("external_urls");
            String spotifyUrl = (String) externalUrls.get("spotify");

            Map<String, String> songData = new HashMap<>();
            songData.put("name", name);
            songData.put("artist", artistName);
            songData.put("url", spotifyUrl);

            result.add(songData);
        }

        return result;
    }
    @GetMapping("/convert")
    public List<Map<String, String>> convert(
            @RequestParam String url) {
        String token = "BQC3bIWq5beWa6K1RACUJtN7dsBAzU73LOCGOJbkIKz5ruKyTS-8FK7iGWmJ_3jmoyDk3OKWs-gnln94Hheso1Enju6PJLyvGkECdDbtlg2XugLAREw5kn62RTBnQtDlO4hKYr_cIppx-2U2zKNBpOgBpBgW50LN9OPCgXi9W5zibVSSSTXyLY5Y_6IMP3MGgUK6Njbcx7jUyItClwWYmlz7VoB4qdfyu8f3WzBoAbfliHGAsPfBhqS2teIQ3ik_BJRRUYW1vwbvDp5tY5o";


        RestTemplate restTemplate = new RestTemplate();
        List<Map<String, String>> finalResult = new ArrayList<>();


        String videoId = songService.extractVideo(url);
        System.out.println("Video ID: " + videoId);


        String description = youTubeService.getVideoDescription(videoId);
        System.out.println("Description: " + description);


        List<String> songs = songService.extractSongs(description);
        System.out.println("Extracted songs: " + songs);


        for (String song : songs) {

            try {
                String searchUrl = "https://api.spotify.com/v1/search?q="
                        + URLEncoder.encode(song, StandardCharsets.UTF_8)
                        + "&type=track&limit=1";

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + token);

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<Map> response = restTemplate.exchange(
                        searchUrl,
                        HttpMethod.GET,
                        entity,
                        Map.class
                );

                Map body = response.getBody();
                Map tracks = (Map) body.get("tracks");
                List<Map<String, Object>> items =
                        (List<Map<String, Object>>) tracks.get("items");

                if (items != null && !items.isEmpty()) {
                    Map<String, Object> item = items.get(0);

                    String name = (String) item.get("name");

                    List<Map<String, Object>> artists =
                            (List<Map<String, Object>>) item.get("artists");
                    String artistName = (String) artists.get(0).get("name");

                    Map externalUrls = (Map) item.get("external_urls");
                    String spotifyUrl = (String) externalUrls.get("spotify");

                    Map<String, String> resultItem = new HashMap<>();
                    resultItem.put("youtubeSong", song);
                    resultItem.put("spotifyMatch", name);
                    resultItem.put("artist", artistName);
                    resultItem.put("url", spotifyUrl);

                    finalResult.add(resultItem);
                }

            } catch (Exception e) {
                System.out.println("Error processing song: " + song);
            }
        }

        return finalResult;
    }
}