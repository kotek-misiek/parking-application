package com.itsharkz.parking.service;

import com.itsharkz.parking.entities.CitiesProperties;
import lombok.Getter;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Getter
@Service
public class ScaleService {
    private final CitiesProperties citiesProperties;

    public ScaleService(CitiesProperties citiesUrlProperties) {
        this.citiesProperties = citiesUrlProperties;
    }

    public boolean isCityAllowed(String cityName) {
        if (isNull(cityName)) {
            return false;
        }
        return citiesProperties.getCities()
            .stream()
            .anyMatch(city -> city.getName().equalsIgnoreCase(cityName.toLowerCase()));
    }
}
