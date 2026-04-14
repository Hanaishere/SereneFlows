package com.example.Sereneflows.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.*;

@Service
public class SongService {
    public List<String> extractSongs(String desc)
    {
        List<String> songs = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d{1,2}:\\d{2}\\s+(.+)");
        Matcher matcher = pattern.matcher(desc);
        while (matcher.find()) {
            songs.add(matcher.group(1));
        }
        return songs;
    }
    public String extractVideo(String url){
        String[] parts = url.split("v=");
        if(parts.length > 1)
        {
            return parts[1];
        }
        return "";
    }
}
