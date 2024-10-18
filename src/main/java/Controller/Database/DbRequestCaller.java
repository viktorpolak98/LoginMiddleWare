package Controller.Database;

import Model.Request;
import Model.Status;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

public class DbRequestCaller extends DatabaseBase {

    public DbRequestCaller(String dbUrl, String dbUser, String dbUserPassword) {
        super(dbUrl, dbUser, dbUserPassword);
    }

    public synchronized Status execute(Request request) {
        try {
            connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Status status = switch (request.getCall()) {
            case CreateUser -> createUser(request.getUsername(), request.getPassword());

            case RemoveUser -> removeUser(request.getUsername());

            case UpdatePassword -> updatePassword(request.getUsername(), request.getPassword());

            case GetUser -> userExists(request.getUsername());

            case AuthenticateUser -> authenticate(request.getUsername(), request.getPassword());

            default -> Status.BAD_REQUEST;
        };

        try {
            disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return status;
    }


    private Status createUser(String username, String password) {
        if (isInputInvalid(username, password)) {
            return Status.BAD_REQUEST;
        }

        try (CallableStatement statement = getConnection().prepareCall("{call CreateUser(?,?)}")) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.execute();

            if (statement.getUpdateCount() != 1) {
                return Status.BAD_REQUEST;
            }


        } catch (SQLException e) {
            if (e.getErrorCode() == DUPLICATE_KEY_ERROR_CODE) {
                return Status.CONFLICT;
            }
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }

        return Status.CREATED;
    }

    private Status removeUser(String username) {
        if (isInputInvalid(username)) {
            return Status.BAD_REQUEST;
        }

        try (CallableStatement statement = getConnection().prepareCall("{call DeleteUser(?)}")) {
            statement.setString(1, username);

            statement.execute();

            if (statement.getUpdateCount() != 1) {
                return Status.NOT_FOUND;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }

        return Status.OK;
    }

    private Status updatePassword(String username, String password) {
        if (isInputInvalid(username, password)) {
            return Status.BAD_REQUEST;
        }

        try (CallableStatement statement = getConnection().prepareCall("{call UpdatePassword(?,?)}")) {
            statement.setString(1, username);
            statement.setString(2, password);

            statement.execute();

            if (statement.getUpdateCount() != 1) {
                return Status.BAD_REQUEST;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }

        return Status.OK;
    }

    private Status userExists(String username) {
        if (isInputInvalid(username)) {
            return Status.BAD_REQUEST;
        }

        try (CallableStatement statement = getConnection().prepareCall("{call GetUser(?)}")) {
            statement.setString(1, username);

            statement.execute();

            if (!statement.getResultSet().next()) {
                return Status.NOT_FOUND;
            }


        } catch (SQLException e) {
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }

        return Status.OK;
    }

    private Status authenticate(String username, String password) {
        if (isInputInvalid(username, password)) {
            return Status.BAD_REQUEST;
        }

        try (CallableStatement statement = getConnection().prepareCall("{call AuthenticateUser(?,?,?)}")) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.registerOutParameter(3, Types.BIT);

            statement.execute();

            if (!statement.getBoolean(3)) {
                return Status.UNAUTHORIZED;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }

        return Status.OK;
    }
}
