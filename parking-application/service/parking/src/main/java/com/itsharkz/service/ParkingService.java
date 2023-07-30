package com.itsharkz.service;

import com.itsharkz.connection.RequestSender;
import com.itsharkz.properties.input.CityProperties;
import com.itsharkz.properties.input.ParkingProperties;
import com.itsharkz.properties.output.ParkingReturnProperties;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Service
public class ParkingService {
    private final List<String> cityNames;
    private final ParkingProperties parkingProperties;

    public ParkingService(ParkingProperties parkingProperties) {
        this.parkingProperties = parkingProperties;
        this.cityNames = parkingProperties.getCities()
            .stream()
            .map(CityProperties::getCityName)
            .toList();
    }

    public List<ParkingReturnProperties> getParkingsList(String authorization, String cityName, double lattitude,
                                                         double longitude, double length, double screenProportion) throws IOException {
        final var cityProperties = parkingProperties.getCities()
            .stream()
            .filter(property -> property.getCityName().equalsIgnoreCase(cityName))
            .findFirst()
            .get();
        final var sender = RequestSender.create(cityProperties, authorization, lattitude, longitude, length,
            screenProportion);
        return sender.send();
    }

    public boolean isCityAllowed(String cityName) {
        if (isNull(cityName)) {
            return false;
        }
        return cityNames.contains(cityName);
    }
}
