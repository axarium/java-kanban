package exception;

public class NotFoundException extends Error {

    public NotFoundException(String message) {
        super(message);
    }
}