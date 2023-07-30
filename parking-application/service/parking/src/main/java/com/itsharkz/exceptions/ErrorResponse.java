package com.itsharkz.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static java.time.Instant.now;

@Getter
public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.ofInstant(now(), ZoneId.systemDefault());
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
