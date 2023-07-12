package com.itsharkz.parking.controller;

import com.itsharkz.parking.exception.IncorrectCityException;
import com.itsharkz.parking.service.ScaleService;
import com.itsharkz.parking.validator.CityConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.itsharkz.parking.utils.AppUtils.toFirstCapital;
import static java.lang.String.format;

@RestController
public class ParkingController {
    private final static Logger log = LoggerFactory.getLogger(ParkingController.class);
    private final String applicationName;
    private final ScaleService scaleService;

    public ParkingController(@Value("${spring.application.name}") String applicationName, ScaleService scaleService) {
        this.applicationName = applicationName;
        this.scaleService = scaleService;
    }

    @GetMapping("api/parking")
    @Validated
    public ResponseEntity<String> parking(@RequestParam("city") @CityConstraint @Valid String cityName,
                                          @RequestParam("lattitude") double lattitude,
                                          @RequestParam("longitude") double longitude,
                                          @RequestParam(value = "length", required = false,
                                              defaultValue = "${parking.basicParams.defaultLength}") double length,
                                          @RequestParam(value = "proportion", required = false,
                                              defaultValue = "${parking.basicParams.proportion}") double screenProportion) {
        log.info("{}: start", applicationName);
        if (!scaleService.isCityAllowed(cityName)) {
            throw new IncorrectCityException(cityName);
        }
        return ResponseEntity.ok(format("Greetings from %s!", toFirstCapital(cityName)));
    }
}
