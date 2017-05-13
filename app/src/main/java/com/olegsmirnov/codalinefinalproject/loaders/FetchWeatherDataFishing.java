package com.olegsmirnov.codalinefinalproject.loaders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.gms.maps.model.LatLng;
import com.olegsmirnov.codalinefinalproject.R;
import com.olegsmirnov.codalinefinalproject.api.ApiClient;
import com.olegsmirnov.codalinefinalproject.activities.ForecastActivity;
import com.olegsmirnov.codalinefinalproject.entities.WeatherData;

import java.io.IOException;
import java.util.ArrayList;
import retrofit2.Response;

public class FetchWeatherDataFishing extends AsyncTask<LatLng, Void, LatLng> {

    private ArrayList<WeatherData.WeatherList> forecastList;

    private Context context;

    private String location;
    private String units;

    public FetchWeatherDataFishing(Context context, String location, String units) {
        this.context = context;
        this.location = location;
        this.units = units;
    }

    @Override
    protected LatLng doInBackground(LatLng... latLngs) {
        try {
            Response<WeatherData> response = ApiClient.getInstance().forecast(units, latLngs[0].latitude, latLngs[0].longitude).execute();
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
    protected void onPostExecute(LatLng params) {
        super.onPostExecute(params);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, ForecastActivity.class);
                intent.putParcelableArrayListExtra("LIST_FISHING", forecastList);
                intent.putExtra("CITY", location);
                Activity activity = (Activity) context;
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.do_nothing);
            }
        }, 150); // delay to prevert google map becomes black (Google bug)
    }
}
