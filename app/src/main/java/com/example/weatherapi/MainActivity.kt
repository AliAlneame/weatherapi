package com.example.weatherapi

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapi.databinding.ActivityMainBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val weatherService = WeatherService()
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetWeather.setOnClickListener {
            val cityName = binding.etCityName.text.toString()
            disposables.add(
                weatherService.getWeatherForCity(cityName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleWeatherResponse, this::handleError)
            )
        }
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    private fun handleError(error: Throwable) {
        Log.e("OkHttpExample", "Error: ${error.message}")
    }

    private fun handleWeatherResponse(weatherResponse: WeatherResponse) {
        binding.tvWeatherData.text = "Weather data: $weatherResponse"
    }
}
