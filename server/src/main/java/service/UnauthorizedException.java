package service;

public class UnauthorizedException extends ResponseException {

    public UnauthorizedException(String message) {
        super(message, 401);
    }
}
