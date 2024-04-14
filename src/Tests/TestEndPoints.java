package Tests;

import Controller.CallerController;
import Tests.Util.User;
import Tests.Util.XMLTestdataReader;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TestEndPoints {
    private static String BASE_URL;
    private static List<User> users;
    private HttpURLConnection connection;

    @BeforeAll
    public static void setUp(){
        new CallerController(System.getenv("mockDbUrl"), System.getenv("AllowedHostsConfig"));
        BASE_URL = System.getenv("TestUrl");
        users = XMLTestdataReader.readUsers("..\\Testdata\\Testdata.xml");
        cleanUpDatabase();
    }

    @AfterEach
    public void cleanUp(){
        cleanUpDatabase();
    }

    @Test
    public void testCreateMultipleUsers(){
        List<String> results = new ArrayList<>();

        for (User user : users) {
            //Create every user from testdata
            String endpoint = String.format("/create-user/%s/%s", user.getUsername(), user.getPassword());
            setUpConnectionAndURL(endpoint, "POST");

            String response = makeConnection();

            results.add(response);
        }

        for (int i = 0; i < results.size(); i++) {
            Assertions.assertEquals("HTTP/1.0 200 OK", results.get(i),
                    "Assert failed on " + i + ". With the response: " + results.get(i));
        }
    }

    @Test
    public void testCreateSingleUser(){
        User user = users.get(0);
        String endpoint = String.format("/create-user/%s/%s", user.getUsername(), user.getPassword());
        setUpConnectionAndURL(endpoint, "POST");

        String response = makeConnection();

        Assertions.assertEquals("HTTP/1.0 200 OK", response, "Actual response: " + response);
    }

    @Test
    public void testRemoveSingleExistingUser(){
        User user = users.get(0);
        //Create user to be deleted
        String endpoint = String.format("/create-user/%s/%s", user.getUsername(), user.getPassword());
        setUpConnectionAndURL(endpoint, "POST");

        String response = makeConnection();
        Assertions.assertEquals("HTTP/1.0 200 OK", response, "Actual response: " + response);

        //Remove created user
        endpoint = String.format("/remove/%s", user.getUsername());
        setUpConnectionAndURL(endpoint, "DELETE");

        response = makeConnection();
        Assertions.assertEquals("HTTP/1.0 200 OK", response, "Actual response: " + response);
    }

    private void setUpConnectionAndURL(String endpoint, String requestMethod){
        URL url = createUrl(BASE_URL+endpoint);

        Assumptions.assumeFalse(url == null);
        Assumptions.assumeFalse(url.getPath().isBlank());

        connection = createConnection(url, requestMethod);
        Assumptions.assumeFalse(connection == null);
    }

    private String makeConnection(){
        String response = "";
        try {
            connection.connect();
            response = connection.getResponseMessage();
            connection.disconnect();
        } catch (IOException e){
            e.printStackTrace();
        }

        return response;
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

    private static void cleanUpDatabase(){
        try (Connection con = DriverManager.getConnection(System.getenv("mockDbUrl"));
             Statement statement = con.createStatement()
        ) {
            String drop = "DROP FROM Users";
            statement.executeUpdate(drop);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
