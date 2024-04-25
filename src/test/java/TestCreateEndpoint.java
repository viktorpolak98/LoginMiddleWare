import Util.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TestCreateEndpoint extends EndpointParent {

    @Test
    public void testCreateMultipleUsers(){
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
    public void testCreateSingleUser(){
        User user = getUsers().get(0);
        String response = getCalls().createUser(user.getUsername(), user.getPassword());

        Assertions.assertEquals(getHTTP_200(), response);
    }

    @Test
    public void testCreateUserBadRequestNoUsername(){
        User user = getUsers().get(0);
        String response = getCalls().createUser("", user.getPassword());
        Assertions.assertEquals(getHTTP_400(), response);

        response = getCalls().createUser(null, user.getPassword());
        Assertions.assertEquals(getHTTP_400(), response);
    }
}
