package com.example.Sereneflows.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

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
    public String searchSong(@RequestParam String query, @RequestParam String token) {

        RestTemplate restTemplate = new RestTemplate();

        try {

            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            String url = "https://api.spotify.com/v1/search?q=" + encodedQuery + "&type=track&limit=1";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            Map response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();

            return response.toString();

        } catch (Exception e) {
            return "Error searching song: " + e.getMessage();
        }
    }
}