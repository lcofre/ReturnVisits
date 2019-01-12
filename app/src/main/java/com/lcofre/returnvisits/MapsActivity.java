package com.lcofre.returnvisits;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerDragListener {

    private static final int STATIC_INTEGER_VALUE = 1;
    private GoogleMap mMap;
    private MapWrapperLayout mMapWrapperLayout;
    private ViewGroup mInfoWindow;
    private OnTouchListener mEditListener;
    private OnTouchListener mDeleteListener;
    private HashMap<Integer, Marker> mMarkersHashMap = new HashMap<Integer, Marker>();
    private String defaultInfoMessage;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        defaultInfoMessage = getString(R.string.tap_to_add_details);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        mMap.setOnMarkerDragListener(this);

        mMapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
        mMapWrapperLayout.init(mMap, getPixelsFromDp(this, 39 + 20));

        this.mInfoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.options_popup, null);
        ImageButton editButton = (ImageButton) mInfoWindow.findViewById(R.id.edit);
        ImageButton deleteButton = (ImageButton) mInfoWindow.findViewById(R.id.delete);

        mEditListener = new OnTouchListener() {
            @Override
            public void onTouch(View view, Marker marker) {
                Intent intent = new Intent(MapsActivity.this, ReturnVisitDetailsActivity.class);
                ReturnVisit returnVisit = (ReturnVisit) marker.getTag();
                returnVisit.hashCode = marker.hashCode();
                intent.putExtra(ReturnVisit.CLASS_ID, returnVisit);
                startActivityForResult(intent, STATIC_INTEGER_VALUE);
            }
        };
        editButton.setOnTouchListener(mEditListener);

        mDeleteListener = new OnTouchListener() {
            @Override
            public void onTouch(View view, Marker marker) {
                mMarkersHashMap.remove(marker.hashCode());
                marker.remove();
            }
        };
        deleteButton.setOnTouchListener(mDeleteListener);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                mEditListener.setMarker(marker);
                mDeleteListener.setMarker(marker);

                mMapWrapperLayout.setMarkerWithInfoWindow(marker, mInfoWindow);
                return mInfoWindow;
            }
        });

        createMarkersFromDb();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        mMap.setMyLocationEnabled(true);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng whereIAm = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(whereIAm, 10));
                        }
                    }
                });
    }

    private static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    private void createMarkersFromDb() {
        List<ReturnVisit> returnVisitList = MarkerDataReaderDbHelper.getInstance(getApplicationContext()).readAllFromDb(defaultInfoMessage);
        for (ReturnVisit returnVisit : returnVisitList) {
            Marker marker = returnVisit.addMarkerTo(mMap);
            mMarkersHashMap.put(marker.hashCode(), marker);
        }
    }

    @Override
    public void onMapLongClick(LatLng here) {
        Marker marker = new ReturnVisit()
                .setPosition(here)
                .addMarkerTo(mMap);
        mMarkersHashMap.put(marker.hashCode(), marker);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (STATIC_INTEGER_VALUE) : {
                if (resultCode == Activity.RESULT_OK) {
                    ReturnVisit returnVisit = (ReturnVisit) data.getExtras().getSerializable(ReturnVisit.CLASS_ID);
                    returnVisit.setDataInto(mMarkersHashMap.get(returnVisit.hashCode));
                }
                break;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onPause() {
        super.onPause();

        MarkerDataReaderDbHelper markersDb = MarkerDataReaderDbHelper.getInstance(getApplicationContext());
        markersDb.deleteAll();

        for (Object o : mMarkersHashMap.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            Marker marker = (Marker) pair.getValue();
            markersDb.insert((ReturnVisit) marker.getTag());
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}