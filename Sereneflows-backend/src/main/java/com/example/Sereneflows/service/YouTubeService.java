package com.example.Sereneflows.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class YouTubeService {

    @Value("${youtube.api.key}")
    private String apiKey;

    public String getVideoDescription(String videoId)
    {
        String url = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id="
                + videoId + "&key=" + apiKey;
        RestTemplate restTemplate = new RestTemplate();
        Map response  = restTemplate.getForObject(url,Map.class);
        try {
            var items = (java.util.List<Map>)response.get("items");
            var snippet = (Map) items.get(0).get("snippet");
            return (String) snippet.get("description");
        }catch (Exception e)
        {
            return "";
        }
    }
}
