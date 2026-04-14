package com.example.Sereneflows.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.*;

@Service
public class SongService {
    public List<String> extractSongs(String desc)
    {
        Set<String> songs = new LinkedHashSet<>();
        Pattern pattern = Pattern.compile("\\d{1,2}:\\d{2}\\s+(.+)");
        Matcher matcher = pattern.matcher(desc);
        while (matcher.find()) {
            String  song = matcher.group(1);
            songs.add(cleanSong(song));
        }
        return new ArrayList<>(songs);
    }
    public String extractVideo(String url){
        String[] parts = url.split("v=");
        if(parts.length > 1)
        {
            return parts[1];
        }
        return "";
    }
    private String cleanSong(String song)
    {
        return song
                .replaceAll("\\(.*?\\)","")
                .replaceAll("\\[.*?\\]","")
                .replaceAll("(?i)but it's lofi","")
                .replaceAll("(?i)lofi", "")
                .replaceAll("(?i)lyrics", "")
                .trim();
    }
}
