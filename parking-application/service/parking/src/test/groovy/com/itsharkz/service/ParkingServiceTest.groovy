package com.itsharkz.service

import com.itsharkz.properties.input.CityProperties
import com.itsharkz.properties.input.DefaultProperties
import com.itsharkz.properties.input.FieldProperties
import com.itsharkz.properties.input.ParkingProperties
import spock.lang.Specification

import static com.itsharkz.enums.AutorizationTypeEnum.BEARER
import static com.itsharkz.enums.AutorizationTypeEnum.NO_AUTH
import static com.itsharkz.enums.MethodEnum.GET
import static com.itsharkz.enums.MethodEnum.POST
import static com.itsharkz.enums.ParamsTypeEnum.PARAMS
import static com.itsharkz.enums.ParamsTypeEnum.PATH
import static com.itsharkz.enums.ReturnFieldsEnum.CAPACITY
import static com.itsharkz.enums.ReturnFieldsEnum.FREE_PLACES
import static com.itsharkz.enums.ReturnFieldsEnum.INFO
import static com.itsharkz.enums.ReturnFieldsEnum.LATTITUDE
import static com.itsharkz.enums.ReturnFieldsEnum.LONGITUDE
import static com.itsharkz.enums.ReturnFieldsEnum.NAME
import static com.itsharkz.enums.TypeEnum.DOUBLE
import static com.itsharkz.enums.TypeEnum.INTEGER
import static com.itsharkz.enums.TypeEnum.STRING

class ParkingServiceTest extends Specification {
    def parkingProperties
    def scaleService

    void setup() {
        def defaultProperties = new DefaultProperties(2.75, 0.00205, 0.0205)
        def cityProperties0 = new CityProperties("poitiers", "http://localhost:8090/demo/poitiers",
                GET, NO_AUTH, null, PARAMS,
                Map.of(
                        "lattitudeMin", "lat-min", "lattitudeMax", "lat-max",
                        "longitudeMin", "lon-min", "longitudeMax", "lon-max"),
                "\$.[*]",
                Map.of(
                        NAME, new FieldProperties("name", STRING),
                        INFO, new FieldProperties("info", STRING),
                        LATTITUDE, new FieldProperties("lattitude", DOUBLE),
                        LONGITUDE, new FieldProperties("longitude", DOUBLE),
                        CAPACITY, new FieldProperties("capacity", INTEGER),
                        FREE_PLACES, new FieldProperties("free", INTEGER)))
        def cityProperties1 = new CityProperties("montpellier", "http://localhost:8090/demo/montpellier/",
                POST, BEARER, List.of("lattitudeMin", "lattitudeMax", "longitudeMin", "longitudeMax"), PATH,
                Map.of(
                        "lattitude", "lattitude", "longitude", "longitude",
                        "length", "length", "proportion", "proportion"),
                "\$.records[*].fields",
                Map.of(
                        NAME, new FieldProperties("nom", STRING),
                        INFO, new FieldProperties("info", STRING),
                        LATTITUDE, new FieldProperties("ylat", DOUBLE),
                        LONGITUDE, new FieldProperties("xlong", DOUBLE),
                        CAPACITY, new FieldProperties("capacite", DOUBLE),
                        FREE_PLACES, new FieldProperties("places", DOUBLE)))
        def list = List.of(cityProperties0, cityProperties1)
        parkingProperties = new ParkingProperties(defaultProperties, list)
        scaleService = new ParkingService(parkingProperties)
    }

    def "isCityAllowed Test"() {
        when:
        def answer = scaleService.isCityAllowed(cityName)

        then:
        answer == result

        where:
        cityName      | result
        "poitiers"    | true
        "montpellier" | true
        "toulouse"    | false
    }
}
