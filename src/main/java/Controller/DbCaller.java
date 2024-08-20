package Controller;

import Model.Request;
import Model.Status;

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

    public boolean isConnected() throws SQLException {
        return con.isValid(5000);
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
        try (CallableStatement statement = con.prepareCall("{call CreateUser(?,?)}")) {
            statement.setString(1, username);
            statement.setString(2, password);

            statement.execute();

            CallableStatement userExists = con.prepareCall("{call GetUser(?)}");
            userExists.setString(1, username);

            if (!userExists.execute()) {

                return Status.BAD_REQUEST;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }

        return Status.OK;
    }

    private Status removeUser(String username) {
        try (CallableStatement statement = con.prepareCall("{call DeleteUser(?)}")) {
            statement.setString(1, username);

            if (!statement.execute()) {
                return Status.BAD_REQUEST;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }

        return Status.OK;
    }

    private Status updatePassword(String username, String password) {
        try (CallableStatement statement = con.prepareCall("{call UpdatePassword(?,?)}")) {
            statement.setString(1, username);
            statement.setString(2, password);

            if (!statement.execute()) {
                return Status.BAD_REQUEST;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }

        return Status.OK;
    }

    private Status userExists(String username) {
        try (CallableStatement statement = con.prepareCall("{call GetUser(?)}")) {
            statement.setString(1, username);

            if (!statement.execute()) {
                return Status.NOT_FOUND;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }

        return Status.OK;
    }

    private Status authenticate(String username, String password) {
        try (CallableStatement statement = con.prepareCall("{call AuthenticateUser(?,?,?)}")) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.registerOutParameter(3, Types.BIT);

            statement.execute();

            if (!statement.getBoolean(3)) {
                return Status.BAD_REQUEST;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }

        return Status.OK;
    }

    private void connect() throws  SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            con = DriverManager.getConnection(dbUrl, dbUser, dbUserPassword);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() throws SQLException {
        con.close();
    }
}
