package com.vkotify.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

public class Transfer {

    public static void main(String[] args) {
        // read the properties config
        Properties properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("application.properties");
        try {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Spotify spotify = new Spotify(properties.getProperty("client_id"),
                properties.getProperty("client_secret"),
                properties.getProperty("redirect_uri"));
        URI uri = spotify.authorizationCodeUri();
        System.out.println("URI is: " + uri);
    }
}
