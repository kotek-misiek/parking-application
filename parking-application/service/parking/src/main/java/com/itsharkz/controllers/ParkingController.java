package com.itsharkz.controllers;

import com.itsharkz.properties.output.ParkingReturnProperties;
import com.itsharkz.service.ParkingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {
    private final static Logger LOG = LoggerFactory.getLogger(ParkingController.class);
    private final ParkingService parkingService;

    public ParkingController(@Value("${spring.application.name}") String applicationName, ParkingService parkingService) {
        LOG.info("{}: start", applicationName);
        this.parkingService = parkingService;
    }

    @GetMapping
    public ResponseEntity<List<ParkingReturnProperties>> parking(@RequestHeader(value = AUTHORIZATION, required = false)
                                                                 String authorization,
                                                                 @RequestParam("city") String cityName,
                                                                 @RequestParam("lattitude") double lattitude,
                                                                 @RequestParam("longitude") double longitude,
                                                                 @RequestParam(value = "length", required = false,
                                                                     defaultValue = "${parking.basicParams.defaultLength}") double length,
                                                                 @RequestParam(value = "proportion", required = false,
                                                                     defaultValue = "${parking.basicParams.proportion}") double screenProportion) throws IOException {
        return ResponseEntity.ok(
            parkingService.getParkingsList(authorization, cityName, lattitude, longitude, length, screenProportion));
    }
}
