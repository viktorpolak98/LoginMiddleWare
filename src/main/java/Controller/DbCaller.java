package Controller;

import Model.Request;
import Model.Status;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.*;

public class DbCaller {
    private Connection con;
    private final String dbUrl;
    private final String dbUser;
    private final String dbUserPassword;

    public DbCaller(String dbUrl, String dbUser, String dbUserPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbUserPassword = dbUserPassword;
    }

    public synchronized Status execute(Request request) {
        try {
            connect();
        } catch (SQLException e){
            e.printStackTrace();
        }
        Status status = switch (request.getCall()) {
            case createUser -> createUser(request.getUsername(), request.getPassword());

            case removeUser -> removeUser(request.getUsername());

            case updatePassword -> updatePassword(request.getUsername(), request.getPassword());

            case getUser -> userExists(request.getUsername());

            case authenticateUser -> authenticate(request.getUsername(), request.getPassword());

            default -> Status.BAD_REQUEST;
        };

        try {
            disconnect();
        } catch (SQLException e){
            e.printStackTrace();
        }

        return status;
    }


    private Status createUser(String username, String password) {
        try {
            if (isInputInvalid(username, password)){
                return Status.BAD_REQUEST;
            }
            if (userExists(username) != Status.NOT_FOUND){
                return Status.BAD_REQUEST;
            }

            CallableStatement statement = con.prepareCall("{call CreateUser(?,?)}");
            statement.setString(1, username);
            statement.setString(2, password);

            statement.execute();

            if (statement.getUpdateCount() != 1){
                return Status.BAD_REQUEST;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }

        return Status.OK;
    }

    private Status removeUser(String username) {
        if (isInputInvalid(username)){
            return Status.BAD_REQUEST;
        }

        try (CallableStatement statement = con.prepareCall("{call DeleteUser(?)}")) {
            statement.setString(1, username);

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

    private Status updatePassword(String username, String password) {
        if (isInputInvalid(username, password)){
            return Status.BAD_REQUEST;
        }

        try (CallableStatement statement = con.prepareCall("{call UpdatePassword(?,?)}")) {
            statement.setString(1, username);
            statement.setString(2, password);

            statement.execute();

            if (statement.getUpdateCount() != 1){
                return Status.BAD_REQUEST;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }

        return Status.OK;
    }

    private Status userExists(String username) {
        if (isInputInvalid(username)){
            return Status.BAD_REQUEST;
        }

        try (CallableStatement statement = con.prepareCall("{call GetUser(?)}")) {
            statement.setString(1, username);

            statement.execute();

            if (!statement.getResultSet().next()){
                return Status.NOT_FOUND;
            }


        } catch (SQLException e) {
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }

        return Status.OK;
    }

    private Status authenticate(String username, String password) {
        if (isInputInvalid(username, password)){
            return Status.BAD_REQUEST;
        }

        try (CallableStatement statement = con.prepareCall("{call AuthenticateUser(?,?,?)}")) {
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

    private Status checkRequesterKey() throws SQLException {
        //TODO: Implement
        return Status.NOT_FOUND;
    }

    private void connect() throws  SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            con = DriverManager.getConnection(dbUrl, dbUser, dbUserPassword);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean isInputInvalid(String username){
        return username == null || username.trim().isBlank();
    }

    private boolean isInputInvalid(String username, String password){
        return isInputInvalid(username) || (password == null || password.trim().isBlank());
    }

    private void disconnect() throws SQLException {
        con.close();
    }
}
