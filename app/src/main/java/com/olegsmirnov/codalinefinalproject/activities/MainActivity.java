package com.olegsmirnov.codalinefinalproject.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.olegsmirnov.codalinefinalproject.R;
import com.olegsmirnov.codalinefinalproject.utils.Utils;
import com.olegsmirnov.codalinefinalproject.adapters.PagerAdapter;
import com.olegsmirnov.codalinefinalproject.fragments.MapsFragment;
import com.olegsmirnov.codalinefinalproject.loaders.FetchWeatherDataAuto;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;
    private Location mLastLocation;

    private SharedPreferences sharedPreferences;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.toolbarMain)
    Toolbar toolbar;

    @BindView(R.id.tablayout)
    TabLayout tabLayout;

    @BindView(R.id.main_layout)
    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        if (!Utils.areProvidersEnabled(this)) {
            Intent intent = new Intent(this, HelperActivity.class);
            startActivity(intent);
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        System.out.println(sharedPreferences.getString(getString(R.string.prefs_units), "metric"));
        ButterKnife.bind(this);
        mLocationRequest = LocationRequest.create().
                setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).
                setInterval(20000).
                setFastestInterval(1000);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).
                    addConnectionCallbacks(this).
                    addOnConnectionFailedListener(this).
                    addApi(LocationServices.API).build();
        }
        setSupportActionBar(toolbar);
        tabLayout.addTab(tabLayout.newTab().setText("Auto"));
        tabLayout.addTab(tabLayout.newTab().setText("Fishing"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.getBoolean(getString(R.string.prefs_light_style), true)) {
            tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreen));
        }
        else {
            tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
        if (tab.getPosition() == 1) {
            Utils.showLongSnack(getString(R.string.maps_message), findViewById(android.R.id.content));
            // Update location on map
            MapsFragment.getMapView().getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    LatLng coords = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    Marker marker = MapsFragment.getMyPosMarker();
                    marker = googleMap.addMarker(new MarkerOptions().position(coords).title("You are here!"));
                    marker.showInfoWindow();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(coords));
                }
            });
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.do_nothing);
                    }
                }, 600); // delay to prevert google map becomes black (Google bug)
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
            else {
                Intent intent = new Intent(this, HelperActivity.class);
                intent.putExtra("PERMISSIONS_GRANTED", false);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getLocation();
        if (Utils.areProvidersEnabled(this) && mLastLocation == null) {
            Intent intent = new Intent(this, HelperActivity.class);
            intent.putExtra("LOCATION_GOTTEN", false);
            startActivity(intent);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Utils.showLongSnack("Failed connection to google services", findViewById(android.R.id.content));
    }

    @Override
    public void onLocationChanged(Location location) {}

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient) != null) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            new FetchWeatherDataAuto(MainActivity.this, LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient),
                    sharedPreferences.getString(getString(R.string.prefs_units), "metric")).execute();
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                new FetchWeatherDataAuto(MainActivity.this, location,
                        sharedPreferences.getString(getString(R.string.prefs_units), "metric")).execute();
                mLastLocation = location;
            }
        });
    }

}