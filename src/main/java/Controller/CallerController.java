package Controller;

import Model.DbCalls;
import Model.Request;
import Model.Status;
import spark.Response;

import static spark.Spark.*;

public class CallerController {
    private final int BAD_REQUEST_CODE = 400;
    private final int FORBIDDEN_HOST_CODE = 401;
    private final String BAD_REQUEST_STR = "Bad request";
    private final String FORBIDDEN_HOST_STR = "Unauthorized";
    private final RequestHandler requestHandler;
    private final ConfigurationController configurationController;


    public CallerController(String allowedHostsConfig, String dbUrl, String dbUser, String dbUserPassword) {
        port(8080);
        configurationController = new ConfigurationController(allowedHostsConfig);
        requestHandler = new RequestHandler(new DbCaller(dbUrl, dbUser, dbUserPassword));
        initRoutes();
    }

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
                response.header(FORBIDDEN_HOST_STR, FORBIDDEN_HOST_CODE);
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

            Status requestStatus = requestHandler.
                    performRequest(new Request(req.params(":username"), req.params(":password"), DbCalls.createUser));

            setResHeader(requestStatus, res);

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

            Status requestStatus = requestHandler.
                    performRequest(new Request(req.params(":username"), DbCalls.getUser));

            setResHeader(requestStatus, res);

            return res;
        });
    }

    private void setResHeader(Status status, Response res) {
        String INTERNAL_SERVER_ERROR_STR = "Request failed due to internal server error";
        int INTERNAL_SERVER_ERROR_CODE = 500;

        String OK_STR = "OK";
        int OK_CODE = 200;

        String NOT_FOUND_STR = "Not found";
        int NOT_FOUND_CODE = 404;

        switch (status){
            case INTERNAL_SERVER_ERROR -> res.header(INTERNAL_SERVER_ERROR_STR, INTERNAL_SERVER_ERROR_CODE);
            case BAD_REQUEST -> res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
            case NOT_FOUND -> res.header(NOT_FOUND_STR, NOT_FOUND_CODE);
            case OK -> res.header(OK_STR, OK_CODE);
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
