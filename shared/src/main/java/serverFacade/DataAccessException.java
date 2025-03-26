package serverFacade;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{

    private int statusCode;

    public DataAccessException(Integer statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
