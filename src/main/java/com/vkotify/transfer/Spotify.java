package com.vkotify.transfer;

import com.vkotify.transfer.entities.TrackEntity;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.data.library.SaveTracksForUserRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Spotify {
    private final SpotifyApi spotifyApi;

    Spotify(String clientId, String clientSecret, String redirectUri) {
        spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(SpotifyHttpManager.makeUri(redirectUri))
                .build();
    }

    // Find track in Spotify by Artist & Title
    private Paging<Track> searchTrack(TrackEntity track) {
        SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(track.getArtist() + " " + track.getTitle()).build();
        try {
            final Paging<Track> trackPaging = searchTracksRequest.execute();
            System.out.println("Total: " + trackPaging.getTotal());
            return trackPaging;
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Add track into user account
    private void addTrackToUserLibrary(Track track) {
        SaveTracksForUserRequest saveTracksForUserRequest = spotifyApi.saveTracksForUser(track.getId()).build();
        try {
            final String string = saveTracksForUserRequest.execute();
            System.out.println("Null: " + string);
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            e.printStackTrace();
        }
    }

    public void transferAudio(List<TrackEntity> trackEntityList) throws InterruptedException {
        List<Track> tracks = new ArrayList<>();
        for (int i = 0; i < trackEntityList.size(); i++) {
            Track[] track = searchTrack(trackEntityList.get(i)).getItems();
            if (track.length < 1) {
                continue;
            }
            String Id = track[0].getId();
            String name = track[0].getName();
            System.out.println(Id + " " + name);

            TimeUnit.SECONDS.sleep(1);
            tracks.add(track[0]);
        }

        for (int i = 0; i < tracks.size(); i++) {
            addTrackToUserLibrary(tracks.get(i));
            TimeUnit.SECONDS.sleep(1);
        }
    }

    // build a URI for Authorization Code Flow.
    // Using the authorization code flow to retrieve an access token is necessary
    // if the requests are bound to a specific user.
    // Using this flow returns a refresh token, which can be used to renew the access token before it expires
    public URI authorizationCodeUri() {
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope("user-library-modify, user-library-read, playlist-modify-private, playlist-read-private, " +
                        "playlist-read-collaborative, user-read-private, user-read-email")
                .show_dialog(true)
                .build();

        return authorizationCodeUriRequest.execute();
    }

    public void authorizeByCode(String code) {
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();
        try {
            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            e.printStackTrace();
        }
    }
}
