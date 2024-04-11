package Tests;

import Controller.CallerController;
import Tests.Util.User;
import Tests.Util.XMLTestdataReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TestEndPoints {
    private static String BASE_URL;
    private static List<User> users;

    @BeforeAll
    public static void setUp(){
        new CallerController(System.getenv("mockDbUrl"), System.getenv("AllowedHostsConfig"));
        BASE_URL = System.getenv("TestUrl");
        users = XMLTestdataReader.readUsers("..\\Testdata\\Testdata.xml");
    }

    @Test
    public void testCreateMultipleUsers(){
        List<String> results = new ArrayList<>();
        HttpURLConnection connection;


        for (User user : users) {
            String endpoint = String.format("/create-user/%s/%s", user.getUsername(), user.getPassword());
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
            } catch (IOException e) {
                e.printStackTrace();
            }

            results.add(response);
        }

        for (int i = 0; i < results.size(); i++) {
            Assertions.assertEquals("HTTP/1.0 200 OK", results.get(i),
                    "Assert failed on " + i + ". With the response: " + results.get(i));
        }
    }

    @Test
    public void testCreateSingleUser(){
        HttpURLConnection connection;

        User user = users.get(0);
        String endpoint = String.format("/create-user/%s/%s", user.getUsername(), user.getPassword());
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

        Assertions.assertEquals("HTTP/1.0 200 OK", response, "Actual response: " + response);
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
