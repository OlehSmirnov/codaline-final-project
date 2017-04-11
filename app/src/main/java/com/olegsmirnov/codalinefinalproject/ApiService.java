package com.olegsmirnov.codalinefinalproject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("daily?&cnt=14&units=metric&appid=1d0625288d8deb30b4d2ce607191b3bb")
    Call<WeatherData> forecast(@Query("lat") double latitude, @Query("lon") double longitude);

}
