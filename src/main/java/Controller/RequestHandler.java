package Controller;

import Model.Request;
import Model.Status;

import java.sql.SQLException;
import java.util.concurrent.*;

public class RequestHandler {
    private final DbCaller dbCaller;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private boolean connected = true;

    public RequestHandler(DbCaller dbCaller) {
        this.dbCaller = dbCaller;
        checkConnection();
    }

    public Status performRequest(Request request) {
        if (!connected) {
            return Status.INTERNAL_SERVER_ERROR;
        }
        try {
            return executorService.submit(() -> dbCaller.execute(request)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }
    }

    private void checkConnection() {
        new Thread(() -> {
            while (connected) {
                try {
                    connected = dbCaller.isConnected();
                } catch (SQLException e) {
                    e.printStackTrace();
                    connected = false;
                }
            }
        }).start();

    }
}
