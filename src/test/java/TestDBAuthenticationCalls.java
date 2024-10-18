import Controller.Database.DbAPIRequestCaller;
import Model.DbAPIKeyCalls;
import Model.DbAPIKeyRequest;
import Model.Status;
import org.junit.jupiter.api.*;

import java.sql.*;

public class TestDBAuthenticationCalls {

    private static DbAPIRequestCaller dbAPIRequestCaller;
    private final String emailAddress = "example@example.com";
    private final String emailAddress2 = "example1@example1.com";
    private final String APIKey = "KeyExample1";
    private final String APIKey2 = "KeyExample2";

    @BeforeAll
    public static void setUp() {
        dbAPIRequestCaller = new DbAPIRequestCaller(System.getenv("MockDbUrl"),
                System.getenv("MockDbUser"),
                System.getenv("MockDbUserPassword"));
        cleanUpDatabase();
    }

    @AfterEach
    public void cleanup() {
        cleanUpDatabase();
    }

    @Test
    public void testAuthenticateAPIKey() {
        Assumptions.assumeTrue(setupUsersAndKeys());

        String invalidKey = "apikey";
        Assumptions.assumeTrue(Status.CREATED == dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateKey,
                emailAddress, invalidKey, new Date(System.currentTimeMillis() - 500_000_000)))); //Invalid roughly 6 days ago

        Assertions.assertEquals(Status.OK, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.AuthenticateKey, emailAddress, APIKey)));

        Assertions.assertEquals(Status.BAD_REQUEST, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.AuthenticateKey, emailAddress, " ")));

        Assertions.assertEquals(Status.BAD_REQUEST, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.AuthenticateKey, emailAddress, "")));

        Assertions.assertEquals(Status.BAD_REQUEST, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.AuthenticateKey, " ", APIKey)));

        Assertions.assertEquals(Status.BAD_REQUEST, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.AuthenticateKey, "", APIKey)));

        Assertions.assertEquals(Status.UNAUTHORIZED, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.AuthenticateKey, emailAddress, "notAKey")));

        Assertions.assertEquals(Status.UNAUTHORIZED, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.AuthenticateKey, "notAUser", APIKey)));

        Assertions.assertEquals(Status.UNAUTHORIZED, dbAPIRequestCaller.execute(
                new DbAPIKeyRequest(DbAPIKeyCalls.AuthenticateKey, emailAddress, APIKey2))); //User does not own key

        Assertions.assertEquals(Status.UNAUTHORIZED, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.AuthenticateKey, emailAddress, invalidKey)));
    }

    @Test
    public void testCreateAPIUser() {
        Assertions.assertEquals(Status.CREATED, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateUser, emailAddress)));

        Assertions.assertEquals(Status.CREATED, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateUser, emailAddress2)));

        Assertions.assertEquals(Status.BAD_REQUEST, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateUser, "")));

        Assertions.assertEquals(Status.BAD_REQUEST, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateUser, " ")));

        Assertions.assertEquals(Status.CONFLICT, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateUser, emailAddress)));
    }

    @Test
    public void testCreateAPIKey() {
        Assumptions.assumeTrue(Status.CREATED == dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateUser, emailAddress)));

        Assertions.assertEquals(Status.CREATED, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateKey, emailAddress, APIKey)));

        Assertions.assertEquals(Status.CREATED, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateKey,
                emailAddress, APIKey2, new Date(System.currentTimeMillis()))));

        Assertions.assertEquals(Status.CONFLICT, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateKey, emailAddress, APIKey)));

        Assertions.assertEquals(Status.CONFLICT, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateKey, emailAddress2, APIKey)));

        Assertions.assertEquals(Status.BAD_REQUEST, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateKey, emailAddress, "")));

        Assertions.assertEquals(Status.BAD_REQUEST, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateKey, "", APIKey)));

        Assertions.assertEquals(Status.BAD_REQUEST, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateKey, " ", APIKey)));

        Assertions.assertEquals(Status.BAD_REQUEST, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateKey, emailAddress, " ")));

        Assertions.assertEquals(Status.BAD_REQUEST, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateKey, null, null)));

        Assertions.assertEquals(Status.BAD_REQUEST, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateKey, emailAddress, null)));

        Assertions.assertEquals(Status.BAD_REQUEST, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateKey, null, APIKey)));
    }

    @Test
    public void testInvalidateAPIKey() {
        Assumptions.assumeTrue(setupUsersAndKeys());

        Assertions.assertEquals(Status.OK, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.InvalidateKey, emailAddress, APIKey)));

        Assertions.assertEquals(Status.OK, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.InvalidateKey,
                emailAddress, APIKey))); //Should be possible to invalidate already invalid key

        Assertions.assertEquals(Status.NOT_FOUND, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.InvalidateKey, emailAddress, APIKey2)));

        Assertions.assertEquals(Status.BAD_REQUEST, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.InvalidateKey, "", "")));

        Assertions.assertEquals(Status.BAD_REQUEST, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.InvalidateKey, " ", " ")));

        Assertions.assertEquals(Status.BAD_REQUEST, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.InvalidateKey, null, null)));

        Assertions.assertEquals(Status.BAD_REQUEST, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.InvalidateKey, emailAddress, null)));

        Assertions.assertEquals(Status.BAD_REQUEST, dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.InvalidateKey, null, APIKey)));

    }

    private boolean setupUsersAndKeys() {
        Status user = dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateUser, emailAddress));
        Status key = dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateKey, emailAddress, APIKey));

        boolean user1Status = key == Status.CREATED && user == Status.CREATED;

        user = dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateUser, emailAddress2));
        key = dbAPIRequestCaller.execute(new DbAPIKeyRequest(DbAPIKeyCalls.CreateKey, emailAddress2, APIKey2));

        boolean user2Status = key == Status.CREATED && user == Status.CREATED;

        return user1Status && user2Status;
    }

    private static void cleanUpDatabase() {
        try (Connection con = DriverManager.getConnection(System.getenv("MockDbUrl"),
                System.getenv("MockDbUser"), System.getenv("MockDbUserPassword"));
             Statement statement = con.createStatement()
        ) {
            String drop = "DELETE FROM [dbo].[APIKeys]; " +
                    "DELETE FROM [dbo].[APIUsers]";

            statement.executeUpdate(drop);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
