package com.itsharkz.properties.output;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParkingReturnProperties {
    private String name;
    private String info;
    private Double lattutude;
    private Double longitude;
    private Integer capacity;
    private Integer freePlaces;
}
