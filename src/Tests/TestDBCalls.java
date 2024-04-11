package Tests;

import Controller.DbCaller;
import Model.DbCalls;
import Model.Request;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


public class TestDBCalls {
    private static DbCaller dbCaller;
    private static final String username = "new_user";
    private static final String password = "password";

    @BeforeAll
    public static void setUp(){
        dbCaller = new DbCaller(System.getenv("mockDbUrl"));
    }

    @AfterEach
    public void cleanUp(){
        try (Connection con = DriverManager.getConnection(System.getenv("mockDbUrl"));
             Statement statement = con.createStatement()
             ) {
            String drop = "DROP FROM Users";
            statement.executeUpdate(drop);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAllCalls(){
        List<Boolean> resultList = List.of(
                dbCaller.execute(new Request(username, password, DbCalls.createUser)),
                dbCaller.execute(new Request(username, password, DbCalls.authenticateUser)),
                dbCaller.execute(new Request(username, "new_" + password, DbCalls.updatePassword)),
                dbCaller.execute(new Request(username, DbCalls.getUser)),
                dbCaller.execute(new Request(username, DbCalls.removeUser))
        );

        for (Boolean bool : resultList) {
            Assertions.assertTrue(bool);
        }
    }

    @Test
    public void testGetCall(){
        boolean bool = dbCaller.execute(new Request(username, password, DbCalls.createUser));
        Assertions.assertTrue(bool);

        bool = dbCaller.execute(new Request(username, DbCalls.getUser));
        Assertions.assertTrue(bool);

        bool = dbCaller.execute(new Request("non-existent user", DbCalls.getUser));
        Assertions.assertFalse(bool);
    }

    @Test
    public void testRemoveCall(){
        boolean bool = dbCaller.execute(new Request(username, password, DbCalls.createUser));
        Assertions.assertTrue(bool);

        bool = dbCaller.execute(new Request(username, DbCalls.removeUser));
        Assertions.assertTrue(bool);
    }

    @Test
    public void testCreateCall(){
        boolean bool = dbCaller.execute(new Request(username, password, DbCalls.createUser));
        Assertions.assertTrue(bool);

        bool = dbCaller.execute(new Request(username, password, DbCalls.createUser));
        Assertions.assertFalse(bool);

        bool = dbCaller.execute(new Request(null, password, DbCalls.createUser));
        Assertions.assertFalse(bool);

        bool = dbCaller.execute(new Request("", password, DbCalls.createUser));
        Assertions.assertFalse(bool);

        bool = dbCaller.execute(new Request("user", null, DbCalls.createUser));
        Assertions.assertFalse(bool);

        bool = dbCaller.execute(new Request("user", "", DbCalls.createUser));
        Assertions.assertFalse(bool);
    }

    @Test
    public void testUpdateCall(){
        boolean bool = dbCaller.execute(new Request(username, password, DbCalls.createUser));
        Assertions.assertTrue(bool);

        bool = dbCaller.execute(new Request(username, "new_"+password, DbCalls.authenticateUser));
        Assertions.assertTrue(bool);
    }

    @Test
    public void testAuthenticateCall(){
        boolean bool = dbCaller.execute(new Request(username, password, DbCalls.createUser));
        Assertions.assertTrue(bool);

        bool = dbCaller.execute(new Request(username, password, DbCalls.authenticateUser));
        Assertions.assertTrue(bool);
    }

    @Test
    public void testCreateExistingUser(){
        boolean bool = dbCaller.execute(new Request(username, password, DbCalls.createUser));
        Assertions.assertTrue(bool);

        bool = dbCaller.execute(new Request(username, password, DbCalls.createUser));
        Assertions.assertFalse(bool);
    }
}
