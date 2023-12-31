package Controller;

import Model.DbCalls;
import Model.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.*;

public class CallerController {
    private final int INTERNAL_SERVER_ERROR_CODE = 500;
    private final int BAD_REQUEST_CODE = 400;
    private final int OK_CODE = 200;
    private final String INTERNAL_SERVER_ERROR_STR = "Request failed due to internal server error";
    private final String BAD_REQUEST_STR = "Bad request";
    private final String OK_STR = "OK";
    private final RequestHandler requestHandler;

    public CallerController(String dbUrl){
        port(8080);
        requestHandler = new RequestHandler(new DbCaller(dbUrl));
        try{
            initRoutes();
        } catch (Exception e){
            e.printStackTrace();
        }

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

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));


        patch("/update-password/:username/:password", (req, res) -> {
            if (!validateCall(req.params(":username"), req.params(":password"))){
                res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
                return res;
            }
            boolean requestStatus = requestHandler.
                    performRequest(new Request(req.params(":username"), req.params(":password"), DbCalls.updatePassword));

            setResHeader(requestStatus, res);

            return res;
        });

        delete("/remove/:username", (req, res) -> {
            if (!validateCall(req.params(":username"))){
                res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
                return res;
            }
            boolean requestStatus = requestHandler.
                    performRequest(new Request(req.params(":username"), DbCalls.removeUser));

            setResHeader(requestStatus, res);

            return res;
        });

        post("/new-user/:username/:password", (req, res) -> {
            if(!validateCall(req.params(":username"), req.params(":password"))){
                res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
                return res;
            }

            boolean requestStatus = requestHandler.
                    performRequest(new Request(req.params(":username"), req.params(":password"), DbCalls.createUser));

            setResHeader(requestStatus, res);

            return res;
        });

        get("/authenticate/:username/:password/", (req, res) -> {
            if(!validateCall(req.params(":username"), req.params(":password"))){
                res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
                return res;
            }

            boolean requestStatus = requestHandler.
                    performRequest(new Request(req.params(":username"), req.params(":password"), DbCalls.authenticateUser));

            setResHeader(requestStatus, res);

            return res;
        });

        get("/user/:username", (req, res) -> {
            if(!validateCall(req.params(":username"))){
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

    private boolean validateCall(String... params){
        boolean valid = true;
        for (String s : params) {
            valid = valid && !s.isBlank();
        }

        return valid;
    }
}
