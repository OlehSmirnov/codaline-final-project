package com.olegsmirnov.codalinefinalproject.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.olegsmirnov.codalinefinalproject.R;
import com.olegsmirnov.codalinefinalproject.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HelperActivity extends AppCompatActivity {

    private LocationListener listener;
    private LocationManager manager;

    @BindView(R.id.tv_helper)
    TextView tvHelper;

    @BindView(R.id.button_helper)
    Button buttonHelper;

    @OnClick(R.id.button_helper)
    public void onClick() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);
        ButterKnife.bind(this);
        if (!getIntent().getBooleanExtra("LOCATION_GOTTEN", true)) {
            tvHelper.setText(getString(R.string.getting_location));
        }
        else if (!getIntent().getBooleanExtra("PERMISSIONS_GRANTED", true)) {
            buttonHelper.setVisibility(View.VISIBLE);
            tvHelper.setText(getString(R.string.request_user_permission));
        }
        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            public void onLocationChanged(Location location) {
                HelperActivity.super.onBackPressed();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {
                if (Utils.areProvidersEnabled(getApplicationContext())) {
                    tvHelper.setText(getString(R.string.getting_location));
                }
            }

            public void onProviderDisabled(String provider) {}
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity(); // to prevert user back to empty main activity
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.removeUpdates(listener);
    }
}
