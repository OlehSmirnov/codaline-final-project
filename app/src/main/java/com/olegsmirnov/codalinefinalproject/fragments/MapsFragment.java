package com.olegsmirnov.codalinefinalproject.fragments;

import android.content.SharedPreferences;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.olegsmirnov.codalinefinalproject.R;
import com.olegsmirnov.codalinefinalproject.utils.Utils;
import com.olegsmirnov.codalinefinalproject.loaders.FetchWeatherDataFishing;

import java.io.IOException;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static MapView mapView;
    private static Marker myPosMarker;
    private Marker marker;
    private LatLng coords = Utils.DEFAULT_COORDS;

    private Snackbar snack;

    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        final Geocoder geocoder = new Geocoder(getContext());
        googleMap.setMinZoomPreference(8);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(coords));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (latLng != null) {
                    coords = latLng;
                    try {
                        if (marker != null) {
                            marker.remove(); // refresh marker
                        }
                        marker = googleMap.addMarker(new MarkerOptions().position(coords).title("Place, where you want to go fishing"));
                        marker.showInfoWindow();
                        final String location = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(1);
                        snack = Snackbar.make(getView(), location, Snackbar.LENGTH_INDEFINITE);
                        View mView = snack.getView();
                        TextView tv = (TextView) mView.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv.setTextSize(18);
                        snack.show();
                        snack.setAction("Show forecast", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new FetchWeatherDataFishing(getContext(), location,
                                        preferences.getString(getString(R.string.prefs_units), "metric")).execute(coords);
                            }
                        });
                    } catch (IOException e) {}
                }
            }
        });
    }

    public static MapView getMapView() {
        return mapView;
    }

    public static Marker getMyPosMarker() {
        return myPosMarker;
    }

}