package com.olegsmirnov.codalinefinalproject.api;

import com.olegsmirnov.codalinefinalproject.entities.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("daily?&cnt=14&appid=1d0625288d8deb30b4d2ce607191b3bb")
    Call<WeatherData> forecast(@Query("units") String units, @Query("lat") double latitude, @Query("lon") double longitude);

}
