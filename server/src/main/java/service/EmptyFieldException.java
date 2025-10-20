package service;

public class EmptyFieldException extends ResponseException {

    public EmptyFieldException(String message) {
        super(message, 400);
    }
}
