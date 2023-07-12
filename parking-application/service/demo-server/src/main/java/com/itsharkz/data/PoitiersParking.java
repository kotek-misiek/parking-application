package com.itsharkz.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PoitiersParking {
    private String name;
    private String info;
    private Double lattitude;
    private Double longitude;
    private int capacity;
    private int free;
}
