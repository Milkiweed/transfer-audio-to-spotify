package com.vkotify.transfer.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackEntity {
    @SerializedName("artist")
    @Expose
    private String artist;
    @SerializedName("title")
    @Expose
    private String title;

    public String getArtist() {
        return artist;
    }

    public TrackEntity setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public TrackEntity setTitle(String title) {
        this.title = title;
        return this;
    }
}
