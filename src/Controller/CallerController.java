package Controller;

import static spark.Spark.*;

public class CallerController {
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

                    return response;
                }
        );

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        get("/events/:searchTerm/:startDate/:endDate", (req, res) -> {

            return null;
        });

        get("/tweets/:x/:y/:date", (req, res) -> {

            return null;
        });
    }
}
