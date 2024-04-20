package Tests;

import Tests.Util.User;
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

        for (int i = 0; i < results.size(); i++) {
            Assertions.assertEquals(getHTTP_200(), results.get(i),
                    "Assert failed on " + i + ". With the response: " + results.get(i));
        }
    }

    @Test
    public void testCreateSingleUser(){
        User user = getUsers().get(0);
        String response = getCalls().createUser(user.getUsername(), user.getPassword());

        Assertions.assertEquals(getHTTP_200(), response, "Actual response: " + response);
    }
}
