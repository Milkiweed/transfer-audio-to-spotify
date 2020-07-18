package com.vkotify.transfer;

import com.vkotify.transfer.entities.TrackEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

@SpringBootApplication
@RestController
public class AuthorizationServer {
    private final static Properties properties = new Properties();
    private static Spotify spotify;
    private static AudioClient audioClient;

    public static void main(String[] args) {
        // load system properties
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("application.properties");
        try {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // define spotify api class
        spotify = new Spotify(properties.getProperty("client_id"),
                properties.getProperty("client_secret"),
                properties.getProperty("redirect_uri"));

        // define client for parse VK audio
        audioClient = new AudioClient()
                .setParseEndpoint(properties.getProperty("parse_endpoint"));

        // run server
        SpringApplication.run(AuthorizationServer.class, args);
    }

    @GetMapping("/spotify-authorization")
    public String spotifyAuthorization(@RequestParam(value = "code") String code) {
        spotify.authorizeByCode(code);
        try {
            List<TrackEntity> trackEntityList = audioClient.getAudio("riffglitchard");
            spotify.transferAudio(trackEntityList);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return "Success!";
    }

    @CrossOrigin(origins = "null")
    @GetMapping("/auth_link")
    public Map<String, String> getAuthLink() {
        URI uri = spotify.authorizationCodeUri();
        HashMap<String, String> map = new HashMap<>();
        map.put("link", uri.toString());
        return map;
    }

}
