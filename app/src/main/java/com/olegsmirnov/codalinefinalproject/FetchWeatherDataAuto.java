package com.olegsmirnov.codalinefinalproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Response;

public class FetchWeatherDataAuto extends AsyncTask<Void, Void, Void> {

    private String imageURL = "http://openweathermap.org/img/w/";
    private String temp = "";

    private WeakReference<Activity> weakActivity;

    private Date date;

    private ProgressDialog progressDialog;

    public FetchWeatherDataAuto(Activity activity) {
        this.weakActivity = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(weakActivity.get());
        progressDialog.setMessage("Loading, please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Response<WeatherData> response = ApiClient.getInstance().forecast(MainFragment.getLatitude(), MainFragment.getLongitude()).execute();
            if (response.isSuccessful()) {
                MainFragment.forecastList = new ArrayList<>();
                for (WeatherData.WeatherList list : response.body().getWeather()) {
                    MainFragment.forecastList.add(list);
                    if (list.getWindSpeed() < 8 && !list.getWeather().get(0).getDescription().contains("rain")) {
                        if (date == null) {
                            date = new Date(list.getDate() * 1000);
                            imageURL += list.getWeather().get(0).getIcon();
                            temp = String.valueOf(list.getTemperature().getTempDay()) + "°C";
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
    protected void onPostExecute(Void param) {
        Activity activity = weakActivity.get();
        ImageView ivClouds = (ImageView) activity.findViewById(R.id.iv_clouds);
        TextView tvTemp = (TextView) activity.findViewById(R.id.tv_temp);
        TextView tvBestTime = (TextView) activity.findViewById(R.id.tv_best_time);
        Glide.with(activity).load(imageURL + ".png").into(ivClouds);
        tvTemp.setText(temp);
        tvBestTime.setText("Closest good time is\n" + date.toString().substring(0, 10));
        progressDialog.dismiss();
    }
}
