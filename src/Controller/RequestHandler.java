package Controller;

import Model.Request;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

public class RequestHandler {
    private final DbCaller dbCaller = new DbCaller();

    public CompletableFuture<Boolean> performRequest(Request request){
        return CompletableFuture.supplyAsync(() -> dbCaller.execute(request));
    }
}
