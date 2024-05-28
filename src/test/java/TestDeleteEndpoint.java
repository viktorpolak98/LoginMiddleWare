import Util.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TestDeleteEndpoint extends EndpointParent {

    @Test
    public void testRemoveSingleExistingUser() {
        User user = getUsers().get(0);
        //Create user to be deleted
        String response = getCalls().createUser(user.getUsername(), user.getPassword());
        Assumptions.assumeTrue(response.equals(getHTTP_200()));

        //Remove created user
        response = getCalls().removeUser(user.getUsername());
        Assertions.assertEquals(getHTTP_200(), response);
    }

    @Test
    public void testRemoveMultipleExistingUsers() {
        List<String> results = new ArrayList<>();

        for (User user : getUsers()) {
            //Create every user from testdata
            String response = getCalls().createUser(user.getUsername(), user.getPassword());
            results.add(response);
        }

        for (String result : results) {
            Assumptions.assumeTrue(result.equals(getHTTP_200()));
        }

        for (User user : getUsers()) {
            //Remove every user previously created
            String response = getCalls().removeUser(user.getUsername());
            results.add(response);
        }

        for (String result : results) {
            Assertions.assertEquals(getHTTP_200(), result);
        }
    }

    @Test
    public void testRemoveBadRequest() {
        String response = getCalls().removeUser("");
        Assertions.assertEquals(getHTTP_400(), response);

        response = getCalls().removeUser(" "); //Whitespace
        Assertions.assertEquals(getHTTP_400(), response);

        response = getCalls().removeUser(null);
        Assertions.assertEquals(getHTTP_400(), response);
    }

    @Test
    public void testRemoveRecreatedUser() {
        User user = getUsers().get(0);

        String response = getCalls().createUser(user.getUsername(), user.getPassword());
        Assumptions.assumeTrue(response.equals(getHTTP_200()));

        response = getCalls().removeUser(user.getUsername());
        Assumptions.assumeTrue(response.equals(getHTTP_200()));

        response = getCalls().createUser(user.getUsername(), user.getPassword());
        Assumptions.assumeTrue(response.equals(getHTTP_200()));

        response = getCalls().removeUser(user.getUsername());
        Assertions.assertEquals(getHTTP_200(), response);
    }

}
