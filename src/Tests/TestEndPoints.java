package Tests;

import Controller.CallerController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.DataOutputStream;
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

        //replace with Testdata.Testdata.xml later.
        String username = "user1";
        String password = "password";
        String endpoint = String.format("/create-user/%s/%s", username, password);
        URL url = createUrl(endpoint);

        Assumptions.assumeFalse(url == null);
        Assumptions.assumeFalse(url.getPath().isBlank());

        connection = createConnection(url, "POST");
        Assumptions.assumeFalse(connection == null);

        String response = "";

        try {
            connection.connect();
            response = connection.getResponseMessage();
            connection.disconnect();
        } catch (IOException e){
            e.printStackTrace();
        }

        Assertions.assertFalse(response.isBlank(), "response was blank");
        Assertions.assertEquals("HTTP/1.0 200 OK", response); //TODO: Replace with actual expected response
    }

    private HttpURLConnection createConnection(URL url, String requestMethod){
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Content-Type", "text/plain");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
        } catch (IOException e){
            e.printStackTrace();
        }
        return connection;
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
