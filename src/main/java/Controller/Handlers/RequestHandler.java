package Controller.Handlers;

import Controller.Database.DbRequestCaller;
import Model.Request;
import Model.Status;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestHandler {
    private final DbRequestCaller dbRequestCaller;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public RequestHandler(DbRequestCaller dbRequestCaller) {
        this.dbRequestCaller = dbRequestCaller;
    }

    public Status performRequest(Request request) {
        try {
            return executorService.submit(() -> dbRequestCaller.execute(request)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }
    }
}
