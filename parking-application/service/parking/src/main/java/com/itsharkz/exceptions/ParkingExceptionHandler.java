package com.itsharkz.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ControllerAdvice
public class ParkingExceptionHandler {
    private final static Logger LOG = LoggerFactory.getLogger(ParkingExceptionHandler.class);

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
        MissingServletRequestParameterException ex, HttpServletRequest req) {
        return createResponse(req, ex, BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    @ResponseBody
    ResponseEntity<ErrorResponse> handleIOException(IOException ex, HttpServletRequest req) {
        return createResponse(req, ex, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest req) {
        return createResponse(req, ex, UNAUTHORIZED);
    }

    @ExceptionHandler(RemoteServerException.class)
    @ResponseBody
    ResponseEntity<ErrorResponse> handleRemoteServerException(RemoteServerException ex, HttpServletRequest req) {
        return createResponse(req, ex, ex.getStatus());
    }

    protected ResponseEntity<ErrorResponse> createResponse(HttpServletRequest req, Exception ex,
                                                           HttpStatus httpStatus) {
        var message = ex.getMessage();
        var path = req.getServletPath();
        LOG.error(message);
        return new ResponseEntity<>(
            new ErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase(), message, path), httpStatus);
    }
}
