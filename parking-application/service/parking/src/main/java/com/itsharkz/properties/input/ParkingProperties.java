package com.itsharkz.properties.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "parking")
@Getter
@Setter
@AllArgsConstructor
public class ParkingProperties {
    private DefaultProperties basicParams;
    private List<CityProperties> cities;
}
