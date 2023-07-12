package com.itsharkz.parking.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Params {
    private double lattitude;
    private double longitude;
    private double length;
    private double proportion;
    private double lattitudeMin;
    private double lattitudeMax;
    private double longitudeMin;
    private double longitudeMax;
}
