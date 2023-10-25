package Controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    public void createUser(String username, String password) {
    }

    public void removeUser(String username) {
    }

    public void updatePassword(String username, String password) {
    }

    public void getUser(String username) {
    }

    public void authenticate(String username, String password) {
    }
}
