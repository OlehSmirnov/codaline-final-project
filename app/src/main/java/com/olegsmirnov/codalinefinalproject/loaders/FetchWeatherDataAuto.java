package com.olegsmirnov.codalinefinalproject.loaders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.olegsmirnov.codalinefinalproject.api.ApiClient;
import com.olegsmirnov.codalinefinalproject.fragments.MainFragment;
import com.olegsmirnov.codalinefinalproject.R;
import com.olegsmirnov.codalinefinalproject.entities.WeatherData;
import com.olegsmirnov.codalinefinalproject.fragments.MapsFragment;
import com.olegsmirnov.codalinefinalproject.utils.Utils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Response;

public class FetchWeatherDataAuto extends AsyncTask<Void, Void, Void> {

    private String imageURL = "http://openweathermap.org/img/w/";
    private String temp;
    private String date;
    private String units;

    private WeakReference<Activity> weakActivity;

    private ProgressDialog progressDialog;

    private boolean isTimeFounded = false;

    private Location location;



    public FetchWeatherDataAuto(Activity activity, Location location, String units) {
        this.weakActivity = new WeakReference<>(activity);
        this.location = location;
        this.units = units;
    }

    @Override
    public void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(weakActivity.get());
        progressDialog.setMessage("Loading, please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public Void doInBackground(Void... params) {
        try {
            Response<WeatherData> response = ApiClient.getInstance().forecast(units, location.getLatitude(), location.getLongitude()).execute();
            if (response.isSuccessful()) {
                MainFragment.forecastList = null;
                MainFragment.forecastList = new ArrayList<>(); // to refresh list in recycler view
                MainFragment.city.setName(response.body().getCity().getName());
                for (WeatherData.WeatherList list : response.body().getWeather()) {
                    MainFragment.forecastList.add(list);
                    if (list.getWindSpeed() < 8 && !list.getWeather().get(0).getDescription().contains("rain")) {
                        if (!isTimeFounded) {
                            isTimeFounded = true;
                            date = new Date(list.getDate() * 1000).toString();
                            imageURL += list.getWeather().get(0).getIcon();
                            temp = String.valueOf(list.getTemperature().getTempDay()) + Utils.detectUnitsPostfix(units);
                        }
                    }
                }
            }
            else {
                Toast.makeText(weakActivity.get(), "Server is not responding, please try again later", Toast.LENGTH_SHORT).show();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPostExecute(Void param) {
        Activity activity = weakActivity.get();
        ImageView ivClouds = (ImageView) activity.findViewById(R.id.iv_clouds);
        TextView tvTemp = (TextView) activity.findViewById(R.id.tv_temp);
        TextView tvBestTime = (TextView) activity.findViewById(R.id.tv_best_time);
        if (!isTimeFounded) {
            tvBestTime.setText("There is no good time to wash auto in " + MainFragment.city.getName() + " :(");
        }
        else {
            Glide.with(activity).load(imageURL + ".png").into(ivClouds);
            tvTemp.setText(temp);
            tvBestTime.setText("Time to wash auto in " + MainFragment.city.getName() + " \n " +
                    date.substring(0, 4) + date.substring(8, 10));
        }
        progressDialog.dismiss();
   }
}
