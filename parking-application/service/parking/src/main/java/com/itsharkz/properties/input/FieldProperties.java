package com.itsharkz.properties.input;

import com.itsharkz.enums.TypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldProperties {
    private final String name;
    private final TypeEnum type;
}
