import Controller.Database.DbAPIRequestCaller;
import org.junit.jupiter.api.BeforeAll;

import java.sql.*;

public class TestDBAuthenticationCalls {

    private static DbAPIRequestCaller dbAPIRequestCaller;
    private static final String emailAddress = "example@example.com";
    private static final String APIKey = "KeyExample123";

    @BeforeAll
    public static void setUp() {
        dbAPIRequestCaller = new DbAPIRequestCaller(System.getenv("MockDbUrl"),
                System.getenv("MockDbUser"),
                System.getenv("MockDbUserPassword"));
        cleanUpDatabase();
    }

    private static void cleanUpDatabase() {
        try (Connection con = DriverManager.getConnection(System.getenv("MockDbUrl"),
                System.getenv("MockDbUser"), System.getenv("MockDbUserPassword"));
             Statement statement = con.createStatement()
        ) {
            String drop = "DROP TABLE IF EXISTS [dbo].[APIKeys], [dbo].[APIUsers]";

            statement.executeUpdate(drop);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
