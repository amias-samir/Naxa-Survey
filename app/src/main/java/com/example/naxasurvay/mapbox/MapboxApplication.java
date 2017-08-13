package com.example.naxasurvay.mapbox;

import android.app.Application;

import com.mapbox.mapboxsdk.Mapbox;

/**
 * Created by Samir on 8/13/2017.
 */

public class MapboxApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Mapbox Access token
        Mapbox.getInstance(getApplicationContext(), "sk.eyJ1IjoicGVhY2VuZXBhbCIsImEiOiJjajZhZDdpbGwxMW0yMnFsc3J6dDhqaTRhIn0.-6QCbw7vfpHyuIcNcXGByg");
    }
}
