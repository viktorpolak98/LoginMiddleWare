package Tests;

import Tests.Util.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TestDeleteEndpoint extends EndpointParent {

    @Test
    public void testRemoveSingleExistingUser(){
        User user = getUsers().get(0);
        //Create user to be deleted
        String response = getCalls().createUser(user.getUsername(), user.getPassword());
        Assumptions.assumeTrue(response.equals(getHTTP_200()), "Actual response: " + response);

        //Remove created user
        response = getCalls().removeUser(user.getUsername());
        Assertions.assertEquals(getHTTP_200(), response, "Actual response: " + response);
    }

    @Test
    public void testRemoveMultipleExistingUsers(){
        List<String> results = new ArrayList<>();

        for (User user : getUsers()) {
            //Create every user from testdata
            String response = getCalls().createUser(user.getUsername(), user.getPassword());
            results.add(response);
        }

        for (int i = 0; i < results.size(); i++){
            Assumptions.assumeTrue(results.get(i).equals(getHTTP_200()),
                    "Assume failed on " + i + ". With the response: " + results.get(i));
        }

        for (User user : getUsers()) {
            //Remove every user previously created
            String response = getCalls().removeUser(user.getUsername());
            results.add(response);
        }

        for (int i = 0; i < results.size(); i++) {
            Assertions.assertEquals(getHTTP_200(), results.get(i),
                    "Assert failed on " + i + ". With the response: " + results.get(i));
        }
    }
}
