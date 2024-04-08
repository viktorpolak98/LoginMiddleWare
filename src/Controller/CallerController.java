package Controller;

import Model.DbCalls;
import Model.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.*;

public class CallerController {
    private final int INTERNAL_SERVER_ERROR_CODE = 500;
    private final int BAD_REQUEST_CODE = 400;
    private final int FORBIDDEN_HOST_CODE = 401;
    private final int OK_CODE = 200;
    private final String INTERNAL_SERVER_ERROR_STR = "Request failed due to internal server error";
    private final String BAD_REQUEST_STR = "Bad request";
    private final String OK_STR = "OK";
    private final RequestHandler requestHandler;
    private final ConfigurationController configurationController;


    public CallerController(String dbUrl, String allowedHostsConfig){
        port(8080);
        configurationController = new ConfigurationController(allowedHostsConfig);
        requestHandler = new RequestHandler(new DbCaller(dbUrl));
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
            if (!allowedHost(request.host())){
                response.header(BAD_REQUEST_STR, FORBIDDEN_HOST_CODE);
            }
            response.header("Access-Control-Allow-Origin", "*");
        });


        patch("/update-password/:username/:password", (req, res) -> {
            if (invalidCall(req.params(":username"), req.params(":password"))){
                res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
                return res;
            }
            boolean requestStatus = requestHandler.
                    performRequest(new Request(req.params(":username"), req.params(":password"), DbCalls.updatePassword));

            setResHeader(requestStatus, res);

            return res;
        });

        delete("/remove/:username", (req, res) -> {
            if (invalidCall(req.params(":username"))){
                res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
                return res;
            }
            boolean requestStatus = requestHandler.
                    performRequest(new Request(req.params(":username"), DbCalls.removeUser));

            setResHeader(requestStatus, res);

            return res;
        });

        post("/create-user/:username/:password", (req, res) -> {
            if(invalidCall(req.params(":username"), req.params(":password"))){
                res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
                return res;
            }

            boolean requestStatus = requestHandler.
                    performRequest(new Request(req.params(":username"), req.params(":password"), DbCalls.createUser));

            setResHeader(requestStatus, res);

            return res;
        });

        get("/authenticate/:username/:password/", (req, res) -> {
            if(invalidCall(req.params(":username"), req.params(":password"))){
                res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
                return res;
            }

            boolean requestStatus = requestHandler.
                    performRequest(new Request(req.params(":username"), req.params(":password"), DbCalls.authenticateUser));

            setResHeader(requestStatus, res);

            return res;
        });

        get("/user/:username", (req, res) -> {
            if(invalidCall(req.params(":username"))){
                res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
                return res;
            }

            boolean requestStatus = requestHandler.
                    performRequest(new Request(req.params(":username"), DbCalls.getUser));

            setResHeader(requestStatus, res);

            return res;
        });
    }

    private void setResHeader(boolean ok, Response res){
        if (ok){
            res.header(OK_STR, OK_CODE);
        } else {
            res.header(INTERNAL_SERVER_ERROR_STR, INTERNAL_SERVER_ERROR_CODE);
        }
    }

    private boolean allowedHost(String caller){
        return configurationController.checkIfAllowedHost(caller);
    }

    private boolean invalidCall(String... params){
        for (String s : params) {
            if(s.isBlank()){
                return true;
            }
        }

        return false;
    }
}
