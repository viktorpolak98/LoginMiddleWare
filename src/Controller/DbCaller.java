package Controller;

import Model.Request;

import java.sql.*;

public class DbCaller {
    private Connection con;

    public DbCaller(){
        try {
            con = DriverManager.getConnection(System.getenv("DbUrl"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() throws SQLException {
        return con.isValid(5000);
    }

    public boolean execute(Request request){
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
                return getUser(request.getUsername());
            }

            case authenticateUser -> {
                return authenticate(request.getUsername(), request.getPassword());
            }
        }
        return false;
    }



    private boolean createUser(String username, String password) {
        //TODO: Implement
        return true;
    }

    private boolean removeUser(String username) {
        //TODO: Implement
        return true;
    }

    private boolean updatePassword(String username, String password) {
        //TODO: Implement
        return true;
    }

    private boolean getUser(String username) {
        //TODO: Implement
        return true;
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
