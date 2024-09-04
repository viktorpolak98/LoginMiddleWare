package Controller;

import Model.ContextBody;
import Model.DbCalls;
import Model.Status;
import Model.Request;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CallerController {
    private final int BAD_REQUEST_CODE = 400;
    private final String BAD_REQUEST_STR = "Bad request";
    private final RequestHandler requestHandler;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Logger logger;



    public CallerController(String dbUrl, String dbUser, String dbUserPassword) {
        logger = LoggerFactory.getLogger(CallerController.class);
        Javalin app = Javalin.create();
        initRoutes(app);
        app.start(8080);

        requestHandler = new RequestHandler(new DbCaller(dbUrl, dbUser, dbUserPassword));
    }

    public void initRoutes(Javalin app){
        app.before(this::beforeRequest);

        app.patch("/update-password/", this::updatePassword);

        app.delete("/remove-user/", this::removeUser);

        app.post("/create-user/", this::createUser);

        app.get("/authenticate/", this::authenticate);

        app.get("/user/", this::user);


    }

    private void beforeRequest(Context context) throws JsonProcessingException {
        logger.info("Received call from: {}", context.host());

        ContextBody contextBody = mapper.readValue(context.body(), ContextBody.class);


        if (invalidCall(contextBody.getEmailAddress(), contextBody.getAPIKey())){
            context.status(BAD_REQUEST_CODE).result(BAD_REQUEST_STR);
            return;
        }

        //TODO: Implement check of APIKey and emailAddress

    }

    private void updatePassword(Context context) throws JsonProcessingException {
        ContextBody contextBody = mapper.readValue(context.body(), ContextBody.class);

        if (invalidCall(contextBody.getUsername(), contextBody.getPassword())){
            context.status(BAD_REQUEST_CODE).result(BAD_REQUEST_STR);
            return;
        }

        Status requestStatus = requestHandler.
                performRequest(new Request(contextBody.getUsername(), contextBody.getPassword(),
                        DbCalls.updatePassword));

        setResponse(requestStatus, context);
    }

    private void removeUser(Context context) throws JsonProcessingException {
        ContextBody contextBody = mapper.readValue(context.body(), ContextBody.class);

        if (invalidCall(contextBody.getUsername())){
            context.status(BAD_REQUEST_CODE).result(BAD_REQUEST_STR);
            return;
        }

        Status requestStatus = requestHandler.
                performRequest(new Request(contextBody.getUsername(), DbCalls.removeUser));

        setResponse(requestStatus, context);
    }

    private void createUser(Context context) throws JsonProcessingException {
        ContextBody contextBody = mapper.readValue(context.body(), ContextBody.class);

        if (invalidCall(contextBody.getUsername(), contextBody.getPassword())){
            context.status(BAD_REQUEST_CODE).result(BAD_REQUEST_STR);
            return;
        }

        Status requestStatus = requestHandler.
                performRequest(new Request(contextBody.getUsername(), contextBody.getPassword(),
                        DbCalls.createUser));

        setResponse(requestStatus, context);
    }

    private void authenticate(Context context) throws JsonProcessingException {
        ContextBody contextBody = mapper.readValue(context.body(), ContextBody.class);

        if (invalidCall(contextBody.getUsername(), contextBody.getPassword())){
            context.status(BAD_REQUEST_CODE).result(BAD_REQUEST_STR);
            return;
        }

        Status requestStatus = requestHandler.
                performRequest(new Request(contextBody.getUsername(), contextBody.getPassword(),
                        DbCalls.authenticateUser));

        setResponse(requestStatus, context);
    }

    private void user(Context context) throws JsonProcessingException {
        ContextBody contextBody = mapper.readValue(context.body(), ContextBody.class);

        if (invalidCall(contextBody.getUsername())){
            context.status(BAD_REQUEST_CODE).result(BAD_REQUEST_STR);
            return;
        }

        Status requestStatus = requestHandler.
                performRequest(new Request(contextBody.getUsername(), DbCalls.getUser));

        setResponse(requestStatus, context);
    }

    private void setResponse(Status status, Context context) {
        String INTERNAL_SERVER_ERROR_STR = "Request failed due to internal server error";
        int INTERNAL_SERVER_ERROR_CODE = 500;

        String OK_STR = "OK";
        int OK_CODE = 200;

        String NOT_FOUND_STR = "Not found";
        int NOT_FOUND_CODE = 404;

        String UNAUTHORIZED_STR = "Unauthorized";
        int UNAUTHORIZED_CODE = 401;
        switch (status){
            case INTERNAL_SERVER_ERROR -> context.status(INTERNAL_SERVER_ERROR_CODE).result(INTERNAL_SERVER_ERROR_STR);
            case BAD_REQUEST -> context.status(BAD_REQUEST_CODE).result(BAD_REQUEST_STR);
            case NOT_FOUND -> context.status(NOT_FOUND_CODE).result(NOT_FOUND_STR);
            case OK -> context.status(OK_CODE).result(OK_STR);
            case UNAUTHORIZED -> context.status(UNAUTHORIZED_CODE).result(UNAUTHORIZED_STR);
        }
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
