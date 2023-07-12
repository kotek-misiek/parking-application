package com.itsharkz.parking.exception;

import java.io.Serial;

import static java.lang.String.format;

public class IncorrectCityException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6991181598680186622L;

    public IncorrectCityException(String cityName) {
        super(format("City of '%s' is not allowed in this application", cityName));
    }
}
