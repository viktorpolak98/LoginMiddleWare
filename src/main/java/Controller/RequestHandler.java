package Controller;

import Model.Request;

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

    public Boolean performRequest(Request request) {
        if (!connected) {
            return false;
        }
        try {
            return executorService.submit(() -> dbCaller.execute(request)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
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
