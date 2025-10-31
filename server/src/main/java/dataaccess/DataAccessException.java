package dataaccess;

import service.ResponseException;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends ResponseException {
    private final int statusCode;

    public DataAccessException(String message, Exception ex) {
        super(message, 500);
        this.statusCode = 500;
    }

    public int getStatusCode(){return statusCode;}
}
