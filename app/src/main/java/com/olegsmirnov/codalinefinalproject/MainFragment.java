package com.olegsmirnov.codalinefinalproject;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.IS_NOTIFICATION_ACTIVE, true);
        editor.apply();
        showSnack(getString(R.string.snack_text_add));
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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.IS_NOTIFICATION_ACTIVE, false);
        editor.apply();
        showSnack(getString(R.string.snack_text_cancel));
        fabCancel.setVisibility(View.INVISIBLE);
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

    private SharedPreferences sharedPreferences;

    private LocationManager locationManager;

    private Location location;
    private static double latitude;
    private static double longitude;
    public static ArrayList<WeatherList> forecastList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        setHasOptionsMenu(true);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        sharedPreferences = context.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
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
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sharedPreferences.getBoolean(Constants.IS_NOTIFICATION_ACTIVE, false)) {
            fabAdd.setVisibility(View.INVISIBLE);
            fabCancel.setVisibility(View.VISIBLE);
        }
        if (locationManager != null) {
            try {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                } else {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                }
            } catch (SecurityException e) {
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
                showSnack(getString(R.string.request_user_permission));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                showSnack("Share app with friends (not really)");
                return true;
            case R.id.settings:
                Toast.makeText(context, "Settings Blank", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                if (location == null) {
                    Toast.makeText(context, R.string.location_error_message, Toast.LENGTH_SHORT).show();
                }
                else {
                    new FetchWeatherDataAuto(getActivity()).execute();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void showSnack(String text) {
        Snackbar snack = Snackbar.make(getView(), text, Snackbar.LENGTH_SHORT);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextSize(18);
        snack.show();
    }

}