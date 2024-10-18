package Model;

public enum Status {
    OK(),
    BAD_REQUEST(),
    INTERNAL_SERVER_ERROR(),
    NOT_FOUND(),
    UNAUTHORIZED(),
    CREATED(),
    CONFLICT();

    private int code;
    private String message;

    private Status(){
        setCodeAndMessage(this);
    }

    public int getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }

    private void setCodeAndMessage(Status status) {
        switch (status) {
            case OK -> {
                code = 200;
                message = "OK";
            }
            case BAD_REQUEST -> {
                code = 400;
                message = "Bad request";
            }
            case INTERNAL_SERVER_ERROR -> {
                code = 500;
                message = "Request failed due to internal server error";
            }
            case NOT_FOUND -> {
                code = 404;
                message = "Not found";
            }
            case UNAUTHORIZED -> {
                code = 401;
                message = "Unauthorized";
            }
            case CREATED -> {
                code = 201;
                message = "Created";
            }
            case CONFLICT -> {
                code = 409;
                message = "Conflict";
            }
        }
    }
}
