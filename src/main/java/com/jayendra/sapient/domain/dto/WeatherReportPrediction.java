package com.jayendra.sapient.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherReportPrediction {
    private boolean rainy;
    private boolean windy;
    private boolean sunny;
    private boolean storm;
    private Float max;
    private Float min;
    private int date;
}
