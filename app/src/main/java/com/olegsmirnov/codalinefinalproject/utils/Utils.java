package com.olegsmirnov.codalinefinalproject.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class Utils {

    public static final LatLng DEFAULT_COORDS = new LatLng(49.45, 32.06);

    public static void showLongSnack(String text, View view) {
        Snackbar snack = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        View mView = snack.getView();
        TextView tv = (TextView) mView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextSize(18);
        snack.show();
    }

    public static void showShortSnack(String text, View view) {
        Snackbar snack = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
        View mView = snack.getView();
        TextView tv = (TextView) mView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextSize(18);
        snack.show();
    }

    public static void changeSettings(SharedPreferences sharedPreferences, String option, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(option, value);
        editor.apply();
    }

    public static String detectWindDirection(int degree) {
        if (degree > 338 || degree <= 22) {
            return "↑";
        }
        else if (degree > 22 && degree <= 68) {
            return "↗";
        }
        else if (degree > 68 && degree <= 112) {
            return "→";
        }
        else if (degree > 112 && degree <= 158) {
            return "↘";
        }
        else if (degree > 158 && degree <= 202) {
            return "↓";
        }
        else if (degree > 202 && degree <= 248) {
            return "↙";
        }
        else if (degree > 248 && degree <= 292) {
            return "←";
        }
        else if (degree > 292 && degree <= 338) {
            return "↖";
        }
        return "";
    }

    public static boolean areProvidersEnabled(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static String detectUnitsPostfix(String units) {
        switch (units) {
            case "metric":
                return "°C";
            case "imperial":
                return "°F";
            default:
                return "°K";
        }
    }

}
