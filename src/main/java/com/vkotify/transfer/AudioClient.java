package com.vkotify.transfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vkotify.transfer.entities.TrackEntity;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

public class AudioClient {
    private String parseEndpoint;

    // parse user tracklist from 3rd party service
    // example of structure:   {
    //    "id": 456239350,
    //    "owner_id": 560237477,
    //    "url": "",
    //    "artist": "ПЕТУХИНЕЗИС",
    //    "title": "В книге всё было по-другому (17 Независимый, раунд 4)",
    public List<TrackEntity> getAudio(String username) throws IOException, InterruptedException {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("target", username);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(payload);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(parseEndpoint))
                .timeout(Duration.ofMinutes(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse response = HttpClient.newHttpClient()
                .send(httpRequest, HttpResponse.BodyHandlers.ofString());

        List<TrackEntity> trackEntityList = new ArrayList<>();
        JsonArray jsonArray = JsonParser.parseString(response.body().toString()).getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            TrackEntity trackEntity = new TrackEntity();
            String artist = jsonArray.get(i).getAsJsonObject().get("artist").getAsString();
            String title = jsonArray.get(i).getAsJsonObject().get("title").getAsString();
            trackEntity.setArtist(artist);
            trackEntity.setTitle(title);
            trackEntityList.add(trackEntity);
        }
        return trackEntityList;
    }

    public AudioClient setParseEndpoint(String parseEndpoint) {
        this.parseEndpoint = parseEndpoint;
        return this;
    }
}
