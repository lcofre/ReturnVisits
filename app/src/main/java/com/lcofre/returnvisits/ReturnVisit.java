package com.lcofre.returnvisits;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;

public class ReturnVisit implements Serializable{
    public static final String CLASS_ID = ReturnVisit.class.getCanonicalName();

    public int hashCode;
    public double latitude;
    public double longitude;
    public String name;
    public String address;
    public String details;

    public Marker setDataInto(Marker marker) {
        marker.setPosition(new LatLng(latitude, longitude));
        marker.setTitle(getShortDescription());
        marker.setTag(this);

        if (marker.isInfoWindowShown())
            marker.showInfoWindow();

        return marker;
    }

    public ReturnVisit setPosition(LatLng position) {
        latitude = position.latitude;
        longitude = position.longitude;

        return this;
    }

    public Marker addMarkerTo(GoogleMap map) {
        Marker marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .draggable(true));

        return setDataInto(marker);
    }

    public String getShortDescription() {
        if(validString(name)) {
            return shorten(name);
        }

        if(validString(address)) {
            return shorten(address);
        }

        if(validString(details)) {
            return shorten(details);
        }

        return "";
    }

    private boolean validString(String s) {
        return s != null && !s.isEmpty();
    }

    private String shorten(String text) {
        return (!validString(text) || text.length() <= 10)? text : text.substring(0, 6) + "...";
    }
}