package Controller.Handlers;

import Controller.Database.DbAPIRequestCaller;
import Model.DbAPIKeyRequest;
import Model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthenticationHandler {
    private final DbAPIRequestCaller caller;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Logger logger;

    public AuthenticationHandler(DbAPIRequestCaller caller) {
        logger = LoggerFactory.getLogger(AuthenticationHandler.class);
        this.caller = caller;
    }

    public Status handleRequest(DbAPIKeyRequest request) {
        try {
            return executorService.submit(() -> caller.execute(request)).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("An exception was thrown when checking key", e);
            return Status.INTERNAL_SERVER_ERROR;
        }
    }
}
