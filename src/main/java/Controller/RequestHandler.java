package Controller;

import Model.Request;
import Model.Status;

import java.sql.SQLException;
import java.util.concurrent.*;

public class RequestHandler {
    private final DbCaller dbCaller;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public RequestHandler(DbCaller dbCaller) {
        this.dbCaller = dbCaller;
        checkConnection();
    }

    public Status performRequest(Request request) {
        if (!checkConnection()) {
            return Status.INTERNAL_SERVER_ERROR;
        }
        try {
            return executorService.submit(() -> dbCaller.execute(request)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }
    }

    private boolean checkConnection() {
        try {
            return dbCaller.isConnected();
        } catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }
}
