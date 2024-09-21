package Model;

public class Request {
    String username;
    String password;
    DbCalls call;

    public Request(DbCalls call, String username, String password) {
        this.username = username;
        this.password = password;
        this.call = call;
    }

    public Request(DbCalls call, String username) {
        this.username = username;
        this.call = call;
    }

    public DbCalls getCall() {
        return call;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
