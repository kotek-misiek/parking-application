package com.itsharkz.exceptions;

import org.springframework.http.HttpStatus;

public class RemoteServerException extends RuntimeException {
    private final HttpStatus status;

    public RemoteServerException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
