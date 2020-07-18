package com.vkotify.transfer;

import com.vkotify.transfer.entities.TrackEntity;
import org.junit.jupiter.api.Test;

import javax.sound.midi.Track;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AudioClientTest {

    @Test
    void getAudio() throws IOException, InterruptedException {
        AudioClient audioClient = new AudioClient();
        List<TrackEntity> trackEntityList = audioClient.getAudio("riffglitchard");
        assertFalse(trackEntityList.isEmpty());
    }
}