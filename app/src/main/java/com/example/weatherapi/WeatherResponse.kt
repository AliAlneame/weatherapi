package com.example.weatherapi

data class WeatherResponse(
    val data: WeatherData
)

data class WeatherData(
    val timelines: List<Timeline>
)

data class Timeline(
    val timesteps: String,
    val startTime: String,
    val endTime: String,
    val intervals: List<Interval>
)

data class Interval(
    val startTime: String,
    val endTime: String,
    val values: WeatherValues
)

data class WeatherValues(
    val temperature: Double
)
data class WeatherItem(
    val cityName: String,
    val date: String,
    val weatherData: WeatherValues
)