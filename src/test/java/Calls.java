import org.junit.jupiter.api.Assumptions;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

class Calls {
    private final String BASE_URL;
    private HttpURLConnection connection;

    protected Calls(){
        BASE_URL = System.getenv("TestUrl");
    }

    protected String createUser(String username, String password){
        String endpoint = String.format("/create-user/%s/%s", username, password);
        setUpConnectionAndURL(endpoint, "POST");

        return makeConnection();
    }

    protected String removeUser(String username){
        String endpoint = String.format("/remove/%s", username);
        setUpConnectionAndURL(endpoint, "DELETE");

        return makeConnection();
    }

    protected String getUser(String username){
        String endpoint = String.format("/user/%s", username);
        setUpConnectionAndURL(endpoint, "GET");

        return makeConnection();
    }

    protected String authenticateUser(String username, String password){
        String endpoint = String.format("/authenticate/%s/%s", username, password);
        setUpConnectionAndURL(endpoint, "GET");

        return makeConnection();
    }

    protected String updateUserPassword(String username, String password){
        String endpoint = String.format("/authenticate/%s/%s", username, password);
        setUpConnectionAndURL(endpoint, "PATCH");

        return makeConnection();
    }

    protected void setUpConnectionAndURL(String endpoint, String requestMethod){
        URL url = createUrl(BASE_URL+endpoint);

        Assumptions.assumeFalse(url == null);
        Assumptions.assumeFalse(url.getPath().isBlank());

        connection = createConnection(url, requestMethod);
        Assumptions.assumeFalse(connection == null);
    }

    protected String makeConnection(){
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

    protected HttpURLConnection createConnection(URL url, String requestMethod){
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

    protected URL createUrl(String endpoint){
        URL url = null;
        try {
            url = URI.create(BASE_URL+endpoint).toURL();
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }
}
