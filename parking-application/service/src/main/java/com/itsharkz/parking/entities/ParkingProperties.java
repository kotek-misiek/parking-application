package com.itsharkz.parking.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParkingProperties {
    private String name;
    private String info;
    private double lattitude;
    private double longitude;
    private int capacity;
    private int freePlaces;
}
