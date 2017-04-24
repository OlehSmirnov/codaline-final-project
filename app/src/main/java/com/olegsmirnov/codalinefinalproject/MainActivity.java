package com.olegsmirnov.codalinefinalproject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.olegsmirnov.codalinefinalproject.WeatherData.WeatherList;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LocationListener {

    @BindView(R.id.tv_best_time)
    TextView tvBestTime;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.iv_clouds)
    ImageView ivClouds;

    @BindView(R.id.tv_temp)
    TextView tvTemp;

    @BindView(R.id.button_details)
    Button buttonDetails;

    @BindView(R.id.floatingActionButton)
    FloatingActionButton fab;

    @OnClick(R.id.floatingActionButton)
    public void addNotification() {
        Toast.makeText(this, "Button for add notifications (blank)", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.button_details)
    public void showDetails() {
        Intent intent = new Intent(this, MainForecastActivity.class);
        intent.putParcelableArrayListExtra("LIST", forecastList);
        startActivity(intent);
    }

    ArrayList<WeatherList> forecastList;

    private ProgressDialog progressDialog;

    private LocationManager locationManager;
    private double latitude;
    private double longitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
            else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                onLocationChanged(location);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
            if (location != null) {
                onLocationChanged(location);
            }
        }
        catch (SecurityException e) {
            Toast.makeText(this, "Please, allow to acces your location", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading, please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new AsyncTask<Void, Void, Date>() {
            String imageURL = "http://openweathermap.org/img/w/";
            String temp = "";
            @Override
            protected Date doInBackground(Void... params) {
                Date date = null;
                try {
                    Response<WeatherData> response = ApiClient.getInstance().forecast(latitude, longitude).execute();
                    if (response.isSuccessful()) {
                        forecastList = new ArrayList<>();
                        for (WeatherList list : response.body().getWeather()) {
                            forecastList.add(list);
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
                        Toast.makeText(MainActivity.this, "Server is not responding, please try later", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return date;
            }

            @Override
            protected void onPostExecute(Date date) {
                Glide.with(getApplicationContext()).load(imageURL + ".png").into(ivClouds);
                progressDialog.dismiss();
                tvTemp.setText(temp);
                tvBestTime.setText("Closest good time is\n" + date.toString().substring(0, 10));
            }
        }.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
            else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
        }
        catch (SecurityException e) {}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notifications:
                Toast.makeText(this, "Notifications Blank", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.share:
                Toast.makeText(this, "Share Blank", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.settings:
                Toast.makeText(this, "Settings Blank", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (list != null && list.size() != 0) {
                Toast.makeText(this, "Your location is " + list.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

}