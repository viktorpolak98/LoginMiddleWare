package Tests;

import Tests.Util.User;
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
}
