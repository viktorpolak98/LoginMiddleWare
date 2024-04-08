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
    private static String BASE_URL;

    @BeforeAll
    public static void setUp(){
        new CallerController(System.getenv("mockDbUrl"), System.getenv("AllowedHostsConfig"));
        BASE_URL = System.getenv("TestUrl");
    }

    @Test
    public void testCreateUser(){
        HttpURLConnection connection;
        URL url = createUrl("/create-user");

        Assumptions.assumeFalse(url == null);
        Assumptions.assumeFalse(url.getPath().isBlank());

        try {

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");


            //TODO finish implementing
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private URL createUrl(String endpoint){
        URL url = null;
        try {
            url = URI.create(BASE_URL+endpoint).toURL();
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }
}
