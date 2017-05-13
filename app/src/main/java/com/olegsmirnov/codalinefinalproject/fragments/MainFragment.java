package com.olegsmirnov.codalinefinalproject.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;
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

import com.olegsmirnov.codalinefinalproject.R;
import com.olegsmirnov.codalinefinalproject.entities.WeatherData;
import com.olegsmirnov.codalinefinalproject.utils.Utils;
import com.olegsmirnov.codalinefinalproject.activities.ForecastActivity;
import com.olegsmirnov.codalinefinalproject.entities.WeatherData.WeatherList;
import com.olegsmirnov.codalinefinalproject.services.MyReceiverService;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainFragment extends Fragment {

    private Context context;

    private boolean isNotificationAlive;
    private SharedPreferences sharedPreferences;

    public static ArrayList<WeatherList> forecastList = new ArrayList<>();
    public static WeatherData.City city = new WeatherData.City();

    @BindView(R.id.tv_best_time)
    TextView tvBestTime;

    @BindView(R.id.iv_clouds)
    ImageView ivClouds;

    @BindView(R.id.tv_temp)
    TextView tvTemp;

    @BindView(R.id.button_details)
    Button buttonDetails;

    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;

    @BindView(R.id.fab_cancel)
    FloatingActionButton fabCancel;

    @OnClick(R.id.fab_add)
    public void addNotification() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent service = new Intent(context, MyReceiverService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, service, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10); // 10 secs delay for debug
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Utils.changeSettings(sharedPreferences, getString(R.string.is_notification_alive), true);
        Utils.showShortSnack(getString(R.string.snack_text_add), getView());
        fabAdd.setVisibility(View.INVISIBLE);
        fabCancel.setAnimation(AnimationUtils.loadAnimation(context, R.anim.show));
        fabCancel.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.fab_cancel)
    public void cancelNotification() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent service = new Intent(context, MyReceiverService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, service, 0);
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
        context.stopService(service);
        Utils.changeSettings(sharedPreferences, getString(R.string.is_notification_alive), false);
        Utils.showShortSnack(getString(R.string.snack_text_cancel), getView());
        fabCancel.setVisibility(View.INVISIBLE);
        fabAdd.setAnimation(AnimationUtils.loadAnimation(context, R.anim.show));
        fabAdd.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.button_details)
    public void showDetails() {
        Intent intent = new Intent(context, ForecastActivity.class);
        intent.putExtra("CITY", city.getName()); // City name
        intent.putParcelableArrayListExtra("LIST_AUTO", forecastList);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.do_nothing);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        setHasOptionsMenu(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        isNotificationAlive = sharedPreferences.getBoolean(getString(R.string.is_notification_alive), false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNotificationAlive) {
            fabAdd.setVisibility(View.INVISIBLE);
            fabCancel.setVisibility(View.VISIBLE);
        }
        if (sharedPreferences.getBoolean(getString(R.string.prefs_light_style), true)) {
            getView().setBackgroundResource(R.color.colorWhite);
            buttonDetails.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
            fabCancel.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorAccent));
            fabAdd.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorAccent));
            tvBestTime.setTextColor(ContextCompat.getColor(context, R.color.colorLightBlue));
        }
        else {
            getView().setBackgroundResource(R.color.colorGray);
            buttonDetails.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLightBlue));
            fabCancel.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorLightBlue));
            fabAdd.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorLightBlue));
            tvBestTime.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent service = new Intent(context, MyReceiverService.class);
        context.stopService(service);
    }

}