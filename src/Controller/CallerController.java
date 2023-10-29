package Controller;

import Model.DbCalls;
import Model.Request;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static spark.Spark.*;

public class CallerController {
    private final int BAD_REQUEST_CODE = 400;
    private final int OK_CODE = 200;
    private final String BAD_REQUEST_STR = "Bad request";
    private final String OK_STR = "OK";

    public CallerController(){
        port(8080);

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
            RequestHandler.addRequest(new Request(req.params(":username"), req.params(":password"), DbCalls.updatePassword));

            res.header(OK_STR, OK_CODE);
            return res;
        });

        delete("/remove/:username", (req, res) -> {
            if (!validateCall(req.params(":username"))){
                res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
                return res;
            }
            RequestHandler.addRequest(new Request(req.params(":username"), null, DbCalls.removeUser));

            res.header(OK_STR, OK_CODE);
            return res;
        });

        post("/new-user/:username/:password", (req, res) -> {
            if(!validateCall(req.params(":username"), req.params(":password"))){
                res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
                return res;
            }
            RequestHandler.addRequest(new Request(req.params(":username"), req.params(":password"), DbCalls.createUser));

            res.header(OK_STR, OK_CODE);
            return res;
        });

        get("/authenticate/:username/:password/", (req, res) -> {
            if(!validateCall(req.params(":username"), req.params(":password"))){
                res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
                return res;
            }
            RequestHandler.addRequest(new Request(req.params(":username"), req.params(":password"), DbCalls.authenticateUser));

            res.header(OK_STR, OK_CODE);
            return res;
        });

        get("/user/:username", (req, res) -> {
            if(!validateCall(req.params(":username"))){
                res.header(BAD_REQUEST_STR, BAD_REQUEST_CODE);
                return res;
            }
            RequestHandler.addRequest(new Request(req.params(":username"), null, DbCalls.getUser));

            res.header(OK_STR, OK_CODE);
            return res;
        });
    }

    private boolean validateCall(String... params){
        boolean valid = true;
        for (String s : params) {
            valid = valid && !s.isBlank();
        }

        return valid;
    }
}
