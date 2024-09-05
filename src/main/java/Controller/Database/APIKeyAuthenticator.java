package Controller.Database;

import Model.Status;

import java.sql.SQLException;

public class APIKeyAuthenticator extends DatabaseBase {

    public APIKeyAuthenticator(String dbUrl, String dbUser, String dbUserPassword){
        super(dbUrl, dbUser, dbUserPassword);
    }

    public Status checkRequesterKey(String emailAddress, String APIKey) throws SQLException {
        //TODO: Implement
        return Status.NOT_FOUND;
    }
}
