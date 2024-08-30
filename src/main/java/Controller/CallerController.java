package Controller;

import Model.ContextBody;
import Model.DbCalls;
import Model.Status;
import Model.Request;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;


public class CallerController {
    private final int BAD_REQUEST_CODE = 400;
    private final int UNAUTHORIZED_CODE = 401;
    private final String BAD_REQUEST_STR = "Bad request";
    private final String UNAUTHORIZED_STR = "Unauthorized";
    private final RequestHandler requestHandler;
    private final ConfigurationController configurationController;
    private ObjectMapper mapper = new ObjectMapper();


    public CallerController(String allowedHostsConfig, String dbUrl, String dbUser, String dbUserPassword) {
        Javalin app = Javalin.create();
//        initRoutes(app);
//        app.start(8080);

//        app.get("/get", ctx -> ctx.status(500).result("This is a test"));//.start(8080);
//        app.post("/post", ctx -> ctx.status(200).result("ok"));
//
//        app.get("/test/{param}", context -> {
//            String bodyText = context.body();
//
//
//            ObjectMapper mapper = new ObjectMapper();
//            ContextBody body = mapper.readValue(context.body(), ContextBody.class);
//
//            String pass = body.getPassword();//context.formParam("Password");
//            String user = body.getUsername();//context.formParam("Username");
//
//            System.out.println(pass);
//
//            System.out.println(user);
//
//
//           context.status(200).result("hello there");
//        });

        app.start(8080);
        configurationController = new ConfigurationController(allowedHostsConfig);
        requestHandler = new RequestHandler(new DbCaller(dbUrl, dbUser, dbUserPassword));
    }

    public void initRoutes(Javalin app){
        app.before(context -> {
            if(!allowedHost(context.host())) {
                context.status(UNAUTHORIZED_CODE).result(UNAUTHORIZED_STR);
            }
        });

        app.patch("/update-password/", this::updatePassword);

        app.delete("/remove-user/", this::removeUser);

        app.post("/create-user/", this::createUser);

        app.get("/authenticate/", this::authenticate);

        app.get("/user/", this::user);


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
/*
    public void initRoutes() {
        options("/*",
                (request, response) -> {
                    String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");

                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request.headers("Access-Control-Request-Method");

                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
                    }

                    return "OK";
                }
        );

        before((request, response) -> {
            if (!allowedHost(request.host())) {
                response.header(UNAUTHORIZED_STR, UNAUTHORIZED_CODE);
            }
            response.header("Access-Control-Allow-Origin", "*");
        });


        patch("/update-password/:username/:password", (req, res) -> {
            if (invalidCall(req.params(":username"), req.params(":password"))) {
                res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
                return res;
            }
            Status requestStatus = requestHandler.
                    performRequest(new Request(req.params(":username"), req.params(":password"), DbCalls.updatePassword));

            setResHeader(requestStatus, res);

            return res;
        });

        delete("/remove/:username", (req, res) -> {
            if (invalidCall(req.params(":username"))) {
                res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
                return res;
            }
            Status requestStatus = requestHandler.
                    performRequest(new Request(req.params(":username"), DbCalls.removeUser));

            setResHeader(requestStatus, res);

            return res;
        });

        post("/create-user/:username/:password", (req, res) -> {
            if (invalidCall(req.params(":username"), req.params(":password"))) {
                res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
                return res;
            }
            System.out.println(req.params(":username") + req.params(":password"));

            Status requestStatus = requestHandler.
                    performRequest(new Request(req.params(":username"), req.params(":password"), DbCalls.createUser));

            setResHeader(requestStatus, res);
            System.out.println(res.body());

            return res;
        });

        get("/authenticate/:username/:password/", (req, res) -> {
            if (invalidCall(req.params(":username"), req.params(":password"))) {
                res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
                return res;
            }

            Status requestStatus = requestHandler.
                    performRequest(new Request(req.params(":username"), req.params(":password"), DbCalls.authenticateUser));

            setResHeader(requestStatus, res);

            return res;
        });

        get("/user/:username", (req, res) -> {
            if (invalidCall(req.params(":username"))) {
                res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
                return res;
            }
            System.out.println(req.params("username"));

            Status requestStatus = requestHandler.
                    performRequest(new Request(req.params(":username"), DbCalls.getUser));

            setResHeader(requestStatus, res);

            System.out.println(res.body());

            return res;
        });

    }

*/
    private void setResponse(Status status, Context context) {
        String INTERNAL_SERVER_ERROR_STR = "Request failed due to internal server error";
        int INTERNAL_SERVER_ERROR_CODE = 500;

        String OK_STR = "OK";
        int OK_CODE = 200;

        String NOT_FOUND_STR = "Not found";
        int NOT_FOUND_CODE = 404;

        switch (status){
            case INTERNAL_SERVER_ERROR -> context.status(INTERNAL_SERVER_ERROR_CODE).result(INTERNAL_SERVER_ERROR_STR);
            case BAD_REQUEST -> context.status(BAD_REQUEST_CODE).result(BAD_REQUEST_STR);
            case NOT_FOUND -> context.status(NOT_FOUND_CODE).result(NOT_FOUND_STR);
            case OK -> context.status(OK_CODE).result(OK_STR);
            case UNAUTHORIZED -> context.status(UNAUTHORIZED_CODE).result(UNAUTHORIZED_STR);
        }
    }
    private boolean allowedHost(String caller) {
        return configurationController.checkIfAllowedHost(caller);
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
