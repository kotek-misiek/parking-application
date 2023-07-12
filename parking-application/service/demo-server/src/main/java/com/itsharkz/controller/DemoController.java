package com.itsharkz.controller;

import com.auth0.jwt.JWT;
import com.itsharkz.data.PoitiersParking;
import com.itsharkz.service.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
public class DemoController {
    private final static Logger log = LoggerFactory.getLogger(DemoController.class);
    private final static String START = "{}: start {}";
    private final static String POITIERS = "Poitiers";
    private final static String MONTPELLIER = "Montpellier";
    private final String applicationName;
    private final DemoService demoService;

    public DemoController(@Value("${spring.application.name}") String applicationName, DemoService demoService) {
        this.applicationName = applicationName;
        this.demoService = demoService;
    }

    @GetMapping("demo/poitiers")
    @Validated
    public ResponseEntity<List<PoitiersParking>> poitiers(@RequestParam("lattitude") double lattitude,
                                                          @RequestParam("longitude") double longitude,
                                                          @RequestParam("length") double length,
                                                          @RequestParam("proportion") double proportion) {
        log.info(START, applicationName, POITIERS);
        final var headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(
            demoService.getPoitiersParkings(lattitude, longitude, length, proportion), headers, OK);
    }

    @PostMapping("demo/montpellier/{lat-min}/{lat-max}/{long-min}/{long-max}")
    @Validated
    public ResponseEntity<String> montpellier(@PathVariable("lat-min") double latMin,
                                              @PathVariable("lat-max") double latMax,
                                              @PathVariable("long-min") double longMin,
                                              @PathVariable("long-max") double longMax) {
        log.info(START, applicationName, MONTPELLIER);
        final var headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(demoService.getMontpellierParkings(latMin, latMax, longMin, longMax), headers, OK);
    }

    @GetMapping("demo/token")
    private ResponseEntity<String> createToken() {
        final var token = JWT.create()
            .withIssuer("ITSharkz")
            .withSubject("ITSharkz test")
            .withClaim("userId", "mtokurow")
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + 600000L))
            .withJWTId(UUID.randomUUID().toString())
            .withNotBefore(new Date(System.currentTimeMillis() + 1000L))
            .sign(HMAC256("ITSharkz"));
        final var headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, TEXT_PLAIN_VALUE);
        return new ResponseEntity<>(token, headers, OK);
    }
}
