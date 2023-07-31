package com.itsharkz.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
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
import java.util.Date;
import java.util.List;

import static java.util.Objects.isNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final static Logger LOG = LoggerFactory.getLogger(JwtRequestFilter.class);
    private final static String AUTH_NULL_ERROR = "Authorization header doesn't exist";
    private final static String AUTH_NO_BEARER_ERROR = "Authorization header doesn't contain the keyword Bearer";
    private final static String AUTH_INCORRECT_ISSUER_ERROR = "Incorrect issuer";
    private final static String AUTH_INCORRECT_TOKEN_ERROR = "Incorrect token";
    private final static String AUTH_TOKEN_EXPIRED_ERROR = "Token expired";
    private final static String PATH_ENTERED = "Path entered {}";
    private final static String BEARER_ = "Bearer ";
    private final static String ISSUER_CORRECT = "ITSharkz";
    private final List<String> pathExcluded;

    public JwtRequestFilter(@Value("${filter.path.excluded}") String[] pathExcluded) {
        this.pathExcluded = Arrays.asList(pathExcluded);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        LOG.info(PATH_ENTERED, request.getServletPath());
        if (!pathExcluded.contains(request.getServletPath())) {
            final String authorization = request.getHeader(AUTHORIZATION);
            if (!checkAuthorization(authorization)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private boolean checkAuthorization(String authorization) {
        if (isNull(authorization)) {
            LOG.error(AUTH_NULL_ERROR);
            return false;
        } else if (!authorization.startsWith(BEARER_)) {
            LOG.error(AUTH_NO_BEARER_ERROR);
            return false;
        } else {
            final var jwt = authorization.substring(7);
            try {
                final var decodedJWT = JWT.decode(jwt);
                if (!ISSUER_CORRECT.equals(decodedJWT.getIssuer())) {
                    LOG.error(AUTH_INCORRECT_ISSUER_ERROR);
                    return false;
                } else if (decodedJWT.getExpiresAt().before(new Date())) {
                    LOG.error(AUTH_TOKEN_EXPIRED_ERROR);
                    return false;
                }
            } catch (JWTDecodeException e) {
                LOG.error(AUTH_INCORRECT_TOKEN_ERROR);
                return false;
            }
        }
        return true;
    }
}
