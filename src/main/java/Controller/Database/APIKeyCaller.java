package Controller.Database;

import Model.DbAPIKeyRequest;
import Model.Status;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;
import java.sql.SQLException;

public class APIKeyCaller extends DatabaseBase {

    public APIKeyCaller(String dbUrl, String dbUser, String dbUserPassword){
        super(dbUrl, dbUser, dbUserPassword);
    }

    public synchronized Status execute(DbAPIKeyRequest request) {
        try {
            connect();
        } catch (SQLException e){
            e.printStackTrace();
        }

        Status status = switch (request.getCall()){
            case CreateKey -> createAPIKey(request.getEmailAddress(), request.getAPIKey(), request.getValidTo());
            case AuthenticateKey -> AuthenticateKey(request.getEmailAddress(), request.getAPIKey());
            case InvalidateKey -> invalidateKey(request.getEmailAddress(), request.getAPIKey());
            default -> Status.NOT_FOUND;
        };

        try {
            disconnect();
        } catch (SQLException e){
            e.printStackTrace();
        }

        return status;
    }

    private Status AuthenticateKey(String emailAddress, String APIKey) {
        //TODO: Implement
        return Status.NOT_FOUND;
    }

    private Status invalidateKey(String emailAddress, String APIKey) {
        //TODO: Implement
        return Status.NOT_FOUND;
    }

    private Status createAPIKey(String emailAddress, String APIKey, @Nullable Date validTo) {
        //TODO: Implement
        return Status.NOT_FOUND;
    }
}
