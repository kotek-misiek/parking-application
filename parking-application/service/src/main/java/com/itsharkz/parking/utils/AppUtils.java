package com.itsharkz.parking.utils;

import static java.util.Objects.isNull;

public class AppUtils {
    public static String toFirstCapital(String name) {
        if (isNull(name)) {
            return null;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
