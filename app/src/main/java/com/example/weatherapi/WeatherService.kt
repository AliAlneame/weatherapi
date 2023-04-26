package com.example.weatherapi

import com.google.gson.Gson
import io.reactivex.rxjava3.core.Observable
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

class WeatherService {
    private val httpClient = OkHttpClient()
    private val gson = Gson()
    private val locationService = LocationService()

    fun getWeatherForCity(cityName: String): Observable<WeatherResponse> {
        return locationService.getCityCoordinates(cityName)
            .flatMap(::getWeatherData)
    }

    private fun getWeatherData(coordinates: Pair<Double, Double>): Observable<WeatherResponse> {
        val url = HttpUrl.Builder().scheme("https").host("api.tomorrow.io")
            .addPathSegments("v4/timelines")
            .addQueryParameter("location", "${coordinates.first},${coordinates.second}")
            .addQueryParameter("fields", "temperature")
            .addQueryParameter("timesteps", "1d")
            .addQueryParameter("units", "metric")
            .addQueryParameter("apikey", "YOUR_API_KEY")
            .build()

        val request = Request.Builder().url(url).build()

        return Observable.fromCallable {
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let { gson.fromJson(it, WeatherResponse::class.java) }
                    ?: throw Exception("Response body is null")
            } else {
                throw Exception("Request failed with status code: ${response.code}")
            }
        }
    }
}