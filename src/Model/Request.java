package Model;

import Controller.RequestHandler;

public class Request {
    String username;
    String password;
    DbCalls call;

    public Request(String username, String password, DbCalls call){
        this.username = username;
        this.password = password;
        this.call = call;
    }

    public Request(String username, DbCalls call){
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
