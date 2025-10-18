package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    String message;

    public DataAccessException(String message) {
        super(message);
        this.message = message;
    }

    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
    }

    public String getMessage(){
        return message;
    }
}
