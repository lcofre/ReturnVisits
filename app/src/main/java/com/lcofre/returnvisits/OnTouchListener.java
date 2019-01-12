package com.lcofre.returnvisits;

import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.maps.model.Marker;

public abstract class OnTouchListener implements View.OnTouchListener {
    Marker mMarker;

    public void setMarker(Marker marker) {
        mMarker = marker;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() ==
                MotionEvent.ACTION_DOWN) {
            onTouch(view, mMarker);
            return true;
        }
        return false;
    }

    public abstract void onTouch(View view, Marker marker);
}
