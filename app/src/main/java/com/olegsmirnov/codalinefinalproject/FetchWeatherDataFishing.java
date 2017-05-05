package com.olegsmirnov.codalinefinalproject;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import retrofit2.Response;

public class FetchWeatherDataFishing extends AsyncTask<LatLng, Void, LatLng> {

    private ArrayList<WeatherData.WeatherList> forecastList;

    private Context context;

    FetchWeatherDataFishing(Context context) {
        this.context = context;
    }

    @Override
    protected LatLng doInBackground(LatLng... latLngs) {
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
        return latLngs[0];
    }

    @Override
    protected void onPostExecute(LatLng aVoid) {
        super.onPostExecute(aVoid);
        Geocoder geocoder = new Geocoder(context);
        try {
            Toast.makeText(context, geocoder.getFromLocation(aVoid.latitude, aVoid.longitude, 1).get(0).getAddressLine(1), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(context, ForecastActivity.class);
        intent.putParcelableArrayListExtra("LIST", forecastList);
        context.startActivity(intent);
    }
}
