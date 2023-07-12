package com.itsharkz.parking.entities;

import com.itsharkz.parking.enums.TypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemsNames {
    private String name;
    private String info;
    private String lattitude;
    private String longitude;
    private String capacity;
    private TypeEnum capacityType;
    private String freePlaces;
    private TypeEnum freePlacesType;
}
