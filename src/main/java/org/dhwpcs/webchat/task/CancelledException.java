package org.dhwpcs.webchat.task;

public class CancelledException extends RuntimeException {
    public CancelledException() {
    }

    public CancelledException(String message) {
        super(message);
    }

    public CancelledException(String message, Throwable cause) {
        super(message, cause);
    }

    public CancelledException(Throwable cause) {
        super(cause);
    }
}
