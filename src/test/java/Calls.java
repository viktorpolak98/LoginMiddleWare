import org.junit.jupiter.api.Assumptions;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

class Calls {
    private final String BASE_URL;

    protected Calls() {
        BASE_URL = System.getenv("TestUrl");
    }

    protected String createUser(String username, String password) {
        String endpoint = "/v1/create-user";
        String body = formatBody(username, password);
        HttpURLConnection connection = setUpConnectionAndURL(endpoint, "POST");
        writeToConnection(connection, body);

        return makeConnection(connection);
    }

    protected String removeUser(String username) {
        String endpoint = "/v1/remove-user";
        String body = formatBody(username);
        HttpURLConnection connection = setUpConnectionAndURL(endpoint, "DELETE");
        writeToConnection(connection, body);

        return makeConnection(connection);
    }

    protected String getUser(String username) {
        String endpoint = String.format("/v1/get-user/%s", username);
        HttpURLConnection connection = setUpConnectionAndURL(endpoint, "GET");

        return makeConnection(connection);
    }

    protected String authenticateUser(String username, String password) {
        String endpoint = String.format("/v1/authenticate/%s", username);
        HttpURLConnection connection = setUpConnectionAndURL(endpoint, "GET");
        //Basic authentication
        String encoded = Base64.getEncoder().encodeToString((username+":"+password).getBytes(StandardCharsets.UTF_8));
        connection.setRequestProperty("Authorization", "Basic "+encoded);

        return makeConnection(connection);
    }

    protected String updateUserPassword(String username, String password) {
        String endpoint = "/v1/update-password";
        String body = formatBody(username, password);
        HttpURLConnection connection = setUpConnectionAndURL(endpoint, "PUT");
        writeToConnection(connection, body);

        return makeConnection(connection);
    }

    protected HttpURLConnection setUpConnectionAndURL(String endpoint, String requestMethod) {
        URL url = createUrl(endpoint);

        Assumptions.assumeFalse(url == null);
        Assumptions.assumeFalse(url.getPath().isBlank());

        HttpURLConnection connection = createConnection(url, requestMethod);
        Assumptions.assumeFalse(connection == null);

        return connection;
    }

    protected String makeConnection(HttpURLConnection connection) {
        String response = "";
        try {
            connection.connect();
            response =  connection.getResponseCode() + " " + connection.getResponseMessage();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    protected void writeToConnection(HttpURLConnection connection, String body){
        connection.setDoOutput(true);
        try {
            OutputStream os = connection.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            writer.write(body);
            writer.flush();
            writer.close();
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected HttpURLConnection createConnection(URL url, String requestMethod) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Content-Type", "text/plain");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    protected URL createUrl(String endpoint) {
        URL url = null;
        String urlString = BASE_URL + endpoint;
        try {
            url = URI.create(urlString).toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private String formatBody(String username) {
        return String.format("{ \"username\":\"%s\" }", username);
    }

    private String formatBody(String username, String password) {
        return String.format("{ \"username\":\"%s\",\"password\":\"%s\" }", username, password);
    }
}
