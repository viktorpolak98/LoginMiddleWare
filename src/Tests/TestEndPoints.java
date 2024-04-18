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
        new CallerController(System.getenv("AllowedHostsConfig"),
                System.getenv("MockDbUrl"),
                System.getenv("MockDbUser"),
                System.getenv("MockDbUserPassword"));
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
            String response = setUpCreate(user);

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
        String response = setUpCreate(user);

        Assertions.assertEquals("HTTP/1.0 200 OK", response, "Actual response: " + response);
    }

    @Test
    public void testGetSingleExistingUser() {
        User user = users.get(0);
        //Create user to be fetched
        String response = setUpCreate(user);
        Assumptions.assumeTrue(response.equals("HTTP/1.0 200 OK"), "Actual response: " + response);

        //Get user
        response = setUpGet(user);
        Assertions.assertEquals("HTTP/1.0 200 OK", response, "Actual response: " + response);
    }

    @Test
    public void testRemoveSingleExistingUser(){
        User user = users.get(0);
        //Create user to be deleted
        String response = setUpCreate(user);
        Assumptions.assumeTrue(response.equals("HTTP/1.0 200 OK"), "Actual response: " + response);

        //Remove created user
        response = setUpRemove(user);
        Assertions.assertEquals("HTTP/1.0 200 OK", response, "Actual response: " + response);
    }

    @Test
    public void testRemoveMultipleExistingUsers(){
        List<String> results = new ArrayList<>();

        for (User user : users) {
            //Create every user from testdata
            String response = setUpCreate(user);
            results.add(response);
        }

        for (int i = 0; i < results.size(); i++){
            Assumptions.assumeTrue(results.get(i).equals("HTTP/1.0 200 OK"),
                    "Assume failed on " + i + ". With the response: " + results.get(i));
        }

        for (User user : users) {
            //Remove every user previously created
            String response = setUpRemove(user);
            results.add(response);
        }

        for (int i = 0; i < results.size(); i++) {
            Assertions.assertEquals("HTTP/1.0 200 OK", results.get(i),
                    "Assert failed on " + i + ". With the response: " + results.get(i));
        }
    }

    private String setUpCreate(User user){
        String endpoint = String.format("/create-user/%s/%s", user.getUsername(), user.getPassword());
        setUpConnectionAndURL(endpoint, "POST");

        return makeConnection();
    }

    private String setUpRemove(User user){
        String endpoint = String.format("/remove/%s", user.getUsername());
        setUpConnectionAndURL(endpoint, "DELETE");

        return makeConnection();
    }

    private String setUpGet(User user){
        String endpoint = String.format("/user/%s", user.getUsername());
        setUpConnectionAndURL(endpoint, "GET");

        return makeConnection();
    }

    private String setUpAuthenticate(User user){
        String endpoint = String.format("/authenticate/%s/%s", user.getUsername(), user.getPassword());
        setUpConnectionAndURL(endpoint, "GET");

        return makeConnection();
    }

    private String setUpUpdatePassword(User user){
        String endpoint = String.format("/authenticate/%s/%s", user.getUsername(), user.getPassword());
        setUpConnectionAndURL(endpoint, "PATCH");

        return makeConnection();
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
            String drop = "DROP FROM users";
            statement.executeUpdate(drop);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
