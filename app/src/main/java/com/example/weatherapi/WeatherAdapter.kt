package com.example.weatherapi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapi.databinding.WeatherItemCardBinding

class WeatherAdapter(var weatherItems: List<WeatherItem>) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding =
            WeatherItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val currentItem = weatherItems[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int = weatherItems.size

    inner class WeatherViewHolder(private val binding: WeatherItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(weatherItem: WeatherItem) {
            binding.tvCityName.text = weatherItem.cityName
            binding.tvWeatherData.text = "Temperature: ${weatherItem.weatherData.temperature}°C"
            binding.tvDate.text = weatherItem.date
        }
    }

}
