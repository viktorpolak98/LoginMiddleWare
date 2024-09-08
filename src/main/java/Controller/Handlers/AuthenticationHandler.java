package Controller.Handlers;

import Controller.Database.APIKeyAuthenticator;
import Model.Status;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthenticationHandler {
    private final APIKeyAuthenticator authenticator;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public AuthenticationHandler(APIKeyAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    public Status checkRequesterKey(String emailAddress, String APIKey) {
        try {
            return executorService.submit(() -> authenticator.checkRequesterKey(emailAddress, APIKey)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Status.INTERNAL_SERVER_ERROR;
        }
    }
}
