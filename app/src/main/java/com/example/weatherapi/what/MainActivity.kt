package com.example.weatherapi.what

import WeatherService
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapi.WeatherAdapter
import com.example.weatherapi.WeatherItem
import com.example.weatherapi.WeatherResponse
import com.example.weatherapi.databinding.ActivityMainBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val searchTextSubject: PublishSubject<String> = PublishSubject.create()
    private val TAG = "whatever"

    private lateinit var binding: ActivityMainBinding
    private lateinit var autoCompleteAdapter: ArrayAdapter<String>
    private val weatherService = WeatherService()
    private val disposables = CompositeDisposable()
    private lateinit var weatherAdapter: WeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAutoComplete()

        binding.btnGetWeather.setOnClickListener {
            val cityName = binding.etCityName.text.toString()
            disposables.add(
                weatherService.getWeatherForCity(cityName)
                    .throttleFirst(3, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleWeatherResponse, this::handleError)
            )
        }
        weatherAdapter = WeatherAdapter(listOf())
        binding.rvWeatherList.layoutManager = LinearLayoutManager(this)
        binding.rvWeatherList.adapter = weatherAdapter
    }

    private fun setupAutoComplete() {
        autoCompleteAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        )
        binding.etCityName.setAdapter(autoCompleteAdapter)

        disposables.add(
            weatherService.getAllCountries()
                .debounce (2,TimeUnit.SECONDS)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ countries ->
                    autoCompleteAdapter.clear()
                    autoCompleteAdapter.addAll(countries)
                    autoCompleteAdapter.notifyDataSetChanged()
                }, this::handleError)
        )
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    private fun handleError(error: Throwable) {
        Log.e("OkHttpExample", "Error: ${error.message}")
    }

    private fun handleWeatherResponse(weatherResponse: WeatherResponse) {
        Log.d("OkHttpExample", "Weather data: $weatherResponse")

        val cityName = binding.etCityName.text.toString()
        val newWeatherItems = mutableListOf<WeatherItem>()

        for (interval in weatherResponse.data.timelines[0].intervals) {
            val weatherItem = WeatherItem(cityName, interval.startTime, interval.values)
            newWeatherItems.add(weatherItem)
        }

        weatherAdapter.weatherItems = newWeatherItems

        weatherAdapter.notifyDataSetChanged()
    }

    private fun setupTextWatcher() {
        binding.etCityName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                searchTextSubject.onNext(s.toString())

            }
        })
    }
}