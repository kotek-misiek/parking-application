package com.itsharkz.properties.input;

import com.itsharkz.enums.MethodEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RequestProperties {
    private final MethodEnum method;
    private final ParamProperties params;
}

