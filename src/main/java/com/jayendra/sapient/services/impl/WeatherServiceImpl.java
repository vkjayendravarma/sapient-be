package com.jayendra.sapient.services.impl;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayendra.sapient.domain.dto.WeatherReportPrediction;
import com.jayendra.sapient.services.WeatherService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class WeatherServiceImpl implements WeatherService {
    private RedisService redisService;

    public WeatherServiceImpl(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public Object getForecast(String city) throws Exception {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


        LocalDate dateNow = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String redisCacheKey = "weather-" + city + dateNow.format(formatter);

        var cachedRequest = redisService.getKey(redisCacheKey);
        if (cachedRequest != null){
            log.info("cache retrived");
            return objectMapper.readTree(String.valueOf(cachedRequest));
        }
        log.info("no cache");
        // Construct the URL with query parameters
        HttpUrl url = HttpUrl.parse("https://api.openweathermap.org/data/2.5/forecast").newBuilder()
                .addQueryParameter("q", city)         // City name
                .addQueryParameter("appid", "d2929e9483efc82c82c32ee7e02d563e") // API key
                .addQueryParameter("cnt", "24")
                .addQueryParameter("units", "metric")// Number of results
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();


        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;

                JsonNode json = objectMapper.readTree(response.body().string());

                List<WeatherReportPrediction> predictions = new ArrayList<>();

                LocalDateTime dateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(Long.parseLong(String.valueOf(json.get("list").get(0).get("dt")))),
                        ZoneId.systemDefault()
                );

                int currentDate = -1;
                int counter = -1;

                for (int index = 0; index <= 23; index++) {
                    var currentObject = json.get("list").get(index);
                    LocalDateTime currentObjectDateTime = LocalDateTime.ofInstant(
                            Instant.ofEpochSecond(Long.parseLong(String.valueOf(currentObject.get("dt")))),
                            ZoneId.systemDefault()
                    );
                    int currentObjectDate = currentObjectDateTime.getDayOfMonth();
                    if (currentObjectDate != currentDate) {
                        counter++;

                        if (counter >= 3) break;

                        WeatherReportPrediction prediction = new WeatherReportPrediction();
                        prediction.setDate(Integer.parseInt(String.valueOf(currentObject.get("dt"))));
                        predictions.add(prediction);
                        currentDate = currentObjectDate;
                    }
                    WeatherReportPrediction currentPrediction = predictions.get(counter);

                    // min set
                    Float min = Float.parseFloat(String.valueOf(currentObject.get("main").get("temp_min")));
                    if (currentPrediction.getMin() == null || currentPrediction.getMin() > min)
                        currentPrediction.setMin(min);

//                    max set
                    Float max = Float.parseFloat(String.valueOf(currentObject.get("main").get("temp_max")));
                    if (currentPrediction.getMax() == null || currentPrediction.getMax() < max)
                        currentPrediction.setMax(max);

                    // sunny
                    if (max >= 40) {
                        currentPrediction.setSunny(true);
                    }

                    // rain
                    if (Objects.equals(String.valueOf(currentObject.get("weather").get(0).get("main")), "Rain")) {
                        currentPrediction.setRainy(true);
                    }
                    // rain
                    if (Objects.equals(String.valueOf(currentObject.get("weather").get(0).get("main")), "Thunderstorm")) {
                        currentPrediction.setStorm(true);
                    }

                    // rain
                    if (Float.parseFloat(String.valueOf(currentObject.get("wind").get("speed"))) > 2.77778) {
                        currentPrediction.setWindy(true);
                    }

                    predictions.set(counter, currentPrediction);

                }



                redisService.setKeyWithTTL(redisCacheKey, objectMapper.writeValueAsString(predictions), 60*60*3);

                return predictions;

            } else {
                if (response.code() == 404) {
                    throw new BadRequestException("city not found");
                }
                throw new Exception("Internal Error");
            }
        }
    }

}
