package Controller;

import Controller.Database.DbAPIRequestCaller;
import Controller.Database.DbRequestCaller;
import Controller.Handlers.AuthenticationHandler;
import Controller.Handlers.RequestHandler;
import Model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


public class CallerController {
    private final RequestHandler requestHandler;
    private final AuthenticationHandler authenticationHandler;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Logger logger;


    //Inject app for testing purposes
    public CallerController(Javalin app, String dbUrl, String dbUser, String dbUserPassword) {
        logger = LoggerFactory.getLogger(CallerController.class);
        initRoutes(app);
        app.start(8080);

        requestHandler = new RequestHandler(new DbRequestCaller(dbUrl, dbUser, dbUserPassword));
        authenticationHandler = new AuthenticationHandler(new DbAPIRequestCaller(dbUrl, dbUser, dbUserPassword));
    }

    public CallerController(String dbUrl, String dbUser, String dbUserPassword) {
        logger = LoggerFactory.getLogger(CallerController.class);
        Javalin app = Javalin.create();
        initRoutes(app);
        app.start(8080);

        requestHandler = new RequestHandler(new DbRequestCaller(dbUrl, dbUser, dbUserPassword));
        authenticationHandler = new AuthenticationHandler(new DbAPIRequestCaller(dbUrl, dbUser, dbUserPassword));
    }

    public void initRoutes(Javalin app){
        app.before(this::beforeRequest);

        app.put("/v1/update-password", this::updatePassword);

        app.delete("/v1/remove-user", this::removeUser);

        app.post("/v1/create-user", this::createUser);

        app.get("/v1/authenticate/{user}", this::authenticateUser);

        app.get("/v1/get-user/{user}", this::getUser);

    }

    private void beforeRequest(Context context) {
        logger.info("Received call from: {} to endpoint: {}", context.host(), context.path());
    }

    private void updatePassword(Context context) throws JsonProcessingException {
        ContextBody contextBody = mapper.readValue(context.body(), ContextBody.class);

        if (invalidCall(contextBody.getUsername(), contextBody.getPassword())){
            logger.warn("Update password failed due to bad request");
            setResponse(Status.BAD_REQUEST, context);
            return;
        }

        Status requestStatus = requestHandler.
                performRequest(new Request(DbCalls.UpdatePassword, contextBody.getUsername(), contextBody.getPassword()
                ));

        setResponse(requestStatus, context);
    }

    private void removeUser(Context context) throws JsonProcessingException {
        ContextBody contextBody = mapper.readValue(context.body(), ContextBody.class);

        if (invalidCall(contextBody.getUsername())){
            logger.warn("Remove user failed due to bad request");
            setResponse(Status.BAD_REQUEST, context);
            return;
        }

        Status requestStatus = requestHandler.
                performRequest(new Request(DbCalls.RemoveUser, contextBody.getUsername()));

        setResponse(requestStatus, context);
    }

    private void createUser(Context context) throws JsonProcessingException {
        ContextBody contextBody = mapper.readValue(context.body(), ContextBody.class);

        if (invalidCall(contextBody.getUsername(), contextBody.getPassword())){
            logger.warn("Create user failed due to bad request");
            setResponse(Status.BAD_REQUEST, context);
            return;
        }

        Status requestStatus = requestHandler.
                performRequest(new Request(DbCalls.CreateUser, contextBody.getUsername(), contextBody.getPassword()
                ));

        setResponse(requestStatus, context);
    }

    private void authenticateUser(Context context) {
        String user = context.pathParam("user");
        String authUsername = Objects.requireNonNull(context.basicAuthCredentials()).getUsername();
        String authPassword = Objects.requireNonNull(context.basicAuthCredentials()).getPassword();

        if (invalidCall(user, authUsername, authPassword) || !user.equals(authUsername)){
            logger.warn("Authentication failed due to bad request");
            setResponse(Status.BAD_REQUEST, context);
            return;
        }

        Status requestStatus = requestHandler.
                performRequest(new Request(DbCalls.AuthenticateUser, authUsername, authPassword));

        setResponse(requestStatus, context);
    }

    private void getUser(Context context) {
        String user = context.pathParam("user");

        if (invalidCall(user)){
            logger.warn("Get user failed due to bad request");
            setResponse(Status.BAD_REQUEST, context);
            return;
        }


        Status requestStatus = requestHandler.
                performRequest(new Request(DbCalls.GetUser, user));

        setResponse(requestStatus, context);
    }

    private void setResponse(Status status, Context context) {
        context.status(status.getCode()).result(status.getMessage());
    }

    private boolean invalidCall(String... params) {
        if (params == null) {
            return true;
        }

        for (String s : params) {
            if (s==null || s.isBlank()) {
                return true;
            }
        }

        return false;
    }
}
