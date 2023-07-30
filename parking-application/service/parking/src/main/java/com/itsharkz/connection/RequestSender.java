package com.itsharkz.connection;

import com.itsharkz.exceptions.RemoteServerException;
import com.itsharkz.properties.input.CityProperties;
import com.itsharkz.properties.output.ParkingReturnProperties;
import com.jayway.jsonpath.JsonPath;
import lombok.Getter;
import net.minidev.json.JSONArray;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.itsharkz.enums.AutorizationTypeEnum.BEARER;
import static com.itsharkz.enums.ParamsTypeEnum.PATH;
import static com.itsharkz.enums.ReturnFieldsEnum.CAPACITY;
import static com.itsharkz.enums.ReturnFieldsEnum.FREE_PLACES;
import static com.itsharkz.enums.ReturnFieldsEnum.INFO;
import static com.itsharkz.enums.ReturnFieldsEnum.LATTITUDE;
import static com.itsharkz.enums.ReturnFieldsEnum.LONGITUDE;
import static com.itsharkz.enums.ReturnFieldsEnum.NAME;
import static com.itsharkz.enums.TypeEnum.DOUBLE;
import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Getter
public class RequestSender {
    private final static Logger LOG = LoggerFactory.getLogger(RequestSender.class);
    private final static Integer GLOBAL_TIMEOUT = 5000;
    private final static String SENT_MESSAGE = "Request sent: \n{}";
    private final CityProperties cityProperties;
    private final String autorization;
    private final Double lattitude;
    private final Double longitude;
    private final Double length;
    private final Double proportion;
    private final Double lattitudeMin;
    private final Double lattitudeMax;
    private final Double longitudeMin;
    private final Double longitudeMax;
    private final String fullUrl;
    private final Map<String, String> params;
    private final HttpRequestBase request;
    private String result;

    private RequestSender(CityProperties cityProperties, String autorization, final Double lattitude,
                          final Double longitude,
                          final Double length, final Double proportion) throws IOException {
        this.cityProperties = cityProperties;
        this.autorization = autorization;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.length = length;
        this.proportion = proportion;

        this.longitudeMin = longitude - length / 2;
        this.longitudeMax = longitude + length / 2;
        final var height = length / proportion;
        this.lattitudeMin = lattitude - height / 2;
        this.lattitudeMax = lattitude + height / 2;

        this.params = cityProperties.getParams().entrySet()
            .stream()
            .filter(entry -> nonNull(entry.getValue()))
            .collect(
                toMap(Map.Entry::getValue, entry -> readParamValue(entry.getKey()), (a, b) -> b, LinkedHashMap::new));
        this.fullUrl = createUrlForParams();
        this.request = createRequest();
    }

    public static RequestSender create(CityProperties cityProperties, String autorization, final Double lattitude,
                                       final Double longitude,
                                       final Double length, final Double proportion) throws IOException {
        return new RequestSender(cityProperties, autorization, lattitude, longitude, length, proportion);
    }

    private String createUrlForParams() throws UnsupportedEncodingException {
        final var url = cityProperties.getUrl();
        if (cityProperties.getParamsType().equals(PATH)) {
            if (nonNull(cityProperties.getPathOrder())) {
                final var paramPart = cityProperties.getPathOrder()
                    .stream()
                    .map(this::readParamValue)
                    .collect(joining("/"));
                return url + (url.endsWith("/") ? "" : "/") + paramPart;
            }
            return url;
        } else {
            return url + getParamsString();
        }
    }

    private String readParamValue(String paramName) {
        return switch (paramName) {
            case "lattitude" -> lattitude.toString();
            case "longitude" -> longitude.toString();
            case "length" -> length.toString();
            case "proportion" -> proportion.toString();
            case "lattitudeMin" -> lattitudeMin.toString();
            case "lattitudeMax" -> lattitudeMax.toString();
            case "longitudeMin" -> longitudeMin.toString();
            case "longitudeMax" -> longitudeMax.toString();
            default -> null;
        };
    }

    public String getParamsString() {
        return params.entrySet()
            .stream()
            .map(entry -> encode(entry.getKey(), UTF_8) + "=" + URLEncoder.encode(entry.getValue(), UTF_8))
            .collect(joining("&", "?", ""));
    }

    @Override
    public String toString() {
        return format("{\"url\": \"%s\", \"method\": \"%s\", \"params\": %s}",
            fullUrl, cityProperties.getMethod(), params);
    }

    private HttpRequestBase createRequest() {
        final var request = switch (cityProperties.getMethod()) {
            case POST -> new HttpPost(fullUrl);
            case PUT -> new HttpPut(fullUrl);
            case HEAD -> new HttpHead(fullUrl);
            case DELETE -> new HttpDelete(fullUrl);
            default -> new HttpGet(fullUrl);
        };
        request.addHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        if (cityProperties.getAuthorizationType().equals(BEARER)) {
            request.addHeader(AUTHORIZATION, autorization);
        }
        return request;
    }

    private List<ParkingReturnProperties> extractParkingData() {
        final var parkingContext = JsonPath.parse(result);
        final JSONArray jsonArray = parkingContext.read(cityProperties.getJsonItem());
        return jsonArray
            .stream()
            .map(item -> (Map<String, Object>) item)
            .map(this::createReturnProperties)
            .toList();
    }

    private ParkingReturnProperties createReturnProperties(Map<String, Object> item) {
        final var itemsMap = cityProperties.getItems();
        final var name = (String) item.get(itemsMap.get(NAME).getName());
        final var info = (String) item.get(itemsMap.get(INFO).getName());
        final var lattutude = (Double) item.get(itemsMap.get(LATTITUDE).getName());
        final var longitude = (Double) item.get(itemsMap.get(LONGITUDE).getName());
        final var capacity = itemsMap.get(CAPACITY).getType().equals(DOUBLE)
            ? ((Double) item.get(itemsMap.get(CAPACITY).getName())).intValue()
            : (Integer) item.get(itemsMap.get(CAPACITY).getName());
        final var free = itemsMap.get(FREE_PLACES).getType().equals(DOUBLE)
            ? ((Double) item.get(itemsMap.get(FREE_PLACES).getName())).intValue()
            : (Integer) item.get(itemsMap.get(FREE_PLACES).getName());
        return new ParkingReturnProperties(name, info, lattutude, longitude, capacity, free);
    }

    public List<ParkingReturnProperties> send() throws IOException, RemoteServerException {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            final var response = httpClient.execute(request);
            LOG.info(SENT_MESSAGE, this);
            result = EntityUtils.toString(response.getEntity(), "UTF-8");
            if (response.getStatusLine().getStatusCode() == OK.value()) {
                LOG.info(result);
                return extractParkingData();
            } else {
                final var ex = HttpStatus.resolve(response.getStatusLine().getStatusCode());
                throw new RemoteServerException(ex.getReasonPhrase(), ex);
            }
        }
    }
}
