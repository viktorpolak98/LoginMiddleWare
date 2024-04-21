package Tests;

import Tests.Util.User;
import org.junit.jupiter.api.*;

public class TestAuthenticateEndpoint extends EndpointParent {

    @Test
    public void testAuthenticateUser(){
        User user = getUsers().get(0);

        String response = getCalls().createUser(user.getUsername(), user.getPassword());
        Assumptions.assumeTrue(response.equals(getHTTP_200()));

        response = getCalls().authenticateUser(user.getUsername(), user.getPassword());
        Assertions.assertEquals(getHTTP_200(), response);
    }
}
