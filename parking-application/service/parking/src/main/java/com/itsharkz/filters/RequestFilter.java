package com.itsharkz.filters;

import com.itsharkz.service.ParkingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

@Component
public class RequestFilter extends OncePerRequestFilter {
    private final static Logger LOG = LoggerFactory.getLogger(RequestFilter.class);
    private final static String CITY = "city";
    private final static String CITY_NOT_ALLOWED = "City of %s is not allowed";
    private final List<String> pathIncluded;
    private final ParkingService parkingService;

    public RequestFilter(@Value("${spring.application.filter.path.included}") String[] pathIncluded,
                         ParkingService parkingService) {
        this.pathIncluded = Arrays.asList(pathIncluded);
        this.parkingService = parkingService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (pathIncluded.contains(request.getServletPath())) {
            final String cityName = request.getParameter(CITY);
            if (!parkingService.isCityAllowed(cityName)) {
                final var errorMessage = format(CITY_NOT_ALLOWED, cityName);
                LOG.error(errorMessage);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
