package com.example.Sereneflows.controller;

import com.example.Sereneflows.service.SongService;
import com.example.Sereneflows.service.YouTubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SongController {

    private final SongService songService;
    private final YouTubeService youTubeService;

    public SongController(SongService songService, YouTubeService youTubeService) {
        this.songService = songService;
        this.youTubeService = youTubeService;
    }
    @PostMapping("/extract")
    public List<String> extractSongs(@RequestBody String youtubeUrl) {
        System.out.println("API HIT");

        String videoId = songService.extractVideo(youtubeUrl);
        String description = youTubeService.getVideoDescription(videoId);
        System.out.println(description);
        return songService.extractSongs(description);

    }

}
