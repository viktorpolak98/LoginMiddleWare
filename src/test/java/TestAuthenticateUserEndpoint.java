import Util.User;
import org.junit.jupiter.api.*;

public class TestAuthenticateUserEndpoint extends EndpointParent {

    @Test
    public void testAuthenticateUser() {
        User user = getUsers().get(0);

        String response = getCalls().createUser(user.getUsername(), user.getPassword());
        Assumptions.assumeTrue(response.equals(getHTTP_200()));

        response = getCalls().authenticateUser(user.getUsername(), user.getPassword());
        Assertions.assertEquals(getHTTP_200(), response);
    }

    @Test
    public void testAuthenticateUserBadRequest() {
        User user = getUsers().get(0);

        String response = getCalls().createUser(user.getUsername(), user.getPassword());
        Assumptions.assumeTrue(response.equals(getHTTP_200()));

        response = getCalls().authenticateUser(user.getUsername(), "invalid password");
        Assertions.assertEquals(getHTTP_400(), response);
    }

    @Test
    public void testAuthenticateUserExistingPasswordWrongUser() {
        User user1 = getUsers().get(0);
        User user2 = getUsers().get(1);

        String response = getCalls().createUser(user1.getUsername(), user1.getPassword());
        Assumptions.assumeTrue(response.equals(getHTTP_200()));

        response = getCalls().createUser(user2.getUsername(), user2.getPassword());
        Assumptions.assumeTrue(response.equals(getHTTP_200()));

        response = getCalls().authenticateUser(user1.getUsername(), user2.getPassword());
        Assertions.assertEquals(getHTTP_400(), response);
    }

    @Test
    public void testAuthenticateNonExistentUser() {
        String response = getCalls().authenticateUser(null, null);
        Assertions.assertEquals(getHTTP_400(), response);

        response = getCalls().authenticateUser("", "");
        Assertions.assertEquals(getHTTP_400(), response);

        response = getCalls().authenticateUser(" ", " "); //Whitespace
        Assertions.assertEquals(getHTTP_400(), response);

        response = getCalls().authenticateUser("does not exist", "does not exist");
        Assertions.assertEquals(getHTTP_400(), response);
    }

    @Test
    public void testAuthenticateBadRequestNoParameter() {
        User user = getUsers().get(0);

        String response = getCalls().createUser(user.getUsername(), user.getPassword());
        Assumptions.assumeTrue(response.equals(getHTTP_200()));

        response = getCalls().authenticateUser(user.getUsername(), null);
        Assertions.assertEquals(getHTTP_400(), response);

        response = getCalls().authenticateUser(user.getUsername(), "");
        Assertions.assertEquals(getHTTP_400(), response);
    }
}
