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


public class CallerController {
    private final RequestHandler requestHandler;
    private final AuthenticationHandler authenticationHandler;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Logger logger;



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

        app.patch("/v1/update-password/{user}/{password}", this::updatePassword);

        app.delete("/v1/remove-user/{user}", this::removeUser);

        app.post("/v1/create-user/{user}/{password}", this::createUser);

        app.get("/v1/authenticate/{user}/{password}", this::authenticate);

        app.get("/v1/user/{user}", this::user);


    }

    private void beforeRequest(Context context) throws JsonProcessingException {
        logger.info("Received call from: {}", context.host());

        ContextBody contextBody = mapper.readValue(context.body(), ContextBody.class);


        if (invalidCall(contextBody.getEmailAddress(), contextBody.getAPIKey())){
            setResponse(Status.BAD_REQUEST, context);
            return;
        }

        Status requestStatus = authenticationHandler.
                handleRequest(new DbAPIKeyRequest(DbAPIKeyCalls.AuthenticateKey, contextBody.getEmailAddress(),
                        contextBody.getAPIKey()));

        setResponse(requestStatus, context);
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

    private void authenticate(Context context) throws JsonProcessingException {
        ContextBody contextBody = mapper.readValue(context.body(), ContextBody.class);

        if (invalidCall(contextBody.getUsername(), contextBody.getPassword())){
            logger.warn("Authentication failed due to bad request");
            setResponse(Status.BAD_REQUEST, context);
            return;
        }

        Status requestStatus = requestHandler.
                performRequest(new Request(DbCalls.AuthenticateUser, contextBody.getUsername(), contextBody.getPassword()
                ));

        setResponse(requestStatus, context);
    }

    private void user(Context context) throws JsonProcessingException {
        ContextBody contextBody = mapper.readValue(context.body(), ContextBody.class);

        if (invalidCall(contextBody.getUsername())){
            logger.warn("Get user failed due to bad request");
            setResponse(Status.BAD_REQUEST, context);
            return;
        }

        Status requestStatus = requestHandler.
                performRequest(new Request(DbCalls.GetUser, contextBody.getUsername()));

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
            if (s.isBlank()) {
                return true;
            }
        }

        return false;
    }
}
