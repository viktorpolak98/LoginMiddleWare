import Controller.CallerController;
import Util.User;
import Util.XMLTestdataReader;
import io.javalin.Javalin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public abstract class EndpointParent {
    private static List<User> users;
    private final Calls calls = new Calls();
    private static Javalin app;

    @BeforeAll
    protected static void beforeTests() {
        app = Javalin.create();
        new CallerController(app,
                System.getenv("MockDbUrl"),
                System.getenv("MockDbUser"),
                System.getenv("MockDbUserPassword"));
        users = XMLTestdataReader.readUsers("src\\test\\java\\Testdata\\Testdata.xml");
        cleanUpDatabase();
    }

    @AfterAll
    protected static void stop() {
        app.stop();
    }

    @AfterEach
    protected void cleanUp() {
        cleanUpDatabase();
    }

    protected static List<User> getUsers() {
        return users;
    }

    protected String getHTTP_200() {
        return "200 OK";
    }

    protected String getHTTP_400() {
        return "400 Bad Request";
    }

    protected String getHTTP_404() {
        return "404 Not Found";
    }

    protected String getHTTP_201(){
        return "201 Created";
    }

    protected String getHTTP_409(){
        return "409 Conflict";
    }

    protected String getHTTP_401(){
        return "401 Unauthorized";
    }

    protected Calls getCalls() {
        return calls;
    }

    protected static void cleanUpDatabase() {
        try (Connection con = DriverManager.getConnection(System.getenv("MockDbUrl"),
                System.getenv("MockDbUser"), System.getenv("MockDbUserPassword"));
             Statement statement = con.createStatement()
        ) {
            String drop = "DELETE FROM [dbo].[users];" +
                    "DELETE FROM [dbo].[APIKeys]; " +
                    "DELETE FROM [dbo].[APIUsers]";
            statement.executeUpdate(drop);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
