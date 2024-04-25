import Util.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

public class TestGetEndpoint extends EndpointParent {

    @Test
    public void testGetSingleExistingUser() {
        User user = getUsers().get(0);
        //Create user to be fetched
        String response = getCalls().createUser(user.getUsername(), user.getPassword());
        Assumptions.assumeTrue(response.equals(getHTTP_200()));

        //Get user
        response = getCalls().getUser(user.getUsername());
        Assertions.assertEquals(getHTTP_200(), response);
    }
}
