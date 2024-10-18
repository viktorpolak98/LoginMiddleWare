package Controller.Database;

import Model.DbAPIKeyRequest;
import Model.Status;
import org.jetbrains.annotations.Nullable;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;

public class DbAPIRequestCaller extends DatabaseBase {

    public DbAPIRequestCaller(String dbUrl, String dbUser, String dbUserPassword){
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
            case CreateUser -> createAPIUser(request.getEmailAddress());
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
        if (isInputInvalid(emailAddress, APIKey)){
            return Status.BAD_REQUEST;
        }

        try (CallableStatement statement = getConnection().prepareCall("{call AuthenticateAPIKey(?,?,?)}")) {
            statement.setString(1, emailAddress);
            statement.setString(2, APIKey);
            statement.registerOutParameter(3, Types.BIT);

            statement.execute();

            if (!statement.getBoolean(3)){
                return Status.UNAUTHORIZED;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }

        return Status.OK;
    }

    private Status invalidateKey(String emailAddress, String APIKey) {
        if (isInputInvalid(emailAddress, APIKey)){
            return Status.BAD_REQUEST;
        }

        try (CallableStatement statement = getConnection().prepareCall("{call InvalidateAPIKey(?,?)}")) {
            statement.setString(1, emailAddress);
            statement.setString(2, APIKey);

            statement.execute();

            if (statement.getUpdateCount() != 1){
                return Status.NOT_FOUND;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }

        return Status.OK;
    }

    private Status createAPIKey(String emailAddress, String APIKey, @Nullable Date validTo) {
        if (isInputInvalid(emailAddress, APIKey)){
            return Status.BAD_REQUEST;
        }

        try (CallableStatement statement = getConnection().prepareCall("{call CreateAPIKey(?,?,?)}")) {
            statement.setString(1, emailAddress);
            statement.setString(2, APIKey);
            statement.setDate(3, validTo);

            statement.execute();

            if (statement.getUpdateCount() != 1){
                return Status.BAD_REQUEST;
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == DUPLICATE_KEY_ERROR_CODE){
                return Status.CONFLICT;
            }
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }

        return Status.CREATED;
    }

    private Status createAPIUser(String emailAddress) {
        if (isInputInvalid(emailAddress)){
            return Status.BAD_REQUEST;
        }

        try (CallableStatement statement = getConnection().prepareCall("{call CreateAPIUser(?)}")) {
            statement.setString(1, emailAddress);

            statement.execute();

            if (statement.getUpdateCount() != 1){
                return Status.BAD_REQUEST;
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == DUPLICATE_KEY_ERROR_CODE){
                return Status.CONFLICT;
            }
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }

        return Status.CREATED;
    }
}
