package com.olegsmirnov.codalinefinalproject;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.olegsmirnov.codalinefinalproject.WeatherData.WeatherList;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainFragment extends Fragment implements LocationListener {

    @BindView(R.id.tv_best_time)
    TextView tvBestTime;

    @BindView(R.id.iv_clouds)
    ImageView ivClouds;

    @BindView(R.id.tv_temp)
    TextView tvTemp;

    @BindView(R.id.button_details)
    Button buttonDetails;

    @BindView(R.id.floatingActionButtonAdd)
    FloatingActionButton fabAdd;

    @BindView(R.id.floatingActionButtonCancel)
    FloatingActionButton fabCancel;

    @OnClick(R.id.floatingActionButtonAdd)
    public void addNotification() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent service = new Intent(context, MyReceiverService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, service, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Utils.changeSettings(sharedPreferences, getString(R.string.is_notification_alive), true);
        Utils.showSnack(getString(R.string.snack_text_add), getView());
        fabCancel.startAnimation(AnimationUtils.loadAnimation(context, R.anim.animation_show));
        fabCancel.setVisibility(View.VISIBLE);

        fabAdd.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.floatingActionButtonCancel)
    public void cancelNotification() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent service = new Intent(context, MyReceiverService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, service, 0);
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
        context.stopService(service);
        Utils.changeSettings(sharedPreferences, getString(R.string.is_notification_alive), false);
        Utils.showSnack(getString(R.string.snack_text_cancel), getView());
        fabCancel.setVisibility(View.INVISIBLE);
        fabAdd.startAnimation(AnimationUtils.loadAnimation(context, R.anim.animation_show));
        fabAdd.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.button_details)
    public void showDetails() {
        String[] splitedString = tvBestTime.getText().toString().split(" ");
        Intent intent = new Intent(context, ForecastActivity.class);
        intent.putExtra("CITY", splitedString[5]); // City name
        intent.putParcelableArrayListExtra("LIST", forecastList);
        startActivity(intent);
    }

    private Context context;

    private LocationManager locationManager;

    private boolean isNotificationAlive;
    private SharedPreferences sharedPreferences;

    private Location location;
    private static double latitude;
    private static double longitude;
    public static ArrayList<WeatherList> forecastList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        context = getActivity().getApplicationContext();
        setHasOptionsMenu(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        isNotificationAlive = sharedPreferences.getBoolean(getString(R.string.is_notification_alive), true);
        setupLocationManager();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNotificationAlive) {
            fabAdd.setVisibility(View.INVISIBLE);
            fabCancel.setVisibility(View.VISIBLE);
        }
        if (sharedPreferences.getBoolean(getString(R.string.prefs_light_style), true)) {
            buttonDetails.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
            fabCancel.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent)));
            fabAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent)));
            tvBestTime.setTextColor(ContextCompat.getColor(context, R.color.colorLightBlue));
        }
        else {
            buttonDetails.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLightBlue));
            fabCancel.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorLightBlue)));
            fabAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorLightBlue)));
            tvBestTime.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
            else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
            else if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent service = new Intent(context, MyReceiverService.class);
        context.stopService(service);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupLocationManager();
            }
            else {
                Utils.showSnack(getString(R.string.request_user_permission), getView());
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        setupLocationManager();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public static double getLatitude() {
        return latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    private void setupLocationManager() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        try {
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                Toast.makeText(context, R.string.gps_error_message, Toast.LENGTH_SHORT).show();
            }
            else {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (location != null) {
                    new FetchWeatherDataAuto(getActivity()).execute();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}