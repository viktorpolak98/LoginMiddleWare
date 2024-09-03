import Controller.CallerController;
import Util.User;
import Util.XMLTestdataReader;
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

    @BeforeAll
    protected static void beforeTests() {
        new CallerController(
                System.getenv("MockDbUrl"),
                System.getenv("MockDbUser"),
                System.getenv("MockDbUserPassword"));
        users = XMLTestdataReader.readUsers("src\\test\\java\\Testdata\\Testdata.xml");
        cleanUpDatabase();
    }

    @AfterEach
    protected void cleanUp() {
        cleanUpDatabase();
    }

    protected static List<User> getUsers() {
        return users;
    }

    protected String getHTTP_200() {
        return "HTTP/1.0 200 OK";
    }

    protected String getHTTP_400() {
        return "HTTP/1.0 400 Bad request";
    }

    protected String getHTTP_404() {
        return "HTTP/1.0 404 Not found";
    }

    protected Calls getCalls() {
        return calls;
    }

    protected static void cleanUpDatabase() {
        try (Connection con = DriverManager.getConnection(System.getenv("MockDbUrl"),
                System.getenv("MockDbUser"), System.getenv("MockDbUserPassword"));
             Statement statement = con.createStatement()
        ) {
            String drop = "DELETE FROM [dbo].[users]";
            statement.executeUpdate(drop);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
