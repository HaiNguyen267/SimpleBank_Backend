package hainguyen.tech.SimpleBank.exception;

public class AccountNoNotFound extends RuntimeException {
    public AccountNoNotFound(String message) {
        super(message);
    }
}

