package Tests;

import Controller.CallerController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestDBCalls {
    private static CallerController callerController;

    @BeforeAll
    public static void setUp(){
        callerController = new CallerController(System.getenv("mockDbUrl"));
    }

    @Test
    public void testAllCalls(){
        //TODO: implement
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
