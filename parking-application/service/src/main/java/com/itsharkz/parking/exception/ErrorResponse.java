package com.itsharkz.parking.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static java.time.Instant.now;

@Getter
public class ErrorResponse {
    private final HttpStatus status;
    private final String message;
    private final LocalDateTime timestamp;
    private final String path;

    public ErrorResponse(HttpStatus status, String message, String path) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.ofInstant(now(), ZoneId.systemDefault());
        this.path = path;
    }
}
