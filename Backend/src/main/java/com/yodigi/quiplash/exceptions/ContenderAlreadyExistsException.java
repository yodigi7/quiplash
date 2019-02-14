package com.yodigi.quiplash.exceptions;

public class ContenderAlreadyExistsException extends Exception {
    public ContenderAlreadyExistsException() {
        super();
    }

    public ContenderAlreadyExistsException(String message) {
        super(message);
    }

    public ContenderAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContenderAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
