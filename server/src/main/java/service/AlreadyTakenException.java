package service;

public class AlreadyTakenException extends ResponseException {

    public AlreadyTakenException(String message) {
        super(message, 403);
    }
}
