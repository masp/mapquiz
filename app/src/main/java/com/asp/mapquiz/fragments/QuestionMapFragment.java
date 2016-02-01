package com.asp.mapquiz.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;

public class QuestionMapFragment extends SupportMapFragment {

    public QuestionMapFragment() {
        super();
    }

    public static QuestionMapFragment newInstance() {
        return new QuestionMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View v = super.onCreateView(inflater, parent, bundle);
        initMap();
        return v;
    }

    private void initMap() {
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                UiSettings settings = map.getUiSettings();
                settings.setAllGesturesEnabled(false);
                settings.setZoomControlsEnabled(false);
                settings.setCompassEnabled(false);
                settings.setMapToolbarEnabled(false);
                settings.setIndoorLevelPickerEnabled(false);
                settings.setMyLocationButtonEnabled(false);
            }
        });
    }

}
