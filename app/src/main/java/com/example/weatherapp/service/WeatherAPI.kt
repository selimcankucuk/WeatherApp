package com.example.weatherapp.service

import com.example.weatherapp.model.WeatherModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

//https://api.openweathermap.org/data/2.5/weather?q=istanbul&appid=4e6f78e046a746adca74c41d063b19c1
interface WeatherAPI {
    @GET("data/2.5/weather?&units=metric&appid=4e6f78e046a746adca74c41d063b19c1")

    fun getData(
        @Query("q") cityName:String
    ):Single<WeatherModel>
}