package com.itsharkz.parking.entities;

import com.itsharkz.parking.enums.ParamsTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParamNames {
    private ParamsTypeEnum paramsType;
    private String lattitude;
    private String longitude;
    private String length;
    private String proportion;
    private String lattitudeMin;
    private String lattitudeMax;
    private String longitudeMin;
    private String longitudeMax;
}
