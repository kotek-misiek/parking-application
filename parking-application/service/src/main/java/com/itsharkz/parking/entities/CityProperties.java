package com.itsharkz.parking.entities;

import com.itsharkz.parking.enums.MethodEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CityProperties {
    private String name;
    private String url;
    private MethodEnum method;
    private ParamNames paramNames;
    private String jsonItem;

}
