package Controller.Handlers;

import Controller.Database.APIKeyAuthenticator;
import Model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthenticationHandler {
    private final APIKeyAuthenticator authenticator;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Logger logger;

    public AuthenticationHandler(APIKeyAuthenticator authenticator) {
        logger = LoggerFactory.getLogger(AuthenticationHandler.class);
        this.authenticator = authenticator;
    }

    public Status checkRequesterKey(String emailAddress, String APIKey) {
        try {
            return executorService.submit(() -> authenticator.checkRequesterKey(emailAddress, APIKey)).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("An exception was thrown when checking key", e);
            return Status.INTERNAL_SERVER_ERROR;
        }
    }
}
