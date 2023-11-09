package Tests;

import Controller.CallerController;
import Controller.DbCaller;
import Model.DbCalls;
import Model.Request;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


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
        //TODO: implement
        dbCaller.execute(new Request(userName, password, DbCalls.createUser));
        dbCaller.execute(new Request(userName, password, DbCalls.authenticateUser));
        dbCaller.execute(new Request(userName, "new_" + password, DbCalls.updatePassword));
        dbCaller.execute(new Request(userName, DbCalls.getUser));
        dbCaller.execute(new Request(userName, DbCalls.removeUser));
    }

    @Test
    public void testMultipleThreads(){
        //TODO: implement
    }

    @Test
    public void testGetCall(){
        //TODO: implement
    }

    @Test
    public void testRemoveCall(){
        //TODO: implement
    }

    @Test
    public void testCreateCall(){
        //TODO: implement
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
