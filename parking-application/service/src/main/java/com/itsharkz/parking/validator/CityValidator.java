package com.itsharkz.parking.validator;

import com.itsharkz.parking.entities.CitiesProperties;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class CityValidator implements ConstraintValidator<CityConstraint, String> {
    private final CitiesProperties citiesUrlProperties;

    public CityValidator(CitiesProperties citiesUrlProperties) {
        this.citiesUrlProperties = citiesUrlProperties;
    }

    @Override public void initialize(CityConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override public boolean isValid(String cityName, ConstraintValidatorContext constraintValidatorContext) {
        return citiesUrlProperties.getCities()
            .stream()
            .anyMatch(city -> city.getName().toLowerCase().equals(cityName.toLowerCase()));
    }
}
