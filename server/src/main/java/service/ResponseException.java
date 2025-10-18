package service;

public class ResponseException extends RuntimeException {
    private final String message;

    public ResponseException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
