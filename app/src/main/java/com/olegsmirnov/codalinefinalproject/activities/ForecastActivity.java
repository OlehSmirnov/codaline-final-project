package com.olegsmirnov.codalinefinalproject.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.olegsmirnov.codalinefinalproject.R;
import com.olegsmirnov.codalinefinalproject.adapters.WeatherAdapterAuto;
import com.olegsmirnov.codalinefinalproject.adapters.WeatherAdapterFishing;
import com.olegsmirnov.codalinefinalproject.entities.WeatherData;
import com.olegsmirnov.codalinefinalproject.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;

public class ForecastActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String units = sharedPreferences.getString(getString(R.string.prefs_units), "metric");
        TextView tvUnits;
        ArrayList<WeatherData.WeatherList> list;
        if (getIntent().getParcelableArrayListExtra("LIST_AUTO") != null) {
            list = getIntent().getParcelableArrayListExtra("LIST_AUTO");
            setContentView(R.layout.activity_forecast_auto);
            toolbar = (Toolbar) findViewById(R.id.toolbar_recycler_auto);
            tvUnits = (TextView) findViewById(R.id.forecast_auto_units);
            tvUnits.setText(Utils.detectUnitsPostfix(units));
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view_auto);
            RecyclerView.Adapter adapter = new WeatherAdapterAuto(list, getApplicationContext());
            recyclerView.setAdapter(adapter);
        }
        else if (getIntent().getParcelableArrayListExtra("LIST_FISHING") != null) {
            list = getIntent().getParcelableArrayListExtra("LIST_FISHING");
            setContentView(R.layout.activity_forecast_fishing);
            toolbar = (Toolbar) findViewById(R.id.toolbar_recycler_fishing);
            tvUnits = (TextView) findViewById(R.id.forecast_fishing_units);
            tvUnits.setText(Utils.detectUnitsPostfix(units));
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view_fishing);
            RecyclerView.Adapter adapter = new WeatherAdapterFishing(list, getApplicationContext());
            recyclerView.setAdapter(adapter);
        }
        toolbar.setTitle(getIntent().getStringExtra("CITY"));
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}