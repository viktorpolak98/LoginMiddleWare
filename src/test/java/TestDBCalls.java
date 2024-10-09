import Controller.Database.DbRequestCaller;
import Model.DbCalls;
import Model.Request;
import Model.Status;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


public class TestDBCalls {
    private static DbRequestCaller dbRequestCaller;
    private static final String username = "new_user";
    private static final String password = "password";

    @BeforeAll
    public static void setUp() {
        dbRequestCaller = new DbRequestCaller(System.getenv("MockDbUrl"),
                System.getenv("MockDbUser"),
                System.getenv("MockDbUserPassword"));
        cleanUpDatabase();
    }

    @AfterEach
    public void cleanUp() {
        cleanUpDatabase();
    }

    @Test
    public void testAllCalls() {
        List<Status> resultList = List.of(
                dbRequestCaller.execute(new Request(DbCalls.CreateUser, username, password)),
                dbRequestCaller.execute(new Request(DbCalls.AuthenticateUser, username, password)),
                dbRequestCaller.execute(new Request(DbCalls.UpdatePassword, username, "new_" + password)),
                dbRequestCaller.execute(new Request(DbCalls.GetUser, username)),
                dbRequestCaller.execute(new Request(DbCalls.RemoveUser, username))
        );

        for (Status status : resultList) {
            Assertions.assertEquals(Status.OK, status);
        }
    }

    @Test
    public void testGetCall() {
        Status status = dbRequestCaller.execute(new Request(DbCalls.CreateUser, username, password));
        Assumptions.assumeTrue(Status.OK == status);

        status = dbRequestCaller.execute(new Request(DbCalls.GetUser, username));
        Assertions.assertEquals(Status.OK, status);

        status = dbRequestCaller.execute(new Request(DbCalls.GetUser, "non-existent user"));
        Assertions.assertEquals(Status.NOT_FOUND, status);

        status = dbRequestCaller.execute(new Request(DbCalls.GetUser, ""));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(DbCalls.GetUser, " "));
        Assertions.assertEquals(Status.BAD_REQUEST, status);
    }

    @Test
    public void testRemoveCall() {
        Status status = dbRequestCaller.execute(new Request(DbCalls.CreateUser, username, password));
        Assumptions.assumeTrue(status == Status.OK);

        status = dbRequestCaller.execute(new Request(DbCalls.RemoveUser, username));
        Assertions.assertEquals(Status.OK, status);

        status = dbRequestCaller.execute(new Request(DbCalls.RemoveUser, ""));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(DbCalls.RemoveUser, " "));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(DbCalls.RemoveUser, "non-existent user"));
        Assertions.assertEquals(Status.BAD_REQUEST, status);
    }

    @Test
    public void testCreateCall() {
        Status status = dbRequestCaller.execute(new Request(DbCalls.CreateUser, username, password));
        Assertions.assertEquals(Status.OK, status);

        status = dbRequestCaller.execute(new Request(DbCalls.CreateUser, username, password)); //User exists
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(DbCalls.CreateUser, null, password));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(DbCalls.CreateUser, "", password));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(DbCalls.CreateUser, " ", password));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(DbCalls.CreateUser, "user", null));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(DbCalls.CreateUser, "user", ""));
        Assertions.assertEquals(Status.BAD_REQUEST, status);
    }

    @Test
    public void testUpdateCall() {
        Status status = dbRequestCaller.execute(new Request(DbCalls.CreateUser, username, password));
        Assumptions.assumeTrue(Status.OK == status);

        status = dbRequestCaller.execute(new Request(DbCalls.UpdatePassword, username, "new_" + password));
        Assertions.assertEquals(Status.OK, status);

        status = dbRequestCaller.execute(new Request(DbCalls.UpdatePassword, username, ""));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(DbCalls.UpdatePassword, username, " "));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(DbCalls.UpdatePassword, "", password));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(DbCalls.UpdatePassword, " ", password));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(DbCalls.UpdatePassword, "", ""));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(DbCalls.UpdatePassword, " ", " "));
        Assertions.assertEquals(Status.BAD_REQUEST, status);
    }

    @Test
    public void testAuthenticateCall() {
        Status status = dbRequestCaller.execute(new Request(DbCalls.CreateUser, username, password));
        Assumptions.assumeTrue(Status.OK == status);

        status = dbRequestCaller.execute(new Request(DbCalls.AuthenticateUser, username, password));
        Assertions.assertEquals(Status.OK, status);

        status = dbRequestCaller.execute(new Request(DbCalls.AuthenticateUser, username, "wrong password"));
        Assertions.assertEquals(Status.UNAUTHORIZED, status);

        status = dbRequestCaller.execute(new Request(DbCalls.AuthenticateUser, "wrong user", password));
        Assertions.assertEquals(Status.UNAUTHORIZED, status);

        status = dbRequestCaller.execute(new Request(DbCalls.AuthenticateUser, username, ""));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(DbCalls.AuthenticateUser, username, " "));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(DbCalls.AuthenticateUser, "", password));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(DbCalls.AuthenticateUser, " ", password));
        Assertions.assertEquals(Status.BAD_REQUEST, status);
    }

    @Test
    public void testCreateExistingUser() {
        Status status = dbRequestCaller.execute(new Request(DbCalls.CreateUser, username, password));
        Assertions.assertEquals(Status.OK, status);

        status = dbRequestCaller.execute(new Request(DbCalls.CreateUser, username, password));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(DbCalls.CreateUser, username, "new_" + password));
        Assertions.assertEquals(Status.BAD_REQUEST, status);
    }

    private static void cleanUpDatabase() {
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
