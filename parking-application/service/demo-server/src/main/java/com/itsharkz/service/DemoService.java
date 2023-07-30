package com.itsharkz.service;

import com.itsharkz.data.PoitiersParking;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Service
public class DemoService {
    private final static Logger LOG = LoggerFactory.getLogger(DemoService.class);
    private final static String FIELDS_JSONPATH = "$.records[*].fields";
    private final static String RECORDS_READ = "{} records read";
    private final static String RECORDS_FILTERED_JSONPATH =
        "$.records[?(@.fields.xlong >= %f12 && @.fields.xlong <= %f12 && @.fields.ylat >= %f12 && @.fields.ylat <= %f12)]";
    private final static String RECORDS_REALTIME_FILTERED_JSONPATH = "$.records[?(@.fields.nom == '%s')]";

    private final static String FIELDS = "fields";
    private final static String NAME = "nom";
    private final static String INFO = "info";
    private final static String LATTITUDE = "ylat";
    private final static String LONGITUDE = "xlong";
    private final static String CAPACITY = "capacite";
    private final static String FREE = "places";

    public List<PoitiersParking> getPoitiersParkings(Double lattitude, Double longitude,
                                                     Double length, Double proportion) {
        final var parkingString = getResourceFileAsString("data/poitiers_parkings.json");
        final var parkingRealtimeString = getResourceFileAsString("data/poitiers_parkings_realtime.json");
        DocumentContext parkingContext = JsonPath.parse(parkingString);
        DocumentContext parkingRealtimeContext = JsonPath.parse(parkingRealtimeString);
        List<Map<String, Object>> parkingList = parkingContext.read(FIELDS_JSONPATH);
        List<Map<String, Object>> parkingRealtimeList = parkingRealtimeContext.read(FIELDS_JSONPATH);

        final var parkings = filterParking(createPotiersParkingList(parkingList, parkingRealtimeList),
            lattitude, longitude, length, proportion);
        LOG.info(RECORDS_READ, parkings.size());
        return parkings;
    }

    public String getMontpellierParkings(Double latMin, Double latMax,
                                         Double longMin, Double longMax) {
        final var parkingString = getResourceFileAsString("data/poitiers_parkings.json");
        final var parkingRealtimeString = getResourceFileAsString("data/poitiers_parkings_realtime.json");
        final Map<String, Object> jsonMap = JsonPath.parse(parkingString).read("$");
        final var recordPath = format(RECORDS_FILTERED_JSONPATH, longMin, longMax, latMin, latMax);
        JSONArray records = JsonPath.parse(parkingString).read(recordPath);
        records
            .forEach(record -> appendRealtimeParams((Map<String, Object>) record, parkingRealtimeString));
        jsonMap.put("nhits", records.size());
        jsonMap.put("records", records);

        LOG.info(RECORDS_READ, records.size());
        return JSONObject.toJSONString(jsonMap);
    }

    private void appendRealtimeParams(Map<String, Object> record, String parkingRealtimeString) {
        final var fields = (Map<String, Object>) record.get(FIELDS);
        final var name = ((Map<?, ?>) record.get(FIELDS)).getOrDefault(NAME, null);
        final var jsonPath = format(RECORDS_REALTIME_FILTERED_JSONPATH, name);
        final var realtimeRecord = (JSONArray) JsonPath.parse(parkingRealtimeString).read(jsonPath);
        var capacity = -1.0;
        var free = -1.0;
        if (!realtimeRecord.isEmpty()) {
            final var flds = (Map<?, ?>) ((Map<?, ?>) realtimeRecord.get(0)).get(FIELDS);
            capacity = (double) flds.get(CAPACITY);
            free = (double) flds.get(FREE);
        }
        fields.put(CAPACITY, capacity);
        fields.put(FREE, free);
    }

    private List<PoitiersParking> createPotiersParkingList(List<Map<String, Object>> parkingList,
                                                           List<Map<String, Object>> parkingRealtimeList) {
        return parkingList
            .stream()
            .map(fields -> createParkingRecord(fields, parkingRealtimeList))
            .toList();
    }

    private PoitiersParking createParkingRecord(Map<String, Object> fields,
                                                List<Map<String, Object>> parkingRealtimeList) {
        final var name = (String) fields.get(NAME);
        final var info = (String) fields.get(INFO);
        final var ylat = (Double) fields.get(LATTITUDE);
        final var xlong = (Double) fields.get(LONGITUDE);
        final var realtimeFields = parkingRealtimeList
            .stream()
            .filter(flds -> name.equals(flds.get(NAME)))
            .findFirst()
            .orElse(Collections.emptyMap());
        final var capacity = realtimeFields.isEmpty() ? -1 : ((Double) realtimeFields.get(CAPACITY)).intValue();
        final var free = realtimeFields.isEmpty() ? -1 : ((Double) realtimeFields.get(FREE)).intValue();

        return new PoitiersParking(name, info, ylat, xlong, capacity, free);
    }

    private List<PoitiersParking> filterParking(List<PoitiersParking> parkings,
                                                Double lattitude, Double longitude,
                                                Double length, Double proportion) {
        final var height = length/proportion;
        final var latMin = lattitude - height/2;
        final var latMax = lattitude + height/2;
        final var lonMin = longitude - length/2;
        final var lonMax = longitude + length/2;

        return parkings
            .stream()
            .filter(parking -> latMin < parking.getLattitude() && parking.getLattitude() < latMax
                && lonMin < parking.getLongitude() && parking.getLongitude() < lonMax)
            .toList();
    }

    private String getResourceFileAsString(String fileName) {
        try {
            File file = ResourceUtils.getFile("classpath:" + fileName);
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            return null;
        }
    }
}
