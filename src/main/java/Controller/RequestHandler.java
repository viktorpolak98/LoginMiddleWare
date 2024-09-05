package Controller;

import Controller.Database.DbRequestCaller;
import Model.Request;
import Model.Status;

import java.util.concurrent.*;

public class RequestHandler {
    private final DbRequestCaller dbRequestCaller;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public RequestHandler(DbRequestCaller dbRequestCaller) {
        this.dbRequestCaller = dbRequestCaller;
    }

    public Status checkRequester(String emailAddress, String APIKey){
        //TODO: Implement
        return Status.NOT_FOUND;
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
