package Model;

public enum Status {
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad request"),
    INTERNAL_SERVER_ERROR(500, "Request failed due to internal server error"),
    NOT_FOUND(404, "Not found"),
    UNAUTHORIZED(401, "Unauthorized"),
    CREATED(201, "Created"),
    CONFLICT(409, "Conflict");

    private final int code;
    private final String message;

    private Status(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }
}
