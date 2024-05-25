import Util.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

public class TestUpdateEndpoint extends EndpointParent {

    @Test
    public void testUpdatePasswordSingleUser(){
        User user = getUsers().get(0);
        String response = getCalls().createUser(user.getUsername(), user.getPassword());
        Assumptions.assumeTrue(response.equals(getHTTP_200()));

        response = getCalls().updateUserPassword(user.getUsername(), "New_password");
        Assertions.assertEquals(getHTTP_200(), response);
    }

    @Test
    public void testUpdatePasswordBadRequestNoPassword(){
        User user = getUsers().get(0);
        String response = getCalls().updateUserPassword(user.getUsername(), "");
        Assertions.assertEquals(getHTTP_400(), response);

        response = getCalls().updateUserPassword(user.getUsername(), " "); //Whitespace
        Assertions.assertEquals(getHTTP_400(), response);

        response = getCalls().updateUserPassword(user.getUsername(), null);
        Assertions.assertEquals(getHTTP_400(), response);
    }

    @Test
    public void testUpdatePasswordBadRequestNoUsername(){
        User user = getUsers().get(0);
        String response = getCalls().updateUserPassword("", user.getPassword());
        Assertions.assertEquals(getHTTP_400(), response);

        response = getCalls().updateUserPassword(" ", user.getPassword()); //Whitespace
        Assertions.assertEquals(getHTTP_400(), response);

        response = getCalls().updateUserPassword(null, user.getPassword());
        Assertions.assertEquals(getHTTP_400(), response);
    }

    @Test
    public void testUpdatePasswordBadRequestNoParameters(){
        String response = getCalls().updateUserPassword("", "");
        Assertions.assertEquals(getHTTP_400(), response);

        response = getCalls().updateUserPassword(" ", " "); //Whitespace
        Assertions.assertEquals(getHTTP_400(), response);

        response = getCalls().updateUserPassword(null, null);
        Assertions.assertEquals(getHTTP_400(), response);
    }

    @Test
    public void testUpdatePasswordSamePassword(){
        User user = getUsers().get(0);
        String response = getCalls().createUser(user.getUsername(), user.getPassword());
        Assumptions.assumeTrue(response.equals(getHTTP_200()));

        response = getCalls().updateUserPassword(user.getUsername(), user.getPassword());
        Assertions.assertEquals(response, getHTTP_200());
    }
}
