package Tests;

import Controller.CallerController;
import Controller.DbCaller;
import Model.DbCalls;
import Model.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


public class TestDBCalls {
    private static DbCaller dbCaller;
    private static String userName = "new_user";
    private static String password = "password";

    @BeforeAll
    public static void setUp(){
        dbCaller = new DbCaller(System.getenv("mockDbUrl"));
    }

    @Test
    public void testAllCalls(){
        List<Boolean> resultList = List.of(
                dbCaller.execute(new Request(userName, password, DbCalls.createUser)),
                dbCaller.execute(new Request(userName, password, DbCalls.authenticateUser)),
                dbCaller.execute(new Request(userName, "new_" + password, DbCalls.updatePassword)),
                dbCaller.execute(new Request(userName, DbCalls.getUser)),
                dbCaller.execute(new Request(userName, DbCalls.removeUser))
        );

        for (Boolean bool : resultList) {
            Assertions.assertTrue(bool);
        }
    }

    @Test
    public void testMultipleThreads(){
        //TODO: implement
    }

    @Test
    public void testGetCall(){
        boolean bool = dbCaller.execute(new Request(userName, password, DbCalls.createUser));
        Assertions.assertTrue(bool);

        bool = dbCaller.execute(new Request(userName, DbCalls.getUser));
        Assertions.assertTrue(bool);

        bool = dbCaller.execute(new Request("non-existent user", DbCalls.getUser));
        Assertions.assertFalse(bool);
    }

    @Test
    public void testRemoveCall(){
        //TODO: implement
    }

    @Test
    public void testCreateCall(){
        boolean bool = dbCaller.execute(new Request(userName, password, DbCalls.createUser));
        Assertions.assertTrue(bool);

        bool = dbCaller.execute(new Request(userName, password, DbCalls.createUser));
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
        //TODO: implement
    }

    @Test
    public void testAuthenticateCall(){
        //TODO: implement
    }

}
