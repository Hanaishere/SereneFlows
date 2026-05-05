package com.example.Sereneflows.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
public class SpotifyController {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;


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
}