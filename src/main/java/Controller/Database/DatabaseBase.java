package Controller.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DatabaseBase {
    private final String dbUrl;
    private final String dbUser;
    private final String dbUserPassword;
    private Connection con;

    protected static final int DUPLICATE_KEY_ERROR_CODE = 2627;
    public DatabaseBase(String dbUrl, String dbUser, String dbUserPassword){
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbUserPassword = dbUserPassword;
    }

    protected void connect() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            con = DriverManager.getConnection(dbUrl, dbUser, dbUserPassword);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected Connection getConnection(){
        return con;
    }

    protected void disconnect() throws SQLException {
        con.close();
    }

    protected boolean isInputInvalid(String... input){
        for (String s : input) {
            if (s == null || s.trim().isBlank()){
                return true;
            }
        }

        return false;
    }
}
