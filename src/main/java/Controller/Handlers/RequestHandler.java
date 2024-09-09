package Controller.Handlers;

import Controller.Database.DbRequestCaller;
import Model.Request;
import Model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestHandler {
    private final DbRequestCaller dbRequestCaller;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Logger logger;

    public RequestHandler(DbRequestCaller dbRequestCaller) {
        logger = LoggerFactory.getLogger(RequestHandler.class);
        this.dbRequestCaller = dbRequestCaller;
    }

    public Status performRequest(Request request) {
        try {
            return executorService.submit(() -> dbRequestCaller.execute(request)).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("An exception was thrown when performing request", e);
            return Status.INTERNAL_SERVER_ERROR;
        }
    }
}
