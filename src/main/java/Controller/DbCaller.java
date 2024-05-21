package Controller;

import Model.Request;
import Model.Status;

import java.sql.*;

public class DbCaller {
    private Connection con;

    public DbCaller(String dbUrl, String dbUser, String dbUserPassword) {
        try {
            con = DriverManager.getConnection(dbUrl, dbUser, dbUserPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() throws SQLException {
        return con.isValid(5000);
    }

    public synchronized Status execute(Request request) {
        switch (request.getCall()) {
            case createUser -> {
                return createUser(request.getUsername(), request.getPassword());
            }

            case removeUser -> {
                return removeUser(request.getUsername());
            }

            case updatePassword -> {
                return updatePassword(request.getUsername(), request.getPassword());
            }

            case getUser -> {
                return userExists(request.getUsername());
            }

            case authenticateUser -> {
                return authenticate(request.getUsername(), request.getPassword());
            }
        }

        return Status.BAD_REQUEST;
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
}
