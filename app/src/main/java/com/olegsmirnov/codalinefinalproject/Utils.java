package com.olegsmirnov.codalinefinalproject;

import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class Utils {
    public static void showSnack(String text, View view) {
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

}
