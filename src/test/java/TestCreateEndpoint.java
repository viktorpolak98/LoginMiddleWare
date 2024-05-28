import Util.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TestCreateEndpoint extends EndpointParent {

    @Test
    public void testCreateMultipleUsers() {
        List<String> results = new ArrayList<>();

        for (User user : getUsers()) {
            //Create every user from testdata
            String response = getCalls().createUser(user.getUsername(), user.getPassword());

            results.add(response);
        }

        for (String result : results) {
            Assertions.assertEquals(getHTTP_200(), result);
        }
    }

    @Test
    public void testCreateSingleUser() {
        User user = getUsers().get(0);
        String response = getCalls().createUser(user.getUsername(), user.getPassword());

        Assertions.assertEquals(getHTTP_200(), response);
    }

    @Test
    public void testCreateUserBadRequestNoUsername() {
        User user = getUsers().get(0);
        String response = getCalls().createUser("", user.getPassword());
        Assertions.assertEquals(getHTTP_400(), response);

        response = getCalls().createUser(" ", user.getPassword()); //Whitespace
        Assertions.assertEquals(getHTTP_400(), response);

        response = getCalls().createUser(null, user.getPassword());
        Assertions.assertEquals(getHTTP_400(), response);
    }

    @Test
    public void testCreateUserBadRequestNoPassword() {
        User user = getUsers().get(0);
        String response = getCalls().createUser(user.getUsername(), "");
        Assertions.assertEquals(getHTTP_400(), response);

        response = getCalls().createUser(user.getUsername(), " "); //Whitespace
        Assertions.assertEquals(getHTTP_400(), response);

        response = getCalls().createUser(user.getUsername(), null);
        Assertions.assertEquals(getHTTP_400(), response);
    }

    @Test
    public void testCreateExistingUser() {
        User user = getUsers().get(0);

        String response = getCalls().createUser(user.getUsername(), user.getPassword());
        Assumptions.assumeTrue(response.equals(getHTTP_200()));

        response = getCalls().createUser(user.getUsername(), user.getPassword());
        Assertions.assertEquals(getHTTP_400(), response);
    }

    @Test
    public void testRecreateUser() {
        User user = getUsers().get(0);

        String response = getCalls().createUser(user.getUsername(), user.getPassword());
        Assumptions.assumeTrue(response.equals(getHTTP_200()));

        response = getCalls().removeUser(user.getUsername());
        Assumptions.assumeTrue(response.equals(getHTTP_200()));

        response = getCalls().createUser(user.getUsername(), user.getPassword());
        Assertions.assertEquals(getHTTP_200(), response);
    }
}
