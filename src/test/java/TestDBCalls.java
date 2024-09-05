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
                dbRequestCaller.execute(new Request(username, password, DbCalls.createUser)),
                dbRequestCaller.execute(new Request(username, password, DbCalls.authenticateUser)),
                dbRequestCaller.execute(new Request(username, "new_" + password, DbCalls.updatePassword)),
                dbRequestCaller.execute(new Request(username, DbCalls.getUser)),
                dbRequestCaller.execute(new Request(username, DbCalls.removeUser))
        );

        for (Status status : resultList) {
            Assertions.assertEquals(Status.OK, status);
        }
    }

    @Test
    public void testGetCall() {
        Status status = dbRequestCaller.execute(new Request(username, password, DbCalls.createUser));
        Assumptions.assumeTrue(Status.OK == status);

        status = dbRequestCaller.execute(new Request(username, DbCalls.getUser));
        Assertions.assertEquals(Status.OK, status);

        status = dbRequestCaller.execute(new Request("non-existent user", DbCalls.getUser));
        Assertions.assertEquals(Status.NOT_FOUND, status);

        status = dbRequestCaller.execute(new Request("", DbCalls.getUser));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(" ", DbCalls.getUser));
        Assertions.assertEquals(Status.BAD_REQUEST, status);
    }

    @Test
    public void testRemoveCall() {
        Status status = dbRequestCaller.execute(new Request(username, password, DbCalls.createUser));
        Assumptions.assumeTrue(status == Status.OK);

        status = dbRequestCaller.execute(new Request(username, DbCalls.removeUser));
        Assertions.assertEquals(Status.OK, status);

        status = dbRequestCaller.execute(new Request("", DbCalls.removeUser));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(" ", DbCalls.removeUser));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request("non-existent user", DbCalls.removeUser));
        Assertions.assertEquals(Status.BAD_REQUEST, status);
    }

    @Test
    public void testCreateCall() {
        Status status = dbRequestCaller.execute(new Request(username, password, DbCalls.createUser));
        Assertions.assertEquals(Status.OK, status);

        status = dbRequestCaller.execute(new Request(username, password, DbCalls.createUser)); //User exists
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(null, password, DbCalls.createUser));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request("", password, DbCalls.createUser));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(" ", password, DbCalls.createUser));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request("user", null, DbCalls.createUser));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request("user", "", DbCalls.createUser));
        Assertions.assertEquals(Status.BAD_REQUEST, status);
    }

    @Test
    public void testUpdateCall() {
        Status status = dbRequestCaller.execute(new Request(username, password, DbCalls.createUser));
        Assumptions.assumeTrue(Status.OK == status);

        status = dbRequestCaller.execute(new Request(username, "new_" + password, DbCalls.updatePassword));
        Assertions.assertEquals(Status.OK, status);

        status = dbRequestCaller.execute(new Request(username, "", DbCalls.updatePassword));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(username, " ", DbCalls.updatePassword));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request("", password, DbCalls.updatePassword));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(" ", password, DbCalls.updatePassword));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request("", "", DbCalls.updatePassword));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(" ", " ", DbCalls.updatePassword));
        Assertions.assertEquals(Status.BAD_REQUEST, status);
    }

    @Test
    public void testAuthenticateCall() {
        Status status = dbRequestCaller.execute(new Request(username, password, DbCalls.createUser));
        Assumptions.assumeTrue(Status.OK == status);

        status = dbRequestCaller.execute(new Request(username, password, DbCalls.authenticateUser));
        Assertions.assertEquals(Status.OK, status);

        status = dbRequestCaller.execute(new Request(username, "wrong password", DbCalls.authenticateUser));
        Assertions.assertEquals(Status.UNAUTHORIZED, status);

        status = dbRequestCaller.execute(new Request("wrong user", password, DbCalls.authenticateUser));
        Assertions.assertEquals(Status.UNAUTHORIZED, status);

        status = dbRequestCaller.execute(new Request(username, "", DbCalls.authenticateUser));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(username, " ", DbCalls.authenticateUser));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request("", password, DbCalls.authenticateUser));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(" ", password, DbCalls.authenticateUser));
        Assertions.assertEquals(Status.BAD_REQUEST, status);
    }

    @Test
    public void testCreateExistingUser() {
        Status status = dbRequestCaller.execute(new Request(username, password, DbCalls.createUser));
        Assertions.assertEquals(Status.OK, status);

        status = dbRequestCaller.execute(new Request(username, password, DbCalls.createUser));
        Assertions.assertEquals(Status.BAD_REQUEST, status);

        status = dbRequestCaller.execute(new Request(username, "new_" + password, DbCalls.createUser));
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
