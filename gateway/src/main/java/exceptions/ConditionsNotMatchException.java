package exceptions;

public class ConditionsNotMatchException extends RuntimeException {
    public ConditionsNotMatchException(String message) {
        super(message);
    }
}