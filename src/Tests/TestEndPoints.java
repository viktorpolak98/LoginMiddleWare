package Tests;

import Controller.CallerController;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class TestEndPoints {
    private static URL url;

    @BeforeAll
    public static void setUp(){
        new CallerController(System.getenv("mockDbUrl"), System.getenv("AllowedHostsConfig"));
        try {
            url = URI.create(System.getenv("TestUrl")).toURL();
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        //If either assumption fails no tests should run
        Assumptions.assumeFalse(url == null);
        Assumptions.assumeFalse(url.getPath().isBlank());
    }

    @Test
    public void testCreateUser(){
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            //TODO finish implementing
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
