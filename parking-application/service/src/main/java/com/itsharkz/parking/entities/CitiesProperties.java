package com.itsharkz.parking.entities;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties("parking")
@Getter
public class CitiesProperties {
    private final List<CityProperties> cities = new ArrayList<>();
}
