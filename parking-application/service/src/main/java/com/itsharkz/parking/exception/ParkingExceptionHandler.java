package com.itsharkz.parking.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class ParkingExceptionHandler {
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    ResponseEntity<ErrorResponse> handleCMissingServletRequestParameterException(
        MissingServletRequestParameterException ex, HttpServletRequest req) {
        return createResponse(req, ex, BAD_REQUEST);
    }

    @ExceptionHandler(IncorrectCityException.class)
    @ResponseBody
    ResponseEntity<ErrorResponse> handleIncorrectCityException(IncorrectCityException ex, HttpServletRequest req) {
        return createResponse(req, ex, BAD_REQUEST);
    }

    protected ResponseEntity<ErrorResponse> createResponse(HttpServletRequest req, Exception ex,
                                                           HttpStatus httpStatus) {
        var message = ex.getMessage();
        var path = req.getServletPath();
        return new ResponseEntity<>(new ErrorResponse(httpStatus, message, path), httpStatus);
    }
}
