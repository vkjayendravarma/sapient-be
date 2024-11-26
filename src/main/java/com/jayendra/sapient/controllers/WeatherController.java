package com.jayendra.sapient.controllers;

import com.jayendra.sapient.services.WeatherService;
import com.jayendra.sapient.utils.HttpResponseStructs;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }


    @GetMapping("/forecast")
    public Object getForecast(@RequestParam String city) {
        Object data = null;
        try {
            data = weatherService.getForecast(city);
            return HttpResponseStructs.generateJsonResponse(data, 200);
        } catch (BadRequestException e) {
            return HttpResponseStructs.generateJsonResponse(e.getMessage(), 404);
        } catch (Exception e) {
            return HttpResponseStructs.generateJsonResponse(e.getMessage(), 500);
        }

    }
}
