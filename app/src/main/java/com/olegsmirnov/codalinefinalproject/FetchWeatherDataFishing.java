package com.olegsmirnov.codalinefinalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import retrofit2.Response;

public class FetchWeatherDataFishing extends AsyncTask<LatLng, Void, Void> {

    private ArrayList<WeatherData.WeatherList> forecastList;

    private Context context;

    FetchWeatherDataFishing(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(LatLng... latLngs) {
        try {
            Response<WeatherData> response = ApiClient.getInstance().forecast(latLngs[0].latitude, latLngs[0].longitude).execute();
            if (response.isSuccessful()) {
                forecastList = new ArrayList<>();
                for (WeatherData.WeatherList list : response.body().getWeather()) {
                    forecastList.add(list);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(forecastList.size());
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Intent intent = new Intent(context, MainForecastActivity.class);
        intent.putParcelableArrayListExtra("LIST", forecastList);
        context.startActivity(intent);
    }
}
