package com.yodigi.quiplash.exceptions;

public class InvalidGameIdException extends Exception {
    public InvalidGameIdException() {
        super();
    }

    public InvalidGameIdException(String message) {
        super(message);
    }

    public InvalidGameIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidGameIdException(Throwable cause) {
        super(cause);
    }
}
