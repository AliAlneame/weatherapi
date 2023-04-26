package com.example.weatherapi

import io.reactivex.rxjava3.core.Observable
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

class LocationService {
    private val httpClient = OkHttpClient()

    fun getCityCoordinates(cityName: String): Observable<Pair<Double, Double>> {
        return Observable.fromCallable {
            val url = HttpUrl.Builder().scheme("https").host("nominatim.openstreetmap.org")
                .addPathSegment("search")
                .addQueryParameter("q", cityName)
                .addQueryParameter("format", "json")
                .addQueryParameter("limit", "1")
                .build()

            val request = Request.Builder().url(url).build()
            val response = httpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let { extractCoordinatesFromJson(it) }
                    ?: throw Exception("Response body is null")
            } else {
                throw Exception("Request failed with status code: ${response.code}")
            }
        }
    }

    private fun extractCoordinatesFromJson(json: String): Pair<Double, Double> {
        val jsonArray = JSONArray(json)
        val jsonObject = jsonArray.getJSONObject(0)
        val lat = jsonObject.getDouble("lat")
        val lon = jsonObject.getDouble("lon")
        return Pair(lat, lon)
    }
}
