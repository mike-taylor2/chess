package dataaccess;

import service.ResponseException;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends ResponseException {

    public DataAccessException(String message, Exception ex) {
        super(message, 500);
    }
}
