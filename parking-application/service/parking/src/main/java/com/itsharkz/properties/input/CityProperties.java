package com.itsharkz.properties.input;

import com.itsharkz.enums.AutorizationTypeEnum;
import com.itsharkz.enums.MethodEnum;
import com.itsharkz.enums.ParamsTypeEnum;
import com.itsharkz.enums.ReturnFieldsEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class CityProperties {
    private final String cityName;
    private final String url;
    private final MethodEnum method;
    private final AutorizationTypeEnum authorizationType;
    private final List<String> pathOrder;
    private final ParamsTypeEnum paramsType;
    private final Map<String, String> params;
    private final String jsonItem;
    private final Map<ReturnFieldsEnum, FieldProperties> items;
}
