package Controller;

import Model.Request;

import java.sql.*;

public class DbCaller {
    private Connection con;

    public DbCaller(String dbUrl, String dbUser, String dbUserPassword){
        try {
            con = DriverManager.getConnection(dbUrl, dbUser, dbUserPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() throws SQLException {
        return con.isValid(5000);
    }

    public synchronized boolean execute(Request request){
        switch (request.getCall()){
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
        return false;
    }



    private boolean createUser(String username, String password) {
        try(CallableStatement statement = con.prepareCall("{call CreateUser(?,?)}")){
            statement.setString(1, username);
            statement.setString(2, password);

            statement.execute();

            CallableStatement userExists = con.prepareCall("{call GetUser(?)}");
            userExists.setString(1, username);

            return userExists.execute();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean removeUser(String username) {
        try(CallableStatement statement = con.prepareCall("{call DeleteUser(?)}")){
            statement.setString(1, username);

            return statement.execute();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean updatePassword(String username, String password) {
        try(CallableStatement statement = con.prepareCall("{call UpdatePassword(?,?)}")){
            statement.setString(1, username);
            statement.setString(2, password);

            return statement.execute();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean userExists(String username) {
        try(CallableStatement statement = con.prepareCall("{call GetUser(?)}")){
            statement.setString(1, username);

            return statement.execute();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean authenticate(String username, String password) {
        try(CallableStatement statement = con.prepareCall("{call AuthenticateUser(?,?,?)}")){
            statement.setString(1, username);
            statement.setString(2, password);
            statement.registerOutParameter(3, Types.BIT);

            statement.execute();

            return statement.getBoolean(3);
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
