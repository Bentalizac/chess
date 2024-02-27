package exception;

public class ResponseException extends Exception { // This is the petshop code, it seems to be general purpose so I pasted it over here. TODO likely needs to be tweaked to this implementation
    final private int statusCode;

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int statusCode() {
        return statusCode;
    }
}
